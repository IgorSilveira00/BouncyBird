package com.aor.bouncy.controller;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.Utilities;
import com.aor.bouncy.controller.entities.*;
import com.aor.bouncy.model.GameModel;
import com.aor.bouncy.model.entities.*;
import com.aor.bouncy.view.GameView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Controls the physics aspect of the game.
 */
public class GameController implements ContactListener{
    /**
     * The singleton instance of this controller.
     */
    private static GameController instance;

    /**
     * Game score.
     */
    private static int GAME_SCORE = 0;

    /**
     * The arena width in meters.
     */
    public static final int ROOM_WIDTH = 60;

    /**
     * The arena height in meters.
     */
    public static final int ROOM_HEIGHT = 45;

    /**
     * Horizontal speed.
     */
    private static float BIRD_X_SPEED = 0.3f;

    /**
     * The speed of the spikes.
     */
    private static final float SPIKE_SPEED = 20f;

    /**
     * Boolean to check if as inverted speed already.
     */
    private boolean hasTurned = false;

    /**
     * Boolean to check if spikes have grown already.
     */
    private boolean SPIKES_OUT = false;

    /**
     * The physics world controlled by this controller.
     */
    private final World world;

    /**
     * The bird's body.
     */
    private List<BirdBody> birdBodies = new ArrayList<BirdBody>();

    /**
     * List of the bodies in the right wall.
     */
    private List<Body> bodiesToMove = new ArrayList<Body>();

    /**
     * List containing last positions of the spikes.
     */
    private List<Body> bodiesToRemove =  new ArrayList<Body>();

    /**
     * Accumulator used to calculate the simulation step.
     */
    private float accumulator;

    private int DIFFICULTY_COUNTER = 0;

    private boolean readyToRemove = false;

    private boolean FX_ENABLED;

    private boolean END = false;

    /**
     * Time between bonus spawns.
     */
    private float TIME_BETWEEN_BONUS = 12;

    /**
     * Time left until next bonus pops up.
     */
    private float timeToNextBonus = TIME_BETWEEN_BONUS;

    /**
     * Creates a new GameController that controls the physics of a certain GameModel.
     */
    private GameController(boolean FX_ENABLED) {
        world = new World(new Vector2(0, 0), true);

        birdBodies.add(new BirdBody(world, GameModel.getInstance().getBird().get(0)));

        if (GameView.isTWO_PLAYERS())
            birdBodies.add(new BirdBody(world, GameModel.getInstance().getBird().get(1)));

        List<SpikeModel> floor_ceiling_spikes = GameModel.getInstance().getFloor_Ceiling_spikes();
        List<SpikeModel> right_wall_spikes = GameModel.getInstance().getRight_wall_spikes();
        List<SpikeModel> left_wall_spikes = GameModel.getInstance().getLeft_wall_spikes();
        List<EdgeModel> edges = GameModel.getInstance().getEdges();

        for (SpikeModel spike: floor_ceiling_spikes)
            new SpikeBody(world, spike);
        for (SpikeModel spike: right_wall_spikes)
            new SpikeBody(world, spike);
        for (SpikeModel spike: left_wall_spikes)
            new SpikeBody(world, spike);
        for (EdgeModel edge: edges)
            new EdgeBody(world, edge);

        world.setContactListener(this);
        world.setGravity(new Vector2(0, -30f));
        this.FX_ENABLED = FX_ENABLED;

        GAME_SCORE = 0;
        //getReady();
    }

    public void getReady() {
        boolean oneReady = false;
        boolean twoReady = false;

        if (!GameView.isTWO_PLAYERS()) twoReady = true;

        //TODO draw press readies

        while (!oneReady || !twoReady) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                oneReady = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                twoReady = true;
            }
        }
    }

    /**
     * Returns a singleton instance of a game controller.
     * @return the singleton instance.
     */
    public static GameController getInstance(boolean FX_ENABLED) {
        if (instance == null)
            instance = new GameController(FX_ENABLED);
        else
            instance.FX_ENABLED = FX_ENABLED;
        return instance;
    }

    /**
     * Calculates the next physics step of duration delta (in seconds)
     * @param delta The size of this physics step in seconds.
     */
    public boolean update(float delta) {
        generateBonus(delta);

        GameModel.getInstance().update(delta);

        if (!SPIKES_OUT)
            growSpikes(processAmount());

        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 6, 2);
            accumulator -= 1/60f;
        }

        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for (Body body: bodies) {
            //verifyBounds(body);
            ((EntityModel) body.getUserData()).setPosition(body.getPosition().x, body.getPosition().y);
        }

        birdBodies.get(0).setTransform(birdBodies.get(0).getX() + BIRD_X_SPEED, birdBodies.get(0).getY(), 0);

        if (birdBodies.size() > 1)
            birdBodies.get(1).setTransform(birdBodies.get(1).getX() - BIRD_X_SPEED, birdBodies.get(1).getY(), 0);

        if (readyToRemove && !world.isLocked())
            degrowSpikes();

        if (birdBodies.get(0).getX() > ROOM_WIDTH / 2 - 1
                & birdBodies.get(0).getX() < ROOM_WIDTH / 2 + 1)
            hasTurned = false;

        return END;
    }

    private int processAmount() {
        int res = 0;
        if (DIFFICULTY_COUNTER % 2 == 0)
            res = DIFFICULTY_COUNTER / 2;
        res = (int)Math.ceil(DIFFICULTY_COUNTER / 2.0);

        if (res < GameModel.AMOUNT_SPIKES)
            return res;
        return GameModel.AMOUNT_SPIKES - 1;  //at least one space must be available
    }

    /**
     * Makes the spikes grow on the correct wall.
     *
     * @param amount amount of total spikes to move in the wall
     */
    private void growSpikes(int amount) {
        float multiplier = 1;
        if (BIRD_X_SPEED > 0)
            multiplier *= -1;

        bodiesToMove.clear();
        bodiesToRemove.clear();
        getCorrectSpikeBodies();

        int[] spikesIndexes = Utilities.getDistinctRandomNumbers(processAmount(), GameModel.AMOUNT_SPIKES);

        if (GameView.isTWO_PLAYERS()) {
            for (Integer index: spikesIndexes) {
                bodiesToMove.get(index).setTransform(bodiesToMove.get(index).getPosition().x +
                                (((SpikeModel) bodiesToMove.get(index).getUserData()).getType() == EntityModel.ModelType.RIGHT_SPIKE ? - GameModel.SPIKE_HEIGHT: GameModel.SPIKE_HEIGHT) ,
                        bodiesToMove.get(index).getPosition().y,
                        bodiesToMove.get(index).getAngle());
                bodiesToRemove.add(bodiesToMove.get(index));
            }
        } else {
            for (Integer index: spikesIndexes) {
                bodiesToMove.get(index).setTransform(bodiesToMove.get(index).getPosition().x + multiplier * GameModel.SPIKE_HEIGHT,
                        bodiesToMove.get(index).getPosition().y,
                        bodiesToMove.get(index).getAngle());
                bodiesToRemove.add(bodiesToMove.get(index));
            }
        }

        SPIKES_OUT = true;
    }

    /**
     * Sets up the array of bodies to be moved.
     */
    private void getCorrectSpikeBodies() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        if (GameView.isTWO_PLAYERS()) {
            for (Body body : bodies)
                if (body.getUserData() instanceof SpikeModel)
                    if (((SpikeModel) body.getUserData()).getType() == EntityModel.ModelType.RIGHT_SPIKE
                        || ((SpikeModel) body.getUserData()).getType() == EntityModel.ModelType.LEFT_SPIKE)
                        bodiesToMove.add(body);
        } else {
            if (BIRD_X_SPEED > 0) {  //bird is moving right
                for (Body body : bodies) {
                    if (body.getUserData() instanceof SpikeModel)
                        if (((SpikeModel) body.getUserData()).getType() == EntityModel.ModelType.RIGHT_SPIKE)
                            bodiesToMove.add(body);
                }
            } else        //bird is moving left
                for (Body body : bodies) {
                    if (body.getUserData() instanceof SpikeModel)
                        if (((SpikeModel) body.getUserData()).getType() == EntityModel.ModelType.LEFT_SPIKE)
                            bodiesToMove.add(body);
                }
        }
    }

    /**
     * Verifies if the body is inside the room bounds.
     * @param body
     */
    private void verifyBounds(Body body) {
        if (body.getPosition().x < 0)
            body.setTransform(0, body.getPosition().y, body.getAngle());
        if (body.getPosition().y < 0)
            body.setTransform(body.getPosition().x, 0, body.getAngle());
        if (body.getPosition().x > ROOM_WIDTH)
            body.setTransform(ROOM_WIDTH, body.getPosition().y, body.getAngle());
        if (body.getPosition().y > ROOM_HEIGHT)
            body.setTransform(body.getPosition().x, ROOM_HEIGHT, body.getAngle());
    }

    /**
     * Returns the world controlled by this controller.
     * @return The world controlled by this controller.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Makes the bird jump.
     */
    public void jump(int index) {
        //TODO
        birdBodies.get(index).applyForceToCenter(0, 3000f, true);
    }

    /**
     * Spawns a new bonus in the room.
     */
    public void generateBonus(float delta) {
        if (timeToNextBonus < 0) {
            BonusModel bonus = GameModel.getInstance().createBonus();
            BonusBody body = new BonusBody(world, bonus);
            timeToNextBonus = TIME_BETWEEN_BONUS;
        }
        else
            timeToNextBonus -= delta;
    }

    /**
     * Returns the current game score.
     * @return the game score.
     */
    public static int getGameScore() {
        return GAME_SCORE;
    }

    /**
     * A contact between two objects was detected.
     * @param contact the detected contact
     */
    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        if (bodyA.getUserData() instanceof BonusModel)
            bonusCollision(bodyA);
        if (bodyB.getUserData() instanceof BonusModel)
            bonusCollision(bodyB);

        if (bodyA.getUserData() instanceof BirdModel && bodyB.getUserData() instanceof SpikeModel)
            birdSpikeCollision(bodyB);
        if (bodyA.getUserData() instanceof SpikeModel && bodyB.getUserData() instanceof BirdModel)
            birdSpikeCollision(bodyA);

        if (bodyA.getUserData() instanceof BirdModel && bodyB.getUserData() instanceof EdgeModel)
            birdEdgeCollision();

        if (bodyA.getUserData() instanceof EdgeModel && bodyB.getUserData() instanceof BirdModel)
            birdEdgeCollision();
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    //TODO game over
    private void birdSpikeCollision(Body bodyA) {
        END = false;
    }

    /**
     * The bonus collided with something (probably the bird),
     * lets remove it.
     * @param body the bonus that collided
     */
    private void bonusCollision(Body body) {
        ((BonusModel) body.getUserData()).setFlaggedForRemoval(true);
        GAME_SCORE += 1;
    }

    /**
     * The bird collided with one of the edges.
     */
    private void birdEdgeCollision() {
        if (!hasTurned) {
            BIRD_X_SPEED *= -1;
            GAME_SCORE += 1;
            hasTurned = true;
            if (FX_ENABLED)
                GameView.playHit();
            DIFFICULTY_COUNTER++;
        }
        readyToRemove = true;
    }

    /**
     * Removes grown spikes. They go back in.
     */
    private void degrowSpikes() {
        for (int i = 0; i < bodiesToRemove.size(); i++)
            bodiesToRemove.get(i).setTransform(bodiesToRemove.get(i).getPosition().x +
                            (((SpikeModel) bodiesToMove.get(i).getUserData()).getType() == EntityModel.ModelType.RIGHT_SPIKE ? GameModel.SPIKE_HEIGHT: - GameModel.SPIKE_HEIGHT),
                    bodiesToRemove.get(i).getPosition().y,
                    bodiesToRemove.get(i).getAngle());

        SPIKES_OUT = false;
        readyToRemove = false;
    }

    /**
     * Removes objects that have been flagged for removal on the previous step.
     */
    public void removeFlagged() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body body: bodies) {
            if (((EntityModel) body.getUserData()).isFlaggedForRemoval()) {
                GameModel.getInstance().remove((EntityModel) body.getUserData());
                world.destroyBody(body);
            }
        }
    }
}

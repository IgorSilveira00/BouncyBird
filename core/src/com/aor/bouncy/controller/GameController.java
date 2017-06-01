package com.aor.bouncy.controller;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.Utilities;
import com.aor.bouncy.controller.entities.*;
import com.aor.bouncy.model.GameModel;
import com.aor.bouncy.model.entities.*;
import com.aor.bouncy.view.GameView;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the physics aspect of the game.
 */
public class GameController implements ContactListener{
    /**
     * The singleton instance of this controller.
     */
    private static GameController instance;

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
    private static List<BirdBody> birdBodies = new ArrayList<BirdBody>();/**

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

    /**
     * Corrects gaps between spikes and edges.
     */
    public static final float corrector = 1f;

    /**
     * Variable used to compute the amount of spikes to be grown.
     */
    private int DIFFICULTY_COUNTER = 1;

    /**
     * Variable used to tell if the already grown spikes are ready for removal.
     */
    private boolean readyToRemove = false;

    /**
     * Used by the GameView to tell if the game
     * as ended upon spike collision.
     */
    private boolean END = false;

    /**
     * Prevents multiple spikes collision at a time.
     */
    private boolean hasDecreasedLife = false;

    /**
     * Tells if the spike are ready to be grown using the bird's position.
     */
    private boolean readyToGrow = true;

    /**
     * Amount used to make the bird jump.
     */
    private final float UPWARD_SPEED = 0.6f;

    /**
     * Amount used to make the bird fall.
     */
    private final float GRAVITY = -0.03f;

    /**
     * Vertical speed added for each bird, for movement.
     */
    private float speedBirdOne = 0, speedBirdTwo = 0;

    /**
     * Boolean used to check if it is time to jump.
     */
    private boolean isJump = false;

    /**
     * Index of the bird who is to perform the jumping action.
     */
    private int birdToJumpIndex = 0;

    /**
     * Time between bonus spawns.
     */
    private float TIME_BETWEEN_BONUS = 10;

    /**
     * Time left until next bonus pops up.
     */
    private float timeToNextBonus = TIME_BETWEEN_BONUS;

    /**
     * Used by the GameView to when when the bird has hit the wall to play the FX.
     */
    private boolean toPlaySound = false;

    /**
     * Used to disable the vertical changes of the bird. (for testings purposes)
     */
    private boolean isJumpEnabled = true;

    /**
     * Used to disable game over. (for testing purposes)
     */
    private boolean isLosingEnabled = true;

    /**
     * Know when bonus colision happens. (for testing purposes)
     */
    private boolean bonusCollided = false;

    /**
     * Creates a new GameController that controls the physics of a certain GameModel.
     */
    public GameController() {

        world = new World(new Vector2(0, 0), true);

        birdBodies.add(new BirdBody(world, GameModel.getInstance().getBird().get(0)));
        ((BirdModel) birdBodies.get(0).getUserData()).setFlying(true);
        ((BirdModel) birdBodies.get(0).getUserData()).setHeadRight(true);

        if (GameView.isTWO_PLAYERS()) {
            birdBodies.add(new BirdBody(world, GameModel.getInstance().getBird().get(1)));
            ((BirdModel) birdBodies.get(1).getUserData()).setFlying(true);
            ((BirdModel) birdBodies.get(1).getUserData()).setHeadRight(false);
        }

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

        BIRD_X_SPEED = 0.3f;
    }

    /**
     * Returns a singleton instance of a game controller.
     * @return the singleton instance.
     */
    public static GameController getInstance() {
        if (instance == null)
            instance = new GameController();
        return instance;
    }

    /**
     * Calculates the next physics step of duration delta (in seconds)
     * @param delta The size of this physics step in seconds.
     */
    public boolean update(float delta) {
        toPlaySound = false;
        bonusCollided = false;

        //Bonus is only spawn in one player game mode.
        if (!GameView.isTWO_PLAYERS())
            generateBonus(delta);

        GameModel.getInstance().update(delta);

        //Prevents spikes from growing while the birds are in range of the motion.
        if (BIRD_X_SPEED > 0) {
            if (birdBodies.get(0).getX() > GameView.VIEWPORT_WIDTH / 4f)
                readyToGrow = true;
            else
                readyToGrow = false;
        }
        else {
            if (birdBodies.get(0).getX() < GameView.VIEWPORT_WIDTH - GameView.VIEWPORT_WIDTH / 4f)
                readyToGrow = true;
            else
                readyToGrow = false;
        }

        if (!SPIKES_OUT && readyToGrow)
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
            if (body.getUserData() instanceof BirdModel)
                verifyBounds(body);
            ((EntityModel) body.getUserData()).setPosition(body.getPosition().x, body.getPosition().y);
        }

       if (isJumpEnabled)
           processJump(birdToJumpIndex);

        moveBodies("0;" + Float.toString(birdBodies.get(0).getX() + BIRD_X_SPEED) + ";" + Float.toString(birdBodies.get(0).getY()));

        if (birdBodies.size() > 1)
            moveBodies("1;" + Float.toString(birdBodies.get(1).getX() - BIRD_X_SPEED) + ";" + Float.toString(birdBodies.get(1).getY()));


        if (readyToRemove && !world.isLocked())
            degrowSpikes();

        if (birdBodies.get(0).getX() > ROOM_WIDTH / 2 - 1
                & birdBodies.get(0).getX() < ROOM_WIDTH / 2 + 1)
            hasTurned = false;

        return END;
    }

    /**
     * A contact between two objects was detected.
     * @param contact the detected contact
     */
    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
    /*    System.out.println("1: " + bodyA.getUserData());
        System.out.println("2: " + bodyB.getUserData());*/

        if (bodyA.getUserData() instanceof BonusModel)
            bonusCollision(bodyA);
        if (bodyB.getUserData() instanceof BonusModel)
            bonusCollision(bodyB);

        if (bodyA.getUserData() instanceof BirdModel && bodyB.getUserData() instanceof SpikeModel)
            birdSpikeCollision(bodyB, bodyA);
        if (bodyA.getUserData() instanceof SpikeModel && bodyB.getUserData() instanceof BirdModel)
            birdSpikeCollision(bodyA, bodyB);

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

    public boolean isToPlaySound() {
        return toPlaySound;
    }

    /**
     * Computes the amount of spikes to be grown.
     * @return the amount of spikes to be grown.
     */
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
        if (bodiesToMove.size() > 0 || bodiesToRemove.size() > 0);
        else {

            Array<Body> bodies = new Array<Body>();
            world.getBodies(bodies);

            float multiplier = 1;

            //If the bird if moving left the amount of x increment is simetric
            if (BIRD_X_SPEED > 0)
                multiplier *= -1;

            bodiesToMove.clear();
            bodiesToRemove.clear();

            getCorrectSpikeBodies();

            //Chooses which spikes will be grown.
            int[] spikesIndexes = Utilities.getDistinctRandomNumbers(amount, GameModel.AMOUNT_SPIKES - 2);

            if (GameView.isTWO_PLAYERS()) {
                for (Integer index : spikesIndexes) {
                    bodiesToMove.get(index).setTransform((((SpikeModel) bodiesToMove.get(index).getUserData()).getType() == EntityModel.ModelType.RIGHT_SPIKE ? GameView.VIEWPORT_WIDTH + 1.2f + GameController.corrector : - 1.2f - GameController.corrector) + 1.28f * corrector *
                                    (((SpikeModel) bodiesToMove.get(index).getUserData()).getType() == EntityModel.ModelType.RIGHT_SPIKE ? -GameModel.SPIKE_HEIGHT : GameModel.SPIKE_HEIGHT),
                            bodiesToMove.get(index).getPosition().y,
                            bodiesToMove.get(index).getAngle());
                    bodiesToRemove.add(bodiesToMove.get(index));
                }
            } else {
                for (Integer index : spikesIndexes) {
                    bodiesToMove.get(index).setTransform(bodiesToMove.get(index).getPosition().x + multiplier * 1.28f * corrector * GameModel.SPIKE_HEIGHT,
                            bodiesToMove.get(index).getPosition().y,
                            bodiesToMove.get(index).getAngle());
                    bodiesToRemove.add(bodiesToMove.get(index));
                }
            }

            readyToGrow = false;
            SPIKES_OUT = true;
        }
    }

    /**
     * Moves the bodies. (is using a string because it was to be implemented with servers).
     * @param positions string containing all the information to perform the move.
     */
    public void moveBodies(String positions) {
        int which;
        float x, y;
        String[] values = positions.split(";");
        which = Integer.parseInt(values[0]);
        x = Float.parseFloat(values[1]);
        y = Float.parseFloat(values[2]);

        birdBodies.get(which).setTransform(x, y, 0);
    }

    /**
     * Sets up the array of bodies that can be moved.
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
        isJump = true;
        birdToJumpIndex = index;
    }

    /**
     * Sets the jumping enabled flag.
     * @param jumpEnabled
     */
    public void setJumpEnabled(boolean jumpEnabled) {
        isJumpEnabled = jumpEnabled;
    }

    /**
     * Sets the losing enabled flag.
     * @param losingEnabled
     */
    public void setLosingEnabled(boolean losingEnabled) {
        isLosingEnabled = losingEnabled;
    }

    /**
     * Used to detect bird's position for testing purposes.
     * @return
     */
    public static float getBirdXSpeed() {
        return BIRD_X_SPEED;
    }

    /**
     * Used to know if bird has hit the bonus, for testing purposes.
     * @return
     */
    public boolean isBonusCollided() {
        return bonusCollided;
    }

    /**
     * Checks to see if the jump flag was activated and makes the bird jump.
     */
    private void processJump(int index) {
        if(isJump)
        {
            if (index == 0)
                speedBirdOne = UPWARD_SPEED;
            else
                speedBirdTwo = UPWARD_SPEED; //negative relative to the normal gravity
            isJump = false; //once a jump is activated, we disable the flag
        }
        speedBirdOne += (GRAVITY); //physics ya, the speed is affected by gravity
        speedBirdTwo += (GRAVITY); //physics ya, the speed is affected by gravity

        moveBodies("0;" + Float.toString(birdBodies.get(0).getX()) + ";" + Float.toString(birdBodies.get(0).getY() + speedBirdOne));

        if (GameView.isTWO_PLAYERS())
            moveBodies("1;" + Float.toString(birdBodies.get(1).getX()) + ";" + Float.toString(birdBodies.get(1).getY() + speedBirdTwo));
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
     * Handles collision between bird and spike.
     * @param bodyA spike which the bird has collided with.
     * @param bodyB bird that has collided.
     */
    private void birdSpikeCollision(Body bodyA, Body bodyB) {
        ((SpikeModel) bodyA.getUserData()).setNormalTexture(false);
        if (!hasDecreasedLife) {
            if (((BirdModel) bodyB.getUserData()).isSecond())
                MyBouncyBird.setPLAYER_TWO_LIVES(MyBouncyBird.getPLAYER_TWO_LIVES() - 1);
            else
                MyBouncyBird.setPLAYER_ONE_LIVES(MyBouncyBird.getPLAYER_ONE_LIVES() - 1);
            hasDecreasedLife = true;
        }
        System.out.println("IS DEAD");
        if (isLosingEnabled)
            END = true;
    }

    /**
     * The bonus collided with something (probably the bird),
     * lets remove it.
     * @param bonusBody the bonus that collided
     */
    private void bonusCollision(Body bonusBody) {
        ((BonusModel) bonusBody.getUserData()).setFlaggedForRemoval(true);
        GameModel.getInstance().incScore();
        bonusCollided = true;
    }

    /**
     * The bird collided with one of the edges.
     */
    private void birdEdgeCollision() {
        if (!hasTurned) {
            BIRD_X_SPEED *= -1;

            //invert birds textures
            for (BirdBody body: birdBodies)
                ((BirdModel) body.getUserData()).setHeadRight(! ((BirdModel) body.getUserData()).isHeadRight());

            GameModel.getInstance().incScore();
            hasTurned = true;
            toPlaySound = true;
            DIFFICULTY_COUNTER++;
        }
        readyToRemove = true;
        readyToGrow = false;
    }

    /**
     * Removes grown spikes. They go back in.
     */
    private void degrowSpikes() {


        for (int i = 0; i < bodiesToRemove.size(); i++)
            bodiesToRemove.get(i).setTransform((((SpikeModel) bodiesToMove.get(i).getUserData()).getType() == EntityModel.ModelType.RIGHT_SPIKE ? GameView.VIEWPORT_WIDTH + 1.2f + GameController.corrector : - 1.2f - GameController.corrector),
                    bodiesToRemove.get(i).getPosition().y,
                    bodiesToRemove.get(i).getAngle());
        bodiesToMove.clear();
        bodiesToRemove.clear();

        SPIKES_OUT = false;
        readyToRemove = false;
        readyToGrow = true;
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

    /**
     * Used to reset.
     */
    public void dispose() {
        birdBodies.clear();
        instance = null;
    }

    /**
     * Gets the list of the bird bodies in game.
     * @return a list containg the bird bodies.
     */
    public static List<BirdBody> getBirdBodies() {
        return birdBodies;
    }
}

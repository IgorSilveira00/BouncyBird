package com.aor.bouncy.model;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.entities.*;
import com.aor.bouncy.view.GameView;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

/**
 * A model representing a game.
 */
public class GameModel {
    /**
     * The singleton instance of the game model.
     */
    private static GameModel instance;

    /**
     * Total number of spikes in the game.
     */
    public static final int AMOUNT_SPIKES = 12;

    /**
     * The spikes height. The amount that pops visible.
     */
    public static final float SPIKE_HEIGHT = 4f;

    /**
     * The birds controlled by the users in this game.
     */
    private List<BirdModel> birds = new ArrayList<BirdModel>();

    /**
     * Amount in seconds that a bonus lasts.
     */
    private static float TIME_ALIVE = 8;

    /**
     * The spikes present in the game's room's floor.
     */
    private List<SpikeModel> floor_ceiling_spikes;

    /**
     * The spikes present in the game's room's left wall.
     */
    private List<SpikeModel> left_wall_spikes;

    /**
     * The spikes present in the game's room's right wall.
     */
    private List<SpikeModel> right_wall_spikes;

    private int GAME_SCORE = 0;

    /**
     * The edges of the game.
     */
    private List<EdgeModel> edges;

    /**
     * The bonus in the room.
     */
    private BonusModel bonus;

    /**
     * Used to create bonus on middle of screen only. (for testing purposes)
     */
    private boolean isBonusStill = false;

    /**
     * Returns a singleton instance of the game model.
     * @return the singleton instance
     */
    public static GameModel getInstance() {
        if (instance == null)
            instance = new GameModel();
        return instance;
    }

    /**
     * Constructs a game with a bird in the middle of the room,
     * all wall spikes hidden and floor and ceiling ones shown,
     * no bonus.
     */
    private GameModel() {
        floor_ceiling_spikes = new ArrayList<SpikeModel>();
        right_wall_spikes = new ArrayList<SpikeModel>();
        left_wall_spikes = new ArrayList<SpikeModel>();
        edges = new ArrayList<EdgeModel>();

        birds.add(new BirdModel(GameController.ROOM_WIDTH / 2,
                GameController.ROOM_HEIGHT / 2,
                0));


        birds.get(0).setNUMBER_LIVES(MyBouncyBird.getPLAYER_ONE_LIVES());
        birds.get(0).setHeadRight(true);

        //If the game is two players mode, a new bird is created.
        if (GameView.isTWO_PLAYERS()) {
            birds.add(new BirdModel(GameController.ROOM_WIDTH / 2,
                    GameController.ROOM_HEIGHT / 2 - 100 * GameView.PIXEL_TO_METER,
                    0));
            birds.get(1).setNUMBER_LIVES(MyBouncyBird.getPLAYER_TWO_LIVES());
            birds.get(1).setSecond(true);
        }

        for (int i = 0; i < birds.size(); i++)
            birds.get(i).setFlying(false);

        //Floor and ceiling spikes.
        for (int i = 0; i < AMOUNT_SPIKES; i++){
            floor_ceiling_spikes.add(new SpikeModel(2 * SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    SPIKE_HEIGHT - GameController.corrector,
                    (float) Math.PI / 2,
                    EntityModel.ModelType.SPIKE));

            floor_ceiling_spikes.add(new SpikeModel(2 * SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    GameView.VIEWPORT_HEIGHT - SPIKE_HEIGHT + GameController.corrector,
                    - (float) Math.PI / 2,
                    EntityModel.ModelType.SPIKE));
        }

        //Right and left walls spikes.
        for (int i = 0; i < AMOUNT_SPIKES - 2 ; i++) {
            right_wall_spikes.add(new SpikeModel(GameController.ROOM_WIDTH + 1.2f + GameController.corrector,
                    SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    (float) Math.PI,
                    EntityModel.ModelType.RIGHT_SPIKE));

            left_wall_spikes.add(new SpikeModel(- 1.2f - GameController.corrector,
                    SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    0,
                    EntityModel.ModelType.LEFT_SPIKE));
        }

        // bottom edge
        edges.add(new EdgeModel(0, 0, 0));

        //top edge
        edges.add(new EdgeModel(0, GameView.VIEWPORT_HEIGHT, 0));

        // left edge
        edges.add(new EdgeModel(0, 0, (float) Math.PI / 2));

        //right edge
        edges.add(new EdgeModel(GameController.ROOM_WIDTH, GameController.ROOM_HEIGHT , (float) Math.PI / 2 ));
    }

    /**
     * Returns the player's bird.
     * @return the bird.
     */
    public List<BirdModel> getBird() {
        return birds;
    }

    /**
     * Returns the floor's spikes.
     * @return the floor's spikes list.
     */
    public List<SpikeModel> getFloor_Ceiling_spikes() {
        return floor_ceiling_spikes;
    }

    /**
     * Returns the left wall's spikes.
     * @return the left wall's spikes list.
     */
    public List<SpikeModel> getLeft_wall_spikes() {
        return left_wall_spikes;
    }

    /**
     * Returns the right wall's spikes.
     * @return the right wall's spikes list.
     */
    public List<SpikeModel> getRight_wall_spikes() {
        return right_wall_spikes;
    }

    /**
     * Returns the edges.
     * @return the edges list.
     */
    public List<EdgeModel> getEdges() { return edges;}

    /**
     * Returns the bonus.
     * @return the bonus.
     */
    public BonusModel getBonus() {
        return bonus;
    }

    /**
     * Creates the bonus in the game.
     * @return the bonus.
     */
    public BonusModel createBonus() {
        if (isBonusStill) {
            TIME_ALIVE = 1000;
            bonus = new BonusModel(GameController.ROOM_WIDTH / 2f, GameController.ROOM_HEIGHT / 2f);
        }
        else
            bonus = new BonusModel(SPIKE_HEIGHT);

        bonus.setFlaggedForRemoval(false);
        bonus.setTimeToLive(TIME_ALIVE);

        return bonus;
    }

    /**
     * Removes a model from this game.
     * @param model the model to be removed
     */
    public void remove(EntityModel model){
        if (model instanceof BonusModel){
            bonus = null;
        }
    }

    /**
     * Update the bodies.
     * @param delta time passed in seconds since last update.
     */
    public void update(float delta) {
        if (bonus != null)
            if (bonus.decreaseTimeToLive(delta))
                bonus.setFlaggedForRemoval(true);
    }

    /**
     * Used to reset the class.
     */
    public void dispose() {
        instance = null;
    }

    /**
     * Returns the current game score.
     * @return the current game score.
     *
     */
    public int getGAME_SCORE() {
        return GAME_SCORE;
    }

    /**
     * Updates the current game score.
     */
    public void incScore() {
        GAME_SCORE++;
    }

    public void reset() {
        birds.clear();
        birds.add(new BirdModel(GameController.ROOM_WIDTH / 2,
                GameController.ROOM_HEIGHT / 2,
                0));


        birds.get(0).setNUMBER_LIVES(MyBouncyBird.getPLAYER_ONE_LIVES());
        birds.get(0).setHeadRight(true);
    }

    /**
     * Used for testing purposes.
     * @param bonusStill true if bonus is to be created on the center of the screen.
     */
    public void setBonusStill(boolean bonusStill) {
        isBonusStill = bonusStill;
    }
}

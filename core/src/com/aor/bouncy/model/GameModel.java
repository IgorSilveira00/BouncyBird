package com.aor.bouncy.model;

import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.entities.*;
import com.aor.bouncy.view.GameView;

import java.lang.reflect.Array;
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
     * The list of the objects who display the lives.
     */
    private List<LifeModel> lifes = new ArrayList<LifeModel>();

    /**
     * Amount in seconds that a bonus lasts.
     */
    private static final float TIME_ALIVE = 8;

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

       birds.add(new BirdModel(GameView.VIEWPORT_WIDTH / 2,
                GameView.VIEWPORT_HEIGHT / 2,
                0));
       birds.get(0).setHeadRight(true);

        if (GameView.isTWO_PLAYERS()) {
            birds.add(new BirdModel(GameView.VIEWPORT_WIDTH / 2,
                    GameView.VIEWPORT_HEIGHT / 2 - 100 * GameView.PIXEL_TO_METER,
                    0));
            birds.get(1).setSecond(true);
        }

        for (int i = 0; i < birds.size(); i++)
            birds.get(i).setFlying(false);

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

        for (int i = 0; i < AMOUNT_SPIKES - 2 ; i++) {
            right_wall_spikes.add(new SpikeModel(GameView.VIEWPORT_WIDTH + 1,
                    SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    (float) Math.PI,
                    EntityModel.ModelType.RIGHT_SPIKE));

            left_wall_spikes.add(new SpikeModel(-1,
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
        edges.add(new EdgeModel(GameView.VIEWPORT_WIDTH, GameView.VIEWPORT_HEIGHT , (float) Math.PI / 2 ));

        /*if (GameView.isTWO_PLAYERS()) {
            lifes.add(new LifeModel(SPIKE_HEIGHT,
                    GameView.VIEWPORT_HEIGHT - SPIKE_HEIGHT / 2, 0));
            lifes.add(new LifeModel(GameView.VIEWPORT_WIDTH - SPIKE_HEIGHT,
                    GameView.VIEWPORT_HEIGHT - SPIKE_HEIGHT / 2, 0));
        }*/
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
     * Returns the lifes models.
     * @return the lifes list.
     */
    public List<LifeModel> getLifes() { return lifes;}

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

    public void update(float delta) {
        if (bonus != null)
            if (bonus.decreaseTimeToLive(delta))
                bonus.setFlaggedForRemoval(true);
    }

    public static void dispose() {
        instance = null;
    }

    public int getGAME_SCORE() {
        return GAME_SCORE;
    }

    public void incScore() {
        GAME_SCORE++;
    }
}

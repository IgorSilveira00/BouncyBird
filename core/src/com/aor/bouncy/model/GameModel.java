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
    public static final int AMOUNT_SPIKES = 10;

    /**
     * The spikes height. The amount that pops visible.
     */
    public static final float SPIKE_HEIGHT = 4.5f;

    /**
     * The bird controlled by the user in this game.
     */
    private List<BirdModel> bird = new ArrayList<BirdModel>();

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

       bird.add(new BirdModel(GameController.ROOM_WIDTH / 2,
                GameController.ROOM_HEIGHT / 2,
                0));

        if (GameView.isTWO_PLAYERS())
            bird.add(new BirdModel(GameController.ROOM_WIDTH / 2,
                    GameController.ROOM_HEIGHT / 2 - 100 * GameView.PIXEL_TO_METER,
                    0));

        for (int i = 0; i < AMOUNT_SPIKES; i++){
            floor_ceiling_spikes.add(new SpikeModel(2 * SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    SPIKE_HEIGHT - GameController.corrector,
                    (float) Math.PI / 2,
                    EntityModel.ModelType.SPIKE));

            floor_ceiling_spikes.add(new SpikeModel(2 * SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    GameController.ROOM_HEIGHT - SPIKE_HEIGHT + GameController.corrector,
                    - (float) Math.PI / 2,
                    EntityModel.ModelType.SPIKE));

            right_wall_spikes.add(new SpikeModel(GameController.ROOM_WIDTH,
                    SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    (float) Math.PI,
                    EntityModel.ModelType.RIGHT_SPIKE));

            left_wall_spikes.add(new SpikeModel(0,
                    SPIKE_HEIGHT + SPIKE_HEIGHT * i,
                    0,
                    EntityModel.ModelType.LEFT_SPIKE));
        }

        // bottom edge
        edges.add(new EdgeModel(0, 1, 0));

        //top edge
        edges.add(new EdgeModel(0, GameController.ROOM_HEIGHT - 1, 0));

        // left edge
        edges.add(new EdgeModel(1f, 0, (float) Math.PI / 2));

        //right edge
        edges.add(new EdgeModel(GameController.ROOM_WIDTH - 1f, GameController.ROOM_HEIGHT - 1 , (float) Math.PI / 2 ));
    }

    /**
     * Returns the player's bird.
     * @return the bird.
     */
    public List<BirdModel> getBird() {
        return bird;
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

    public void reset() {
        instance = new GameModel();
    }
}

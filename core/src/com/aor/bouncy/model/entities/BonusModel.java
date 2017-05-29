package com.aor.bouncy.model.entities;

import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.view.GameView;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * A model representing the bonus.
 */
public class BonusModel extends EntityModel{
    /**
     * Current time left for the model to live.
     */
    public static float timeToLive;

    /**
     * Constructs a bonus model belonging to a game's model.
     * @param SPIKE_HEIGHT the game's spike's height.
     */
    public BonusModel(float SPIKE_HEIGHT) {
        super(random(SPIKE_HEIGHT, GameView.VIEWPORT_WIDTH - 2 * SPIKE_HEIGHT),
                random(SPIKE_HEIGHT, GameView.VIEWPORT_HEIGHT - 2 * SPIKE_HEIGHT),
                0);
    }

    /**
     * Decreases the bonus's time to live by delta seconds
     * @param delta
     * @return
     */
    public boolean decreaseTimeToLive(float delta) {
        timeToLive -= delta;
        return timeToLive < 0;
    }

    /**
     * Sets the bonus' time to live in seconds
     * @param timeToLive seconds
     */
    public void setTimeToLive(float timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public ModelType getType() {
        return ModelType.BONUS;
    }
}

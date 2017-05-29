package com.aor.bouncy.model.entities;

/**
 * A model representing the user's bird.
 */
public class BirdModel extends EntityModel {
    /**
     * Is the bird currently flying.
     */
    private boolean flying;

    /**
     * Is the bird heading right.
     */
    private boolean headRight;

    /**
     * Is the bird the BLUE player.
     */
    private boolean isSecond = false;

    /**
     * Current number of lives of the bird.
     */
    private int NUMBER_LIVES = 3;

    /**
     * Creates a new bird model in a certain position.
     * @param x The x-coordinate of this bird in meters.
     * @param y The y-coordinate of this bird in meters.
     */
    public BirdModel(float x, float y, float angle) {
        super(x, y, angle);
        flying = false;
    }

    @Override
    public ModelType getType() {
        return ModelType.BIRD;
    }

    /**
     * Sets the flying flag for the bird.
     * @param flying true if flying, false otherwise.
     */
    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    /**
     * Returns if the bird is flying.
     * @return true if flying, false otherwise.
     */
    public boolean isFlying() {
        return flying;
    }

    /**
     * Sets the heading right flag of the bird.
     * @param headRight true if headed right, false otherwise.
     */
    public void setHeadRight(boolean headRight) {
        this.headRight = headRight;
    }

    /**
     * Returns if the bird is headed right.
     * @return true if is headed right, false otherwise.
     */
    public boolean isHeadRight() {
        return headRight;
    }

    /**
     * Returns the current number of the bird's lives.
     * @return current bird's amount of lives.
     */
    public int getNUMBER_LIVES() {
        return NUMBER_LIVES;
    }

    /**
     * Sets the is BLUE player flag of the bird.
     * @param second true if is the BLUE player.
     */
    public void setSecond(boolean second) {
        isSecond = second;
    }

    /**
     * Returns if the bird is BLUE player.
     * @return true if is the BLUE player, false otherwise.
     */
    public boolean isSecond() {
        return isSecond;
    }

    /**
     * Sets the bird's current amount of lives.
     * @param NUMBER_LIVES amount to set lives to.
     */
    public void setNUMBER_LIVES(int NUMBER_LIVES) {
        this.NUMBER_LIVES = NUMBER_LIVES;
    }
}

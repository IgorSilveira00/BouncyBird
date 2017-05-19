package com.aor.bouncy.model.entities;

/**
 * A model representing the user's bird.
 */
public class BirdModel extends EntityModel{
    private boolean flying;

    private boolean headRight;

    private boolean isSecond = false;

    private int NUMBER_LIFES = 3;

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

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setHeadRight(boolean headRight) {
        this.headRight = headRight;
    }

    public boolean isHeadRight() {
        return headRight;
    }

    public int getNUMBER_LIFES() {
        return NUMBER_LIFES;
    }

    public void decNumberLifes() {
       if (NUMBER_LIFES > 0)
           NUMBER_LIFES--;
    }

    public void setSecond(boolean second) {
        isSecond = second;
    }

    public boolean isSecond() {
        return isSecond;
    }
}

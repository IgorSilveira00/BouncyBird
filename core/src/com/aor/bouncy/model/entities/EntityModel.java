package com.aor.bouncy.model.entities;

/**
 * An abstract model representing an entity belonging to a game's model.
 */
public abstract class EntityModel {
    public enum ModelType {RIGHT_SPIKE, LEFT_SPIKE, SPIKE, BIRD, BONUS, EDGE, LIFE};

    /**
     * The x-coordinate of this model in meters.
     */
    private float x;

    /**
     * The y-coordinate of this model in meters.
     */
    private float y;

    /**
     * The current angle of this model in radians.
     */
    private float angle;

    /**
     * Has this model been flagged for removal?
     */
    private boolean flaggedForRemoval = false;

    /**
     * Constructs a model with a position
     * @param x The x-coordinate of this model in meters.
     * @param y The y-coordinate of this model in meters.
     */
    public EntityModel(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    /**
     * Returns the x-coordinate of this entity.
     * @return The x-coordinate of this model in meters.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this entity.
     * @return y The y-coordinate of this model in meters.
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the angle og this entity.
     * @return the andle of this model in radians.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Sets the position of this entity.
     * @param x The x-coordinate of this model in meters.
     * @param y The y-coordinate of this model in meters.
     */
    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the angle of this entity.
     * @param angle angle to set to.
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Returns if this entity has been flagged for removal.
     * @return
     */
    public boolean isFlaggedForRemoval() {
        return flaggedForRemoval;
    }

    /**
     * Makes this model flagged for removal on next step.j
     * @param flaggedForRemoval
     */
    public void setFlaggedForRemoval(boolean flaggedForRemoval) {
        this.flaggedForRemoval = flaggedForRemoval;
    }

    public abstract ModelType getType();
}

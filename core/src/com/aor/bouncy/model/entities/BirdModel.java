package com.aor.bouncy.model.entities;

/**
 * A model representing the user's bird.
 */
public class BirdModel extends EntityModel{
    /**
     * Creates a new bird model in a certain position.
     * @param x The x-coordinate of this bird in meters.
     * @param y The y-coordinate of this bird in meters.
     */
    public BirdModel(float x, float y, float angle) {
        super(x, y, angle);
    }

    @Override
    public ModelType getType() {
        return ModelType.BIRD;
    }
}

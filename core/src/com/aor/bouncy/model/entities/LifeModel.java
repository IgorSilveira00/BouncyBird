package com.aor.bouncy.model.entities;

/**
 * An abstract model representating an entity belonging to a game's model.
 */
public class LifeModel extends EntityModel {
    /**
     * Creates a new bird model in a certain position.
     * @param x The x-coordinate of this bird in meters.
     * @param y The y-coordinate of this bird in meters.
     */
    public LifeModel(float x, float y, float angle) {
        super(x, y, angle);
    }

    @Override
    public ModelType getType() {
        return ModelType.LIFE;
    }
}

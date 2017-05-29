package com.aor.bouncy.model.entities;

/**
 * An abstract model representing an entity belonging to a game's model.
 */
public class EdgeModel extends EntityModel {
    /**
     * Creates a new edge model in a certain position.
     * @param x The x-coordinate of this edge in meters.
     * @param y The y-coordinate of this edge in meters.
     * @param angle The angle of this edge in radians.
     */
    public EdgeModel(float x, float y, float angle) {
        super(x, y, angle);
    }

    @Override
    public ModelType getType() {
        return ModelType.EDGE;
    }
}

package com.aor.bouncy.model.entities;

/**
 * A model representing a spike.
 */
public class SpikeModel extends EntityModel{
    private ModelType modelType;

    /**
     * Constructs a spike model belonging to a game's model.
     * @param x The x-coordinate of this spike in meters.
     * @param y The y-coordinate of this spike in meters.
     */
    public SpikeModel(float x, float y, float angle, ModelType type) {
        super(x, y, angle);
        modelType = type;
    }

    @Override
    public ModelType getType() {
        return modelType;
    }
}

package com.aor.bouncy.model.entities;

/**
 * A model representing a spike.
 */
public class SpikeModel extends EntityModel{
    /**
     * Used to know to which wall the spike belongs to.
     */
    private ModelType modelType;

    /**
     * Is the
     */
    private boolean normalTexture = true;

    /**
     * Set the normal texture flag for this spike
     * @param normalTexture the texture flag.
     */
    public void setNormalTexture(boolean normalTexture) {
        this.normalTexture = normalTexture;
    }

    /**
     * Gets the texture flag of this spike.
     * @return the texture flag
     */
    public boolean isNormalTexture() {
        return normalTexture;
    }

    /**
     * Constructs a spike model belonging to a game's model.
     * @param x The x-coordinate of this spike in meters.
     * @param y The y-coordinate of this spike in meters.
     * @param angle The angle of this spike in radians.
     * @param type The ModelType of this spike.
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

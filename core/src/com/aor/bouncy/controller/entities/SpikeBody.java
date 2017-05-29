package com.aor.bouncy.controller.entities;

import com.aor.bouncy.model.entities.SpikeModel;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * A concrete representation of an EntityBody
 * representing the spike.
 */
public class SpikeBody extends EntityBody{
    /**
     * Constructs a spike's body according to
     * a spike model.
     * @param world the physical world this spike belongs to.
     * @param model the model representing this spike.
     */
    public SpikeBody(World world, SpikeModel model){
        super(world, model, BodyDef.BodyType.KinematicBody);

        float density = 0.0f, friction = 0.8f, restitution = 0.5f;
        int width = 100, height = 100;

        // Triangular spike's shape
        createFixture(body, new float[]{0,16, 99,49, 0,82}, width, height, density, friction, restitution, SPIKE_BODY, (short) (BIRD_BODY | SPIKE_BODY | BONUS_BODY | EDGE_BODY));
     }
}

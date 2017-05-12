package com.aor.bouncy.controller.entities;

import com.aor.bouncy.model.entities.BirdModel;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * A concrete representation of and EntityBody
 * representing the player's bird.
 */
public class BirdBody extends EntityBody{
    /**
     * Constructs a bird's body according to
     * a bird model.
     * @param world the physical world this bird belongs to.
     * @param model the model representing this bird.
     */
    public BirdBody(World world, BirdModel model){
        super(world, model, BodyDef.BodyType.DynamicBody);

        float density = 0.5f, friction = 0.2f, restitution = 0f;
        int width = 100, height = 100;

        // Upper triangle
        createFixture(body, new float[]{0,15, 15,1, 85,1, 100,15, 0,85}, width, height, density, friction, restitution, BIRD_BODY, (short) (BIRD_BODY | SPIKE_BODY | BONUS_BODY | EDGE_BODY));

        // Bottom triangle
        createFixture(body, new float[]{0,85, 100,15, 100,85, 85,99, 15,99}, width, height, density, friction, restitution, BIRD_BODY, (short) (BIRD_BODY | SPIKE_BODY | BONUS_BODY | EDGE_BODY));
    }
}

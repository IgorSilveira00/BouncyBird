package com.aor.bouncy.controller.entities;

import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.entities.EdgeModel;
import com.aor.bouncy.view.GameView;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * A concrete representation of an EntityBody
 * representing the edges.
 */
public class EdgeBody extends EntityBody {
    /**
     * Constructs a edges's body according to
     * a ground model.
     * @param world the physical world this ground belongs to.
     * @param model the model representing this edge.
     */
    public EdgeBody(World world, EdgeModel model){
        super(world, model, BodyDef.BodyType.StaticBody);

        float density = 0.5f, friction = 0, restitution = 0f;
        int width = Math.round(GameController.ROOM_WIDTH / GameView.PIXEL_TO_METER) * 2, height = 50;
        // Create rectangular shape
        createFixture(body, new float[]{0,height, width,height, width,0, 0,0}, width, height, density, friction, restitution, EDGE_BODY, (short) (BIRD_BODY | SPIKE_BODY | BONUS_BODY | EDGE_BODY));
    }
}

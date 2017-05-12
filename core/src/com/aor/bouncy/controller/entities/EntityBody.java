package com.aor.bouncy.controller.entities;

import com.aor.bouncy.model.entities.EntityModel;
import com.badlogic.gdx.physics.box2d.*;

import static com.aor.bouncy.view.GameView.PIXEL_TO_METER;
/**
 * Wrapper class that represents an abstract physical
 * body supported by a Box2D body.
 */
public abstract class EntityBody {
    final static short BIRD_BODY = 0x0001;
    final static short SPIKE_BODY = 0x0002;
    final static short BONUS_BODY = 0x0003;
    final static short EDGE_BODY = 0x0004;

    /**
     * The Box2D body that supports this body.
     */
    final Body body;

    EntityBody(World world, EntityModel model, BodyDef.BodyType type){
        BodyDef bodyDef =  new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(model.getX(), model.getY());
        bodyDef.angle = model.getAngle();

        body = world.createBody(bodyDef);
        body.setUserData(model);
    }

    /**
     * By arestivo from github.com/arestivo
     * Helper method to create a polygon fixture represented by a set of vertexes
     * @param body The body the fixture is to be attached to.
     * @param vertexes The vertexes defining the fixture in pixels so it is
     *                 easier to get them from a bitmap image.
     * @param width The width of the bitmap the vertexes where extracted from.
     * @param heigth The height of the bitmap the vertexes where extracted from.
     * @param density The density of the fixture. How heavy it is in relation to it's area.
     * @param friction The friction of the fixture. How slippery it is.
     * @param restitution The restitution of the fixture. How much it bounces.
     * @param category
     * @param mask
     */
    final void createFixture(Body body, float[] vertexes, int width, int heigth, float density, float friction, float restitution, short category, short mask) {
        // Transform pixels into meters, center and invert the y-coordinate
        for (int i = 0; i < vertexes.length; i++) {
            if (i % 2 == 0) vertexes[i] -= width / 2;   //center the vertex's x-coordinate
            if (i % 2 != 0) vertexes[i] -= heigth / 2;  //center the vertex's y-coordinate
            if (i % 2 != 0) vertexes[i] *= -1;          //invert the y-coordinate

            vertexes[i] *= PIXEL_TO_METER;
        }

        PolygonShape polygon = new PolygonShape();
        polygon.set(vertexes);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygon;

        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.filter.categoryBits = category;
        fixtureDef.filter.maskBits = mask;

        body.createFixture(fixtureDef);
        polygon.dispose();
    }

    /**
     * Wraps the getX method from the Box2D body class.
     * @return the x-coordinate of this body.
     */
    public float getX() {
        return body.getPosition().x;
    }

    /**
     * Wraps the getY method from the Box2D body class.
     * @return the y-coordinate of this body.
     */
    public float getY() {
        return body.getPosition().y;
    }

    /**
     * Wraps the setTransform method from the Box2D body class.
     * @param x the new x-coordinate of this body.
     * @param y the new y-coordinate of this body.
     */
    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    /**
     * Wraps the applyForceToCenter method from the Box2D body class.
     * @param x the x-component of the force to be applied.
     * @param y the y-component of the force to be applied.
     * @param awake should the body be awaken
     */
    public void applyForceToCenter(float x, float y, boolean awake) {
        body.applyForceToCenter(x, y, awake);
    }

    /**
     * Wraps the getUserData method from the Box2D body class.
     * @return the user data.
     */
    public Object getUserData() {
        return body.getUserData();
    }
}

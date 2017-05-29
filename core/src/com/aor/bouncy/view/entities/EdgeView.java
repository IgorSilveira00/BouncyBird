package com.aor.bouncy.view.entities;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.entities.EntityModel;
import com.aor.bouncy.view.GameView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A view representing an edge.
 */
public class EdgeView extends EntityView {
    /**
     * The texture used for the edge.
     */
    private TextureRegion textureRegion;

    /**
     * Constructs an edge model.
     *
     * @param game the game this view belongs to. Needed to access the
     *             asset manager to get textures.
     */
    public EdgeView(MyBouncyBird game) {
        super(game);
    }

    /**
     * Creates a sprite representing this edge.
     *
     * @param game the game this view belongs to. Needed to access the
     *             asset manager to get textures.
     * @return the sprite representing this edge.
     */
    @Override
    public Sprite createSprite(MyBouncyBird game) {
        textureRegion = createTexture(game);

        return new Sprite(textureRegion);
    }

    /**
     * Creates the texture used for the edge.
     *
     * @param game the game this view belongs to. Needed to access the
     *             asset manager to get textures.
     * @return the texture used.
     */
    private TextureRegion createTexture(MyBouncyBird game) {
        Texture texture = game.getAssetManager().get("floor.png");
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        return new TextureRegion(texture, texture.getWidth(), texture.getHeight());
    }

    /**
     * Updates this edge's model.
     *
     * @param model the model used to update this view.
     */
    @Override
    public void update(EntityModel model) {
        super.update(model);
    }

    /**
     * Draws the sprite from this view using a sprite batch.
     *
     * @param batch The sprite batch to be used for drawing.
     */
    @Override
    public void draw(SpriteBatch batch) {
        sprite.setRegion(textureRegion);
        sprite.draw(batch);
    }
}

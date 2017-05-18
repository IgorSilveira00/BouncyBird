package com.aor.bouncy.view.entities;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.model.entities.EntityModel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A view representing a bird.
 */
public class BirdView extends EntityView {
    /**
     * The time between the animation frames
     */
    private static final float FRAME_TIME = 0.05f;

    /**
     * The animation used for the bird flying
     */
    private Animation<TextureRegion> flyingAnimation;

    /**
     * The texture used for the bird not flying
     */
    private TextureRegion nonFlyingAnimation;

    /**
     * Is the bird flying.
     */
    private boolean flying;

    /**
     * Time since the space ship started the game. Used
     * to calculate the frame to show in animations.
     */
    private float stateTime = 0;

    /**
     * Constructs a bird model.
     *
     * @param game the game this view belongs to. Needed to access the
     *             asset manager to get textures.
     */
    public BirdView(MyBouncyBird game) {
        super(game);
    }

    /**
     * Creates a sprite representing this bird.
     *
     * @param game the game this view belongs to. Needed to access the
     *             asset manager to get textures.
     * @return the sprite representing this bird.
     */
    @Override
    public Sprite createSprite(MyBouncyBird game) {
        flyingAnimation = createFlyingAnimation(game);
        nonFlyingAnimation = createNonFlyingAnimation(game);

        return new Sprite(nonFlyingAnimation);
    }

    /**
     * Creates the texture used when the bird is not flying
     *
     * @param game the game this view belongs to. Needed to access the
     *             asset manager to get textures.
     * @return the texture used when the bird is not flying
     */
    private TextureRegion createNonFlyingAnimation(MyBouncyBird game) {
        Texture notFlyingTexture = game.getAssetManager().get("bird.png");
        return new TextureRegion(notFlyingTexture, notFlyingTexture.getWidth(), notFlyingTexture.getHeight());
    }

    /**
     * Creates the animation used when the bird is flying
     *
     * @param game the game this view belongs to. Needed to access the
     *             asset manager to get textures.
     * @return the animation used when the bird is flying
     */
    private Animation<TextureRegion> createFlyingAnimation(MyBouncyBird game) {
        Texture thrustTexture = game.getAssetManager().get("bird.png");
        TextureRegion[][] thrustRegion = TextureRegion.split(thrustTexture, thrustTexture.getWidth() / 1, thrustTexture.getHeight());

        TextureRegion[] frames = new TextureRegion[1];
        System.arraycopy(thrustRegion[0], 0, frames, 0, 1);

        return new Animation<TextureRegion>(FRAME_TIME, frames);
    }

    /**
     * Updates this bird model.
     *
     * @param model the model used to update this view
     */
    @Override
    public void update(EntityModel model) {
        super.update(model);

    }

    /**
     * Draws the sprite from this view using a sprite batch.
     * Chooses the correct texture or animation to be used
     * depending on the flying flag.
     *
     * @param batch The sprite batch to be used for drawing.
     */
    @Override
    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        if (flying)
            sprite.setRegion(flyingAnimation.getKeyFrame(stateTime, true));
        else
            sprite.setRegion(nonFlyingAnimation);

        sprite.draw(batch);
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }
}

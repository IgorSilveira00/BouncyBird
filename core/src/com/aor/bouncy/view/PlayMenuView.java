package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.GameModel;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * A view representing the game main menu.
 */
public class PlayMenuView extends ScreenAdapter implements ApplicationListener, InputProcessor {
    /**
     * How much meters does a pixel represent
     */
    public final static float PIXEL_TO_METER = 0.04f;

    /**
     * The width of the viewport in meters. The height is
     * automatically calculated using the screen ratio.
     */
    private static final float VIEWPORT_WIDTH = 60;

    /**
     * The game this screen belongs to.
     */
    private final MyBouncyBird game;

    /**
     * The camera used to show the viewport.
     */
    private final OrthographicCamera camera;

    /**
     * Stage where all buttons will be drawn.
     */
    private Stage stage;

    /**
     * Object of the TextButton class representing the 1P button.
     */
    private TextButton ONE_PLAY_BUTTON;

    /**
     * Object of the TextButton class representing the 2P button.
     */
    private TextButton TWO_PLAY_BUTTON;

    /**
     * Object of the TextButton class representing the back button.
     */
    private TextButton BACK_BUTTON;

    /**
     * Variable telling us if it is the first time running this instance.
     */
    private boolean FIRST_TIME = true;

    /**
     * Creates this screen.
     * @param game The game this screen belongs to
     */
    public PlayMenuView(MyBouncyBird game) {
        this.game = game;
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        loadButtons();

        enableButtons();
        camera = createCamera();
    }

    /**
     * Initializes this view's buttons.
     */
    private void loadButtons() {
        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();
        TextureAtlas textureAtlas = new TextureAtlas();
        TextButton.TextButtonStyle b1 = new TextButton.TextButtonStyle(),
                b2 = new TextButton.TextButtonStyle(),
                b3 = new TextButton.TextButtonStyle(),
                b4 = new TextButton.TextButtonStyle();

        readTexture("1plocal-up", "1plocal-up.png", textureAtlas);
        readTexture("1plocal-down", "1plocal-down.png", textureAtlas);
        readTexture("2plocal-up", "2plocal-up.png", textureAtlas);
        readTexture("2plocal-down", "2plocal-down.png", textureAtlas);
        readTexture("back-up", "back-up.png", textureAtlas);
        readTexture("back-down", "back-down.png", textureAtlas);
        skin.addRegions(textureAtlas);

        b1.font = font; b2.font = font; b3.font = font; b4.font = font;

        //for the 1 local player game button
        b1.up = skin.getDrawable("1plocal-up");
        b1.down = skin.getDrawable("1plocal-down");
        ONE_PLAY_BUTTON = new TextButton("", b1);
        ONE_PLAY_BUTTON.setPosition(Gdx.graphics.getWidth() / 4f - ONE_PLAY_BUTTON.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - ONE_PLAY_BUTTON.getHeight() / 2f);

        //for the 2 local players game button
        b2.up = skin.getDrawable("2plocal-up");
        b2.down = skin.getDrawable("2plocal-down");
        TWO_PLAY_BUTTON = new TextButton("", b2);
        TWO_PLAY_BUTTON.setPosition(3 * Gdx.graphics.getWidth() / 4f - TWO_PLAY_BUTTON.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - TWO_PLAY_BUTTON.getHeight() / 2f);

        //for the Exit button
        b4.up = skin.getDrawable("back-up");
        b4.down = skin.getDrawable("back-down");
        BACK_BUTTON = new TextButton("", b4);
        BACK_BUTTON.setPosition(Gdx.graphics.getWidth() - BACK_BUTTON.getWidth(),
                 0);

        stage.addActor(ONE_PLAY_BUTTON);
        stage.addActor(TWO_PLAY_BUTTON);
        stage.addActor(BACK_BUTTON);
        addListeners();
    }

    @Override
    public void create() {
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (!FIRST_TIME)
            game.setScreen(new PlayMenuView(game));
        FIRST_TIME = false;
    }

    @Override
    public void render() {

    }

    /**
     * Adds the listeners to the MainMenuView's buttons.
     */
    private void addListeners() {
        ONE_PLAY_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();

                GameController.getInstance().dispose();
                GameModel.getInstance().dispose();

                game.setScreen(new GameView(game, false));

                disableButtons();
            }
        });

        TWO_PLAY_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();

                GameController.getInstance().dispose();
                GameModel.getInstance().dispose();

                MyBouncyBird.setPLAYER_ONE_LIVES(3);
                MyBouncyBird.setPLAYER_TWO_LIVES(3);

                game.setScreen(new GameView(game, true));
                disableButtons();
            }
        });

        BACK_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
                game.setScreen(new MainMenuView(game, false));
            }
        });
    }

    /**
     * Disables and hides every button and unpauses the controller updates.
     */
    private void disableButtons() {
        ONE_PLAY_BUTTON.setDisabled(true);
        TWO_PLAY_BUTTON.setDisabled(true);
        BACK_BUTTON.setDisabled(true);
    }

    /**
     * Enables and activates every button and pauses the controller updates.
     */
    private void enableButtons() {
        ONE_PLAY_BUTTON.setDisabled(false);
        TWO_PLAY_BUTTON.setDisabled(false);
        BACK_BUTTON.setDisabled(false);
    }

    /**
     * Adds TextureRegions to a given TextureAtlas
     * @param regionName the name for the atlas' region.
     * @param assetName the name of the asset to load.
     * @param textureAtlas the TextureAtlas to add the TextureRegion to.
     */
    private void readTexture(String regionName, String assetName, TextureAtlas textureAtlas) {
        Texture toLoad = game.getAssetManager().get(assetName);
        TextureRegion textureRegion = new TextureRegion(toLoad, toLoad.getWidth(), toLoad.getHeight());
        textureAtlas.addRegion(regionName, textureRegion);
    }

    /**
     * Creates the camera used to show the viewport.
     * @return the camera
     */
    public OrthographicCamera createCamera() {
        OrthographicCamera camera = new OrthographicCamera(VIEWPORT_WIDTH / PIXEL_TO_METER,
                VIEWPORT_WIDTH / PIXEL_TO_METER * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));

        camera.position.set(camera.viewportWidth / 2f,
                camera.viewportHeight / 2f,
                0);
        camera.update();
        return camera;
    }

    /**
     * Renders this screen.
     *
     * @param delta time since last renders in seconds.
     */
    //@Override
    public void render(float delta) {
        game.getBatch().setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor( 0/255f, 0/255f, 0/255f, 1 );

        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        game.getBatch().begin();
        drawBackground();
        game.getBatch().end();

        stage.draw();
    }

    /**
     * Draws the background
     */
    private void drawBackground() {
       MainMenuView.drawBackground();
    }

    public OrthographicCamera getCamera() {

        return camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        //Catch the android back key.
        if (keycode == Input.Keys.BACK) {
            MainMenuView.playClick();
            game.setScreen(new MainMenuView(game, false));
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

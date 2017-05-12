package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.aor.bouncy.controller.GameController.*;

/**
 * A view representing the game main menu.
 */
public class PlayMenuView extends ScreenAdapter {
    //test
    static int RED = 20, GREEN = 100, BLUE = 200;

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

    private Stage stage;

    private TextButton ONE_PLAY_BUTTON;

    private TextButton TWO_PLAY_BUTTON;

    private TextButton TWO_NET_BUTTON;

    private TextButton BACK_BUTTON;

    /**
     * Creates this screen.
     * @param game The game this screen belongs to
     */
    public PlayMenuView(MyBouncyBird game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        loadButtons();

        enableButtons();
        camera = createCamera();
    }

    private void loadButtons() {
        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();
        TextureAtlas textureAtlas = new TextureAtlas();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(),
                backButtonStyle = new TextButton.TextButtonStyle();

        readTexture("play-up", "test.png", textureAtlas);
        readTexture("play-down", "bt.JPG", textureAtlas);
        readTexture("back-up", "back-up.png", textureAtlas);
        readTexture("back-down", "back-down.png", textureAtlas);
        skin.addRegions(textureAtlas);

        buttonStyle.font = font; backButtonStyle.font = font;

        //for the 1 local player game button
        buttonStyle.up = skin.getDrawable("play-up");
        buttonStyle.down = skin.getDrawable("play-down");
        ONE_PLAY_BUTTON = new TextButton("1P Local", buttonStyle);
        ONE_PLAY_BUTTON.setPosition(20, Gdx.graphics.getHeight() / 2f + ONE_PLAY_BUTTON.getHeight() / 2f);

        //for the 2 local players game button
        buttonStyle.up = skin.getDrawable("play-up");
        buttonStyle.down = skin.getDrawable("play-down");
        TWO_PLAY_BUTTON = new TextButton("2P Local", buttonStyle);
        TWO_PLAY_BUTTON.setPosition(ONE_PLAY_BUTTON.getX(),
                ONE_PLAY_BUTTON.getY() - ONE_PLAY_BUTTON.getHeight());

        //for the 2 net players game button
        buttonStyle.up = skin.getDrawable("play-up");
        buttonStyle.down = skin.getDrawable("play-down");
        TWO_NET_BUTTON = new TextButton("2P Net", buttonStyle);
        TWO_NET_BUTTON.setPosition(TWO_PLAY_BUTTON.getX(),
                TWO_PLAY_BUTTON.getY() - TWO_PLAY_BUTTON.getHeight());

        //for the Exit button
        backButtonStyle.up = skin.getDrawable("back-up");
        backButtonStyle.down = skin.getDrawable("back-down");
        BACK_BUTTON = new TextButton("", backButtonStyle);
        BACK_BUTTON.setPosition(Gdx.graphics.getWidth() - BACK_BUTTON.getWidth(),
                 0);

        addActors();
        addListeners();
    }

    /**
     * Adds the buttons to the MainMenuView's stage.
     */
    private void addActors() {
        stage.addActor(ONE_PLAY_BUTTON);
        stage.addActor(TWO_PLAY_BUTTON);
        stage.addActor(TWO_NET_BUTTON);
        stage.addActor(BACK_BUTTON);
    }

    /**
     * Adds the listeners to the MainMenuView's buttons.
     */
    private void addListeners() {
        ONE_PLAY_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameView(game, false));
                disableButtons();
            }
        });

        TWO_PLAY_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameView(game, true));
                disableButtons();
            }
        });

        TWO_NET_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            }
        });

        BACK_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuView(game, false));
            }
        });
    }

    private void disableButtons() {
        ONE_PLAY_BUTTON.setDisabled(true);
        TWO_PLAY_BUTTON.setDisabled(true);
        TWO_NET_BUTTON.setDisabled(true);
        BACK_BUTTON.setDisabled(true);
    }

    private void enableButtons() {
        ONE_PLAY_BUTTON.setDisabled(false);
        TWO_PLAY_BUTTON.setDisabled(false);
        TWO_NET_BUTTON.setDisabled(false);
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

//        Gdx.gl.glClearColor( 103/255f, 69/255f, 117/255f, 1 );
        Gdx.gl.glClearColor( RED/255f, GREEN/255f, BLUE/255f, 1 );

        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        game.getBatch().begin();
        //drawBackground();
        game.getBatch().end();

        stage.draw();
    }

    /**
     * Draws the background
     */
    private void drawBackground() {
        Texture background = game.getAssetManager().get("background.png", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        game.getBatch().draw(background, 0, 0, 0, 0, (int)(ROOM_WIDTH / PIXEL_TO_METER), (int) (ROOM_HEIGHT / PIXEL_TO_METER));
    }

    public OrthographicCamera getCamera() {

        return camera;
    }
}

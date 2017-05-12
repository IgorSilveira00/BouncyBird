package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.Utilities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
 * A view representing the game screen. Draws all the other views and
 * controls the camera.
 */
public class MainMenuView extends ScreenAdapter {
    //test
    static int RED = 20, GREEN = 100, BLUE = 200;

    private int CHECK  = 1;

    /**
     * Used to debug the position of the physics fixtures
     */
    private static final boolean DEBUG_PHYSICS = true;

    private MainMenuView instance = this;

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

    /**
     * A renderer used to debug the physical fixtures.
     */
    private Box2DDebugRenderer debugRenderer;

    /**
     * The transformation matrix used to transform meters into
     * pixels in order to show fixtures in their correct places.
     */
    private Matrix4 debugCamera;

    private TextButton PLAY_BUTTON;

    private TextButton SETTINGS_BUTTON;

    private TextButton EXIT_BUTTON;

    /**
     * Creates this screen.
     * @param game The game this screen belongs to
     */
    public MainMenuView(MyBouncyBird game, boolean IS_ACTIVE) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        if (IS_ACTIVE) {
        loadAssets();
        loadSounds();

        if (!game.getBACKGROUND_MUSIC().isPlaying())
            game.getBACKGROUND_MUSIC().play();
        }

        loadButtons();
        enableButtons();
        camera = createCamera();
    }

    private void loadSounds() {
        Music toPlay = game.getAssetManager().get("background-music.mp3");
        toPlay.setLooping(true);
        game.setBACKGROUND_MUSIC(toPlay);
    }

    private void loadButtons() {
        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();
        TextureAtlas textureAtlas = new TextureAtlas();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        readTexture("play-up", "test.png", textureAtlas);
        readTexture("play-down", "bt.JPG", textureAtlas);
        skin.addRegions(textureAtlas);

        buttonStyle.font = font;

        //for the play button
        buttonStyle.up = skin.getDrawable("play-up");
        buttonStyle.down = skin.getDrawable("play-down");
        PLAY_BUTTON = new TextButton("Play", buttonStyle);
        PLAY_BUTTON.setPosition(10, Gdx.graphics.getHeight() / 2f + PLAY_BUTTON.getHeight() / 2f);

        //for the settings button
        buttonStyle.up = skin.getDrawable("play-up");
        buttonStyle.down = skin.getDrawable("play-down");
        SETTINGS_BUTTON = new TextButton("Settings", buttonStyle);
        SETTINGS_BUTTON.setPosition(10, PLAY_BUTTON.getY() - SETTINGS_BUTTON.getHeight());

        //for the Exit button
        buttonStyle.up = skin.getDrawable("play-up");
        buttonStyle.down = skin.getDrawable("play-down");
        EXIT_BUTTON = new TextButton("Exit", buttonStyle);
        EXIT_BUTTON.setPosition(10, SETTINGS_BUTTON.getY() - EXIT_BUTTON.getHeight());

        addActors();
        addListeners();
    }

    /**
     * Adds the buttons to the MainMenuView's stage.
     */
    private void addActors() {
        stage.addActor(PLAY_BUTTON);
        stage.addActor(SETTINGS_BUTTON);
        stage.addActor(EXIT_BUTTON);
    }

    /**
     * Adds the listeners to the MainMenuView's buttons.
     */
    private void addListeners() {
        PLAY_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PlayMenuView(game));
                disableButtons();
            }
        });

        SETTINGS_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsMenuView(game));
            }
        });

        EXIT_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
            }
        });
    }

    private void disableButtons() {
        PLAY_BUTTON.setDisabled(true);
        SETTINGS_BUTTON.setDisabled(true);
        EXIT_BUTTON.setDisabled(true);
    }

    private void enableButtons() {
        PLAY_BUTTON.setDisabled(false);
        SETTINGS_BUTTON.setDisabled(false);
        EXIT_BUTTON.setDisabled(false);
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

        if (DEBUG_PHYSICS){
            debugRenderer = new Box2DDebugRenderer();
            debugCamera = camera.combined.cpy();
            debugCamera.scl(1 / PIXEL_TO_METER);
        }
        return camera;
    }

    /**
     * Loads the assets needed by this screen.
     */
    private void loadAssets() {
        this.game.getAssetManager().load( "bt.JPG" , Texture.class);
        this.game.getAssetManager().load( "test.png" , Texture.class);
        this.game.getAssetManager().load("background-music.mp3", Music.class);
        this.game.getAssetManager().load( "empty-check.png" , Texture.class);
        this.game.getAssetManager().load( "full-check.png" , Texture.class);
        this.game.getAssetManager().load( "back-up.png" , Texture.class);
        this.game.getAssetManager().load( "back-down.png" , Texture.class);
        this.game.getAssetManager().load( "edge-hit.wav" , Sound.class);
        this.game.getAssetManager().load( "background.png" , Texture.class);

        this.game.getAssetManager().finishLoading();
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
        drawBackground();
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

package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.aor.bouncy.controller.GameController.*;

/**
 * A view representing the game screen. Draws all the other views and
 * controls the camera.
 */
public class MainMenuView extends ScreenAdapter {
    /**
     * Used to debug the position of the physics fixtures
     */
    private static final boolean DEBUG_PHYSICS = false;

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
    private static MyBouncyBird game;

    /**
     * The camera used to show the viewport.
     */
    private final OrthographicCamera camera;

    /**
     * Stage where all buttons will be drawn.
     */
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

    /**
     * Object of the TextButton class representing the play button.
     */
    private TextButton PLAY_BUTTON;

    /**
     * Object of the TextButton class representing the settings button.
     */
    private TextButton SETTINGS_BUTTON;

    /**
     * Object of the TextButton class representing the exit button.
     */
    private TextButton EXIT_BUTTON;

    /**
     * Object of the Sound class for the mouse click effect.
     */
    private static Sound MOUSE_CLICK;

    /**
     * Variable telling us if it is the first time running this instance.
     */
    private boolean firstTime = true;

    /**
     * Creates this screen.
     * @param game The game this screen belongs to
     */
    public MainMenuView(MyBouncyBird game, boolean IS_ACTIVE) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Only loaded one time.
        if (IS_ACTIVE) {
        loadAssets();
        loadSounds();

        if (!game.getBACKGROUND_MUSIC().isPlaying())
            game.getBACKGROUND_MUSIC().setVolume(game.getBACKGROUND_MUSIC().getVolume() / 2f);
            game.getBACKGROUND_MUSIC().play();
        }

        camera = createCamera();
        loadButtons();
        enableButtons();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (!firstTime)
            game.setScreen(new MainMenuView(game, false));
        firstTime = false;
    }

    /**
     * Initializes the Sounds and Musics used for the game.
     */
    private void loadSounds() {
        Music toPlay = game.getAssetManager().get("background-music.mp3");
        toPlay.setLooping(true);
        game.setBACKGROUND_MUSIC(toPlay);
        MOUSE_CLICK = game.getAssetManager().get("click.mp3");
    }

    /**
     * Initializes all of this view's buttons.
     */
    private void loadButtons() {
        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();
        TextureAtlas textureAtlas = new TextureAtlas();
        TextButton.TextButtonStyle b1 = new TextButton.TextButtonStyle(),
                b2 = new TextButton.TextButtonStyle(),
                b3 = new TextButton.TextButtonStyle();

        readTexture("play-up", "play-up.png", textureAtlas);
        readTexture("play-down", "play-down.png", textureAtlas);
        readTexture("settings-up", "settings-up.png", textureAtlas);
        readTexture("settings-down", "settings-down.png", textureAtlas);
        readTexture("exit-up", "exit-up.png", textureAtlas);
        readTexture("exit-down", "exit-down.png", textureAtlas);
        skin.addRegions(textureAtlas);

        b1.font = font; b2.font = font; b3.font = font;

        //for the play button
        b1.up = skin.getDrawable("play-up");
        b1.down = skin.getDrawable("play-down");
        PLAY_BUTTON = new TextButton("", b1);
        PLAY_BUTTON.setPosition(Gdx.graphics.getWidth() / 2f - PLAY_BUTTON.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f + PLAY_BUTTON.getHeight() / 2f);

        //for the settings button
        b2.up = skin.getDrawable("settings-up");
        b2.down = skin.getDrawable("settings-down");
        SETTINGS_BUTTON = new TextButton("", b2);
        SETTINGS_BUTTON.setPosition(PLAY_BUTTON.getX(), PLAY_BUTTON.getY() - SETTINGS_BUTTON.getHeight());

        //for the Exit button
        b3.up = skin.getDrawable("exit-up");
        b3.down = skin.getDrawable("exit-down");
        EXIT_BUTTON = new TextButton("", b3);
        EXIT_BUTTON.setPosition(SETTINGS_BUTTON.getX(), SETTINGS_BUTTON.getY() - EXIT_BUTTON.getHeight());

        stage.addActor(PLAY_BUTTON);
        stage.addActor(SETTINGS_BUTTON);
        stage.addActor(EXIT_BUTTON);
        addListeners();
    }

    /**
     * Adds the listeners to the MainMenuView's buttons.
     */
    private void addListeners() {
        PLAY_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                MyBouncyBird.setPLAYER_ONE_LIVES(3);
                MyBouncyBird.setPLAYER_TWO_LIVES(3);
                game.setScreen(new PlayMenuView(game));
                disableButtons();
            }
        });

        SETTINGS_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new SettingsMenuView(game));
            }
        });

        EXIT_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClick();
                System.exit(0);
            }
        });
    }

    /**
     * Disables the view's buttons to prevent clicking while in game.
     */
    private void disableButtons() {
        PLAY_BUTTON.setDisabled(true);
        SETTINGS_BUTTON.setDisabled(true);
        EXIT_BUTTON.setDisabled(true);
    }

    /**
     * Enables the buttons.
     */
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
    public static void readTexture(String regionName, String assetName, TextureAtlas textureAtlas) {
        Texture toLoad = game.getAssetManager().get(assetName);
        TextureRegion textureRegion = new TextureRegion(toLoad, toLoad.getWidth(), toLoad.getHeight());
        textureAtlas.addRegion(regionName, textureRegion);
    }

    /**
     * Plays the button click sound.
     */
    public static void playClick() {
        if (game.isFX_ENABLED())
             MOUSE_CLICK.play();
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
     * Loads the assets needed by this game.
     */
    private void loadAssets() {
        this.game.getAssetManager().load( "bird.png" , Texture.class);
        this.game.getAssetManager().load( "bird2.png" , Texture.class);
        this.game.getAssetManager().load( "bird_fly.png" , Texture.class);
        this.game.getAssetManager().load( "bird2_fly.png" , Texture.class);
        this.game.getAssetManager().load("spike.png", Texture.class);
        this.game.getAssetManager().load("worm.png", Texture.class);
        this.game.getAssetManager().load("spike-red.png", Texture.class);
        this.game.getAssetManager().load("floor.png", Texture.class);
        this.game.getAssetManager().load("background-music.mp3", Music.class);
        this.game.getAssetManager().load( "empty-check.png" , Texture.class);
        this.game.getAssetManager().load( "full-check.png" , Texture.class);
        this.game.getAssetManager().load( "back-up.png" , Texture.class);
        this.game.getAssetManager().load( "back-down.png" , Texture.class);
        this.game.getAssetManager().load( "edge-hit.wav" , Sound.class);
        this.game.getAssetManager().load( "background.png" , Texture.class);
        this.game.getAssetManager().load( "play-up.png" , Texture.class);
        this.game.getAssetManager().load( "play-down.png" , Texture.class);
        this.game.getAssetManager().load( "settings-up.png" , Texture.class);
        this.game.getAssetManager().load( "settings-down.png" , Texture.class);
        this.game.getAssetManager().load( "exit-up.png" , Texture.class);
        this.game.getAssetManager().load( "exit-down.png" , Texture.class);
        this.game.getAssetManager().load( "hearts1_p1.png" , Texture.class);
        this.game.getAssetManager().load( "hearts1_p2.png" , Texture.class);
        this.game.getAssetManager().load( "hearts2_p1.png" , Texture.class);
        this.game.getAssetManager().load( "hearts2_p2.png" , Texture.class);
        this.game.getAssetManager().load( "hearts3_p1.png" , Texture.class);
        this.game.getAssetManager().load( "hearts3_p2.png" , Texture.class);
        this.game.getAssetManager().load( "1plocal-up.png" , Texture.class);
        this.game.getAssetManager().load( "1plocal-down.png" , Texture.class);
        this.game.getAssetManager().load( "2plocal-up.png" , Texture.class);
        this.game.getAssetManager().load( "2plocal-down.png" , Texture.class);
        this.game.getAssetManager().load( "2pnet-up.png" , Texture.class);
        this.game.getAssetManager().load( "2pnet-down.png" , Texture.class);
        this.game.getAssetManager().load( "backplate.png" , Texture.class);
        this.game.getAssetManager().load( "ready_p1.png" , Texture.class);
        this.game.getAssetManager().load( "ready_p2.png" , Texture.class);
        this.game.getAssetManager().load( "start_1.png" , Texture.class);
        this.game.getAssetManager().load( "start_2.png" , Texture.class);
        this.game.getAssetManager().load( "start_3.png" , Texture.class);
        this.game.getAssetManager().load( "start.png" , Texture.class);
        this.game.getAssetManager().load( "win_p1.png" , Texture.class);
        this.game.getAssetManager().load( "win_p2.png" , Texture.class);
        this.game.getAssetManager().load( "host_up.png" , Texture.class);
        this.game.getAssetManager().load( "host_down.png" , Texture.class);
        this.game.getAssetManager().load( "join_up.png" , Texture.class);
        this.game.getAssetManager().load( "join_down.png" , Texture.class);
        this.game.getAssetManager().load( "score_template.png" , Texture.class);
        this.game.getAssetManager().load( "text_field.png" , Texture.class);
        this.game.getAssetManager().load( "click.mp3" , Sound.class);
        this.game.getAssetManager().load( "dead.mp3" , Sound.class);
        this.game.getAssetManager().load( "jump.wav" , Sound.class);

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

        Gdx.gl.glClearColor( 0, 0, 0, 1 );

        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        game.getBatch().begin();
        drawBackground();
        game.getBatch().end();

        stage.draw();
    }

    /**
     * Draws the background
     */
    public static void drawBackground() {
        Texture background = game.getAssetManager().get("background.png", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        game.getBatch().draw(background, 0, 0, 0, 0, (int)(ROOM_WIDTH / PIXEL_TO_METER), (int) (ROOM_HEIGHT / PIXEL_TO_METER));
    }
}

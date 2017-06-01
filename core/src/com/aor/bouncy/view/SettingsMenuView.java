package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SettingsMenuView extends ScreenAdapter implements ApplicationListener, InputProcessor{
    //test
    static int RED = 20, GREEN = 50, BLUE = 200;
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
     * Object of the CheckBox class representing the music check button.
     */
    private CheckBox MUSIC_CHECK;

    /**
     * Object of the CheckBox class representing the FX check button.
     */
    private CheckBox FX_CHECK;

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
    public SettingsMenuView(MyBouncyBird game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Gdx.input.setCatchBackKey(true);

        loadUI();

        camera = createCamera();
    }

    /**
     * Initializes this view's buttons and boxes.
     */
    private void loadUI() {
        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();
        TextureAtlas textureAtlas = new TextureAtlas();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();

        readTexture("unchecked", "empty-check.png", textureAtlas);
        readTexture("checked", "full-check.png", textureAtlas);
        readTexture("back-up", "back-up.png", textureAtlas);
        readTexture("back-down", "back-down.png", textureAtlas);
        skin.addRegions(textureAtlas);

        buttonStyle.font = font;

        //for the back button
        buttonStyle.up = skin.getDrawable("back-up");
        buttonStyle.down = skin.getDrawable("back-down");
        BACK_BUTTON = new TextButton("", buttonStyle);
        BACK_BUTTON.setPosition(Gdx.graphics.getWidth() - BACK_BUTTON.getWidth(),
                0);

        checkBoxStyle.font = MainMenuView.getBitmapFont();
        checkBoxStyle.checkboxOn = skin.getDrawable("checked");
        checkBoxStyle.checkboxOff = skin.getDrawable("unchecked");

        //Music checkbox.
        MUSIC_CHECK = new CheckBox("   Music", checkBoxStyle);
        MUSIC_CHECK.setPosition(Gdx.graphics.getWidth() / 2f - MUSIC_CHECK.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f + MUSIC_CHECK.getHeight() / 2f);

        //FX checkbox.
        FX_CHECK = new CheckBox("   FX Sounds", checkBoxStyle);
        FX_CHECK.setPosition(MUSIC_CHECK.getX(),
                MUSIC_CHECK.getY() - 2 * FX_CHECK.getHeight());

        //Updated from the game actual state of these options.
        FX_CHECK.setChecked(game.isFX_ENABLED());
        MUSIC_CHECK.setChecked(game.isMusicEnabled());

        stage.addActor(BACK_BUTTON);
        stage.addActor(MUSIC_CHECK);
        stage.addActor(FX_CHECK);
        addListeners();
    }

    /**
     * Adds the listeners to the MainMenuView's buttons.
     */
    private void addListeners() {
        MUSIC_CHECK.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
                game.setMusicEnabled(!game.isMusicEnabled());
                if (game.isMusicEnabled() && !game.getBACKGROUND_MUSIC().isPlaying())
                    game.getBACKGROUND_MUSIC().play();
                else
                    game.getBACKGROUND_MUSIC().pause();
            }
        });

        FX_CHECK.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
                game.setFX_ENABLED(!game.isFX_ENABLED());
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

    @Override
    public void create() {
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (!FIRST_TIME)
            game.setScreen(new SettingsMenuView(game));
        FIRST_TIME = false;
    }

    @Override
    public void render() {

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
        MainMenuView.drawBackground();
        drawEntities();
        game.getBatch().end();

        stage.draw();
    }

    /**
     * Draws the entities to the screen.
     */
    private void drawEntities() {
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

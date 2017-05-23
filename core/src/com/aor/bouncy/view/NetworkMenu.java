package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
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

import static com.aor.bouncy.controller.GameController.ROOM_HEIGHT;
import static com.aor.bouncy.controller.GameController.ROOM_WIDTH;

public class NetworkMenu extends ScreenAdapter{
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

    private TextButton HOST_BUTTON;

    private TextButton JOIN_BUTTON;

    private TextButton BACK_BUTTON;

    private boolean firstTime = true;

    /**
     * Creates this screen.
     * @param game The game this screen belongs to
     */
    public NetworkMenu(MyBouncyBird game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        loadUI();

        camera = createCamera();
    }

    private void loadUI() {
        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();
        TextureAtlas textureAtlas = new TextureAtlas();
        TextButton.TextButtonStyle buttonStyle1, buttonStyle2, buttonStyle3;

        buttonStyle1 = new TextButton.TextButtonStyle();
        buttonStyle2 = new TextButton.TextButtonStyle();
        buttonStyle3 = new TextButton.TextButtonStyle();

        readTexture("host_up", "host_up.png", textureAtlas);
        readTexture("host_down", "host_down.png", textureAtlas);
        readTexture("join_up", "join_up.png", textureAtlas);
        readTexture("join_down", "join_down.png", textureAtlas);
        readTexture("back-up", "back-up.png", textureAtlas);
        readTexture("back-down", "back-down.png", textureAtlas);
        skin.addRegions(textureAtlas);

        buttonStyle1.font = font;
        buttonStyle2.font = font;
        buttonStyle3.font = font;

        //for the back button
        buttonStyle1.up = skin.getDrawable("back-up");
        buttonStyle1.down = skin.getDrawable("back-down");
        BACK_BUTTON = new TextButton("", buttonStyle1);
        BACK_BUTTON.setPosition(Gdx.graphics.getWidth() - BACK_BUTTON.getWidth(),
                0);
        stage.addActor(BACK_BUTTON);

        buttonStyle2.up = skin.getDrawable("host_up");
        buttonStyle2.down = skin.getDrawable("host_down");
        HOST_BUTTON = new TextButton("", buttonStyle2);
        HOST_BUTTON.setPosition(Gdx.graphics.getWidth() / 2f - HOST_BUTTON.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f + HOST_BUTTON.getHeight() / 2f);
        stage.addActor(HOST_BUTTON);

        buttonStyle3.up = skin.getDrawable("join_up");
        buttonStyle3.down = skin.getDrawable("join_down");
        JOIN_BUTTON = new TextButton("", buttonStyle3);
        JOIN_BUTTON.setPosition(HOST_BUTTON.getX(),
                HOST_BUTTON.getY() - JOIN_BUTTON.getHeight());
        stage.addActor(JOIN_BUTTON);

        addListeners();
    }

    /**
     * Adds the listeners to the MainMenuView's buttons.
     */
    private void addListeners() {
        HOST_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
            }
        });

        JOIN_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
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
    public void resize(int width, int height) {
        super.resize(width, height);

        if (!firstTime)
            game.setScreen(new SettingsMenuView(game));
        firstTime = false;
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

       Gdx.gl.glClearColor( 103/255f, 69/255f, 117/255f, 1 );

        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        game.getBatch().begin();
        MainMenuView.drawBackground();
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

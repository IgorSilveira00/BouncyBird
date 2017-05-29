package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.ServerClient;
import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.GameModel;
import com.aor.bouncy.model.entities.*;
import com.aor.bouncy.view.entities.EntityView;
import com.aor.bouncy.view.entities.ViewFactory;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.aor.bouncy.controller.GameController.*;

/**
 * A view representing the game screen. Draws all the other views and
 * controls the camera.
 */
public class GameView extends ScreenAdapter implements InputProcessor, ApplicationListener {
    //test
    static int t1 = 60, t2 = 30, t3 = 50;

    /**
     * Boolean that decides if players are ready and controller has to work physics.
     */
    private boolean READY_PLAYER_ONE = false;

    /**
     * Boolean that decides if players are ready and controller has to work physics.
     */
    private boolean READY_PLAYER_TWO = false;

    /**
     * Boolean that decides if players are ready and controller has to work physics.
     */
    private boolean IS_RUNNING = false;

    /**
     * Is the game on pause.
     */
    private boolean IS_PAUSED = false;

    /**
     * Time passed since last time. For score and start countdown use only.
     */
    private float passedTime = 4;

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
    public static final float VIEWPORT_WIDTH = 60;
    public static float VIEWPORT_HEIGHT;

    /**
     * The game this screen belongs to.
     */
    private static MyBouncyBird game;

    /**
     * The camera used to show the viewport.
     */
    private final OrthographicCamera camera;

    /**
     * A renderer used to debug the physical fixtures.
     */
    private Box2DDebugRenderer debugRenderer;

    /**
     * The transformation matrix used to transform meters into
     * pixels in order to show fixtures in their correct places.
     */
    private Matrix4 debugCamera;

    Label scoreLabel;

    private TextButton RESUME_BUTTON;

    private TextButton RESTART_BUTTON;

    private TextButton EXIT_BUTTON;

    private static boolean TWO_PLAYERS;

    private static Sound EDGE_HIT_EFFECT;

    private static Sound JUMP_EFFECT;

    private static Sound END_EFFECT;

    private Stage stage =  new Stage();

    private static boolean FIRST_TIME = true;

    private boolean endHasPlayed = false;

    private boolean END = false;

    private ServerClient serverClient = null;

    private boolean IS_SERVER;

    /**
     * Creates this screen.
     * @param game The game that called this screen
     */
    public GameView(MyBouncyBird game, boolean TWO_PLAYERS) {
        this.game = game;
        this.TWO_PLAYERS = TWO_PLAYERS;

        InputMultiplexer multiplexer = new InputMultiplexer(this, stage);
        Gdx.input.setInputProcessor(multiplexer);


        Gdx.input.setCatchBackKey(true);

        camera = createCamera();
        //menuView.setCameras(debugRenderer, debugCamera);

        if (!TWO_PLAYERS) {
            createLabels();
            READY_PLAYER_TWO = true;
        }

        loadButtons();
        disableButtons();

        if (FIRST_TIME) {
            getSoundEffects();
            FIRST_TIME = false;
        }

        IS_RUNNING = false;
        passedTime = 4;
    }

    private void loadButtons() {
        BitmapFont font = new BitmapFont();
        Skin skin = new Skin();
        TextureAtlas textureAtlas = new TextureAtlas();
        TextButton.TextButtonStyle b1 = new TextButton.TextButtonStyle(),
                b2 = new TextButton.TextButtonStyle(),
                b3 = new TextButton.TextButtonStyle();

        MainMenuView.readTexture("play-up", "play-up.png", textureAtlas);
        MainMenuView.readTexture("back-up", "back-up.png", textureAtlas);
        MainMenuView.readTexture("back-down", "back-down.png", textureAtlas);
        MainMenuView.readTexture("play-down", "play-down.png", textureAtlas);
        MainMenuView.readTexture("settings-up", "settings-up.png", textureAtlas);
        MainMenuView.readTexture("settings-down", "settings-down.png", textureAtlas);
        MainMenuView.readTexture("exit-up", "exit-up.png", textureAtlas);
        MainMenuView.readTexture("exit-down", "exit-down.png", textureAtlas);
        skin.addRegions(textureAtlas);

        b1.font = font; b2.font = font; b3.font = font;

        //for the settings button
        b1.up = skin.getDrawable("play-up");
        b1.down = skin.getDrawable("play-down");
        RESUME_BUTTON = new TextButton("", b1);
        RESUME_BUTTON.setPosition(Gdx.graphics.getWidth() / 2f - RESUME_BUTTON.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f + RESUME_BUTTON.getHeight() / 2f);

        //for the Exit button
        b2.up = skin.getDrawable("settings-up");
        b2.down = skin.getDrawable("settings-down");
        RESTART_BUTTON = new TextButton("", b2);
        RESTART_BUTTON.setPosition(RESUME_BUTTON.getX(), RESUME_BUTTON.getY() - RESTART_BUTTON.getHeight());

        b3.up = skin.getDrawable("exit-up");
        b3.down = skin.getDrawable("exit-down");
        EXIT_BUTTON = new TextButton("", b3);
        EXIT_BUTTON.setPosition(RESTART_BUTTON.getX(), RESTART_BUTTON.getY() - EXIT_BUTTON.getHeight());

        stage.addActor(RESUME_BUTTON);
        stage.addActor(RESTART_BUTTON);
        stage.addActor(EXIT_BUTTON);

        addListeners();
    }

    private void addListeners() {
        RESUME_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
                disableButtons();
                passedTime = 4;
            }
        });

        RESTART_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
                disableButtons();
                GameModel.getInstance().dispose();
                GameController.getInstance().dispose();
                game.setScreen(new GameView(game, TWO_PLAYERS));
            }
        });

        EXIT_BUTTON.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuView.playClick();
                game.setScreen(new MainMenuView(game, false));
            }
        });
    }

    private void disableButtons() {
        RESUME_BUTTON.setDisabled(true);
        RESUME_BUTTON.setVisible(false);
        RESTART_BUTTON.setDisabled(true);
        RESTART_BUTTON.setVisible(false);
        EXIT_BUTTON.setDisabled(true);
        EXIT_BUTTON.setVisible(false);
        //IS_RUNNING = true;
        IS_PAUSED = false;
    }

    private void enableButtons() {
        IS_PAUSED = true;
        IS_RUNNING = false;
        RESUME_BUTTON.setDisabled(false);
        RESUME_BUTTON.setVisible(true);
        RESTART_BUTTON.setDisabled(false);
        RESTART_BUTTON.setVisible(true);
        EXIT_BUTTON.setDisabled(false);
        EXIT_BUTTON.setVisible(true);
    }

    private void getSoundEffects() {
        EDGE_HIT_EFFECT = game.getAssetManager().get("edge-hit.wav");
        JUMP_EFFECT = game.getAssetManager().get("jump.wav");
        END_EFFECT = game.getAssetManager().get("dead.mp3");
    }

    /**
     * creates a score Label shown on screen.
     * @return the score Label.
     */
    private void createLabels() {
        scoreLabel = new Label(Integer.toString(GameModel.getInstance().getGAME_SCORE()),
                new Label.LabelStyle(new BitmapFont(), null));
        scoreLabel.setPosition(VIEWPORT_WIDTH / PIXEL_TO_METER  / 2f,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f);
        scoreLabel.setFontScale(20);

        scoreLabel.setColor(Color.WHITE);
    }

    public static void playHit() {
        if (game.isFX_ENABLED())
            EDGE_HIT_EFFECT.play();
    }

    public static void playJump() {
        if (game.isFX_ENABLED())
            JUMP_EFFECT.play();
    }

    /**
     * Creates the camera used to show the viewport.
     * @return the camera
     */
    private OrthographicCamera createCamera() {
        VIEWPORT_HEIGHT = VIEWPORT_WIDTH * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        OrthographicCamera camera = new OrthographicCamera(VIEWPORT_WIDTH / PIXEL_TO_METER,
                VIEWPORT_HEIGHT / PIXEL_TO_METER);

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
     * Renders this screen.
     *
     * @param delta time since last renders in seconds.
     */
    @Override
    public void render(float delta) {

        //if (!END)
        handleInputs(delta);

        if (READY_PLAYER_ONE && READY_PLAYER_TWO) {
            if (IS_RUNNING) {
                GameModel.getInstance().getBird().get(0).setFlying(true);
                if (isTWO_PLAYERS())
                    GameModel.getInstance().getBird().get(1).setFlying(true);

                END = GameController.getInstance().update(delta, serverClient);
                GameController.getInstance().removeFlagged();
            }
        } else {
            GameModel.getInstance().getBird().get(0).setFlying(false);
            if (isTWO_PLAYERS())
                GameModel.getInstance().getBird().get(1).setFlying(false);
        }

        game.getBatch().setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor( t1/255f, t2/255f, t3/255f, 1 );

        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        game.getBatch().begin();
        drawBackground();

        if (END) {
            if (game.isMusicEnabled())
                game.getBACKGROUND_MUSIC().pause();

            if (!endHasPlayed) {
                END_EFFECT.play();
                endHasPlayed = true;
            }

            GameModel.getInstance().getBird().get(0).setFlying(false);
            if (!TWO_PLAYERS) {
                drawScore();
                passedTime += delta;
            }
            else {
                drawWinner();
                GameModel.getInstance().getBird().get(1).setFlying(false);
                passedTime += delta;
            }
            IS_RUNNING = false;

            if (passedTime > 3) {
                if (TWO_PLAYERS) {
                    if (MyBouncyBird.getPLAYER_ONE_LIFES() < 1
                            || MyBouncyBird.getPLAYER_TWO_LIFES() < 1) {

                        if (game.isMusicEnabled())
                            MyBouncyBird.setPLAYER_ONE_LIFES(3);
                        MyBouncyBird.setPLAYER_TWO_LIFES(3);
                        FIRST_TIME = true;
                        game.setScreen(new MainMenuView(game, false));
                    }
                    else {
                        GameModel.getInstance().dispose();
                        GameController.getInstance().dispose();
                    }
                }
                else {
                    if (game.isMusicEnabled())
                        game.getBACKGROUND_MUSIC().play();
                    FIRST_TIME = true;
                    game.setScreen(new MainMenuView(game, false));
                }
            }
        }
        if (TWO_PLAYERS && !END && !IS_RUNNING)
            drawLifes();

        drawEntities();

        if (!READY_PLAYER_ONE || !READY_PLAYER_TWO)
            drawReadies();
        else {
            if (!IS_RUNNING && !END && !IS_PAUSED) {
                passedTime -= delta;
                countdownTimer();
            }
        }

        game.getBatch().end();


        if (DEBUG_PHYSICS) {
            debugCamera = camera.combined.cpy();
            debugCamera.scl(1 / PIXEL_TO_METER);
            debugRenderer.render(GameController.getInstance().getWorld(), debugCamera);
        }

        stage.draw();
    }

    private void drawWinner() {

        if (MyBouncyBird.getPLAYER_ONE_LIFES() < 1 || MyBouncyBird.getPLAYER_TWO_LIFES() < 1) {

            Texture t1 = game.getAssetManager().get(MyBouncyBird.getPLAYER_TWO_LIFES() < 1 ? "win_p1.png" : "win_p2.png", Texture.class);

            Image i1 = new Image(t1);

            i1.scaleBy(2);
            i1.setPosition(0,
                    VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f);

            i1.draw(game.getBatch(), 0.8f);
        }
    }

    private void countdownTimer() {
        Texture t1;
        boolean draw = true;

        if (passedTime > 3)
            t1 = game.getAssetManager().get("start_3.png", Texture.class);
        else if (passedTime > 2)
            t1 = game.getAssetManager().get("start_2.png", Texture.class);
        else if (passedTime > 1)
            t1 = game.getAssetManager().get("start_1.png", Texture.class);
        else if (passedTime > 0)
            t1 = game.getAssetManager().get("start.png", Texture.class);
        else {
            t1 = game.getAssetManager().get("start.png", Texture.class);
            draw = false;
            IS_RUNNING = true;
            passedTime = 0;
        }

        Image i1 = new Image(t1);

        i1.scaleBy(2);
        i1.setPosition(draw ? 0 : 10000,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f - VIEWPORT_HEIGHT / PIXEL_TO_METER / (TWO_PLAYERS ? -10f : 6f));
        i1.draw(game.getBatch(), 0.8f);

    }

    /**
     * Upon losing the scoreing is displayed on screen.
     */
    private void drawScore() {
        Texture t1 = game.getAssetManager().get("score_template.png", Texture.class);
        Image i1 = new Image(t1);
        i1.scaleBy(2);
        i1.setPosition(0,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f - VIEWPORT_HEIGHT / PIXEL_TO_METER / (TWO_PLAYERS ? -10f : 6f));

        i1.draw(game.getBatch(), 0.8f);
    }

    /**
     * Draws the "are you ready textures" on screen.
     */
    private void drawReadies() {
        Texture t2; Image i2 = new Image();
        Texture t1 = game.getAssetManager().get("ready_p1.png", Texture.class);
        Image i1 = new Image(t1);
        i1.scaleBy(2);
        i1.setPosition(0,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f - VIEWPORT_HEIGHT / PIXEL_TO_METER / (TWO_PLAYERS ? -10f : 6f));

        if (TWO_PLAYERS) {
            t2 = game.getAssetManager().get("ready_p2.png", Texture.class);
            i2 = new Image(t2);
            i2.scaleBy(2);
            i2.setPosition(0,
                    12f);

        }

        if (!READY_PLAYER_ONE)
            i1.draw(game.getBatch(), 0.8f);
        if (!READY_PLAYER_TWO)
            i2.draw(game.getBatch(), 0.8f);
    }

    public static boolean isTWO_PLAYERS() {
        return TWO_PLAYERS;
    }

    /**
     * Handles any inputs and passes them to the controller.
     *
     * @param delta time since last time inputs where handled in seconds
     */
    private void handleInputs(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                playJump();
                GameController.getInstance().jump(0);
            }
            READY_PLAYER_ONE = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && isTWO_PLAYERS()) {
            if (READY_PLAYER_TWO && READY_PLAYER_ONE && IS_RUNNING) {
                playJump();
                GameController.getInstance().jump(1);
            }
            READY_PLAYER_TWO = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && IS_RUNNING) {
            MainMenuView.playClick();
            GameModel.getInstance().getBird().get(0).setFlying(false);
            enableButtons();
        }
        if (!IS_PAUSED) {

        }
        if (Gdx.input.justTouched()){
            if (TWO_PLAYERS) {
                if (Gdx.input.getY() > Gdx.graphics.getHeight() / 2f) {
                    if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                        playJump();
                        GameController.getInstance().jump(1);
                    }
                    READY_PLAYER_TWO = true;
                }
                else if (Gdx.input.getY() < Gdx.graphics.getHeight() / 2f) {
                    if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                        playJump();
                        GameController.getInstance().jump(0);
                    }
                    READY_PLAYER_ONE = true;
                }
            }
            else {

                if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                    playJump();
                    GameController.getInstance().jump(0);
                }
                READY_PLAYER_ONE = true;
            }
        }

    }

    /**
     * Draws the entities to the screen.
     */
    private void drawEntities() {
        List<SpikeModel> floor_ceiling_spikes = GameModel.getInstance().getFloor_Ceiling_spikes();
        List<SpikeModel> left_wall_spikes = GameModel.getInstance().getLeft_wall_spikes();
        List<SpikeModel> right_wall_spikes = GameModel.getInstance().getRight_wall_spikes();

        List<EdgeModel> edges = GameModel.getInstance().getEdges();

        //TODO pontos separados para os dois

        if (!TWO_PLAYERS) {
            scoreLabel.setText(Integer.toString(GameModel.getInstance().getGAME_SCORE()));
            scoreLabel.draw(game.getBatch(), 1);
        }

        for (int i = 0; i < floor_ceiling_spikes.size(); i++) {
            EntityView view = ViewFactory.makeView(game, floor_ceiling_spikes.get(i));
            view.update(floor_ceiling_spikes.get(i));
            view.draw(game.getBatch());
        }
        for (int i = 0; i < right_wall_spikes.size(); i++) {
            EntityView view = ViewFactory.makeView(game, right_wall_spikes.get(i));
            view.update(right_wall_spikes.get(i));
            view.draw(game.getBatch());
        }
        for (int i = 0; i < left_wall_spikes.size(); i++) {
            EntityView view = ViewFactory.makeView(game, left_wall_spikes.get(i));
            view.update(left_wall_spikes.get(i));
            view.draw(game.getBatch());
        }

        for (EdgeModel edge: edges) {
            EntityView view = ViewFactory.makeView(game, edge);
            view.update(edge);
            view.draw(game.getBatch());
        }

        if (GameModel.getInstance().getBonus() != null) {
            BonusModel bonus = GameModel.getInstance().getBonus();
            EntityView bonusView = ViewFactory.makeView(game, bonus);
            bonusView.update(bonus);
            bonusView.draw(game.getBatch());
        }

        BirdModel bird = GameModel.getInstance().getBird().get(0);
        EntityView view = ViewFactory.makeView(game, bird);
        view.update(bird);
        view.draw(game.getBatch());

        if (TWO_PLAYERS) {
            BirdModel bird2 = GameModel.getInstance().getBird().get(1);
            EntityView view2 = ViewFactory.makeView(game, bird2);
            view2.update(bird2);
            view2.draw(game.getBatch());
        }
    }

    private void drawLifes() {
        Texture t1 = game.getAssetManager().get("hearts" + GameModel.getInstance().getBird().get(0).getNUMBER_LIFES() + "_p1.png", Texture.class);
        Image i1 = new Image(t1);
        i1.scaleBy(2);
        i1.setPosition(VIEWPORT_WIDTH / PIXEL_TO_METER / 8f,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f - VIEWPORT_HEIGHT / PIXEL_TO_METER / 6f);

        Texture t2 = game.getAssetManager().get("hearts" + GameModel.getInstance().getBird().get(1).getNUMBER_LIFES() + "_p2.png", Texture.class);
        Image i2 = new Image(t2);
        i2.scaleBy(2);
        i2.setPosition(VIEWPORT_WIDTH / PIXEL_TO_METER / 2f + VIEWPORT_WIDTH / PIXEL_TO_METER / 6f,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f - VIEWPORT_HEIGHT / PIXEL_TO_METER / 6f);

        i1.draw(game.getBatch(), 0.8f);
        i2.draw(game.getBatch(), 0.8f);
    }

    /**
     * Draws the background
     */
    private void drawBackground() {
        Texture background = game.getAssetManager().get("background.png", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        game.getBatch().draw(background, 0, 0, 0, 0, (int)(ROOM_WIDTH / PIXEL_TO_METER), (int) (ROOM_HEIGHT / PIXEL_TO_METER));
    }

    @Override
    public void create() {
    }

    @Override
    public void render() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK) {
            if (!END) {
                MainMenuView.playClick();
                GameModel.getInstance().getBird().get(0).setFlying(false);
                enableButtons();
            }
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

package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.List;

import static com.aor.bouncy.controller.GameController.*;

/**
 * A view representing the game screen. Draws all the other views and
 * controls the camera.
 */
public class GameView extends ScreenAdapter implements InputProcessor, ApplicationListener {
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

    /**
     * Label used to display the score
     * on one player mode only.
     */
    Label scoreLabel;

    /**
     * Object of the TextButton class representing the resume button.
     */
    private TextButton RESUME_BUTTON;

    /**
     * Object of the TextButton class representing the restart button.
     */
    private TextButton RESTART_BUTTON;

    /**
     * Object of the TextButton class representing the exit button.
     */
    private TextButton EXIT_BUTTON;

    /**
     * True if the game is on two player mode, false if one player.
     */
    private static boolean TWO_PLAYERS;

    /**
     * Object of the Sound class for the edge hit effect.
     */
    private static Sound EDGE_HIT_EFFECT;

    /**
     * Object of the Sound class for the jump effect.
     */
    private static Sound JUMP_EFFECT;

    /**
     * Object of the Sound class for the end sound.
     */
    private static Sound END_EFFECT;

    /**
     * Stage where all buttons will be drawn.
     */
    private Stage stage;

    /**
     * Variable telling us if it is the first time running this instance.
     */
    private static boolean FIRST_TIME = true;

    /**
     * Used to prevent the end sound to play multiple times at a time.
     */
    private boolean endHasPlayed = false;

    /**
     * Variable updated by the controller to tell if game over.
     */
    private boolean END = false;

    private static long lastShake = 0;

    private static int lastTouch = 0;
    private boolean firstTimeResize = true;

    /**
     * Creates this screen.
     *
     * @param game The game that called this screen
     */
    public GameView(MyBouncyBird game, boolean TWO_PLAYERS) {
        this.game = game;
        this.TWO_PLAYERS = TWO_PLAYERS;
        this.stage = new Stage();

        //Enable button clicking and game touching.
        InputMultiplexer multiplexer = new InputMultiplexer(this, stage);
        Gdx.input.setInputProcessor(multiplexer);

        //Allow back key used for android.
        Gdx.input.setCatchBackKey(true);

        camera = createCamera();

        if (!TWO_PLAYERS) {
            createLabel();
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

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (!firstTimeResize)
            game.setScreen(new GameView(game, TWO_PLAYERS));
        firstTimeResize = false;
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

        b1.font = font;
        b2.font = font;
        b3.font = font;

        //for the resume button
        b1.up = skin.getDrawable("play-up");
        b1.down = skin.getDrawable("play-down");
        RESUME_BUTTON = new TextButton("", b1);
        RESUME_BUTTON.setPosition(Gdx.graphics.getWidth() / 2f - RESUME_BUTTON.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f + RESUME_BUTTON.getHeight() / 2f);

        //for the restart button
        b2.up = skin.getDrawable("settings-up");
        b2.down = skin.getDrawable("settings-down");
        RESTART_BUTTON = new TextButton("", b2);
        RESTART_BUTTON.setPosition(RESUME_BUTTON.getX(), RESUME_BUTTON.getY() - RESTART_BUTTON.getHeight());

        //for the exit button
        b3.up = skin.getDrawable("exit-up");
        b3.down = skin.getDrawable("exit-down");
        EXIT_BUTTON = new TextButton("", b3);
        EXIT_BUTTON.setPosition(RESTART_BUTTON.getX(), RESTART_BUTTON.getY() - EXIT_BUTTON.getHeight());

        stage.addActor(RESUME_BUTTON);
        stage.addActor(RESTART_BUTTON);
        stage.addActor(EXIT_BUTTON);

        addListeners();
    }

    /**
     * Add the listeners for this view's buttons.
     */
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

    /**
     * Disables and hides every button and unpauses the controller updates.
     */
    private void disableButtons() {
        RESUME_BUTTON.setDisabled(true);
        RESUME_BUTTON.setVisible(false);
        RESTART_BUTTON.setDisabled(true);
        RESTART_BUTTON.setVisible(false);
        EXIT_BUTTON.setDisabled(true);
        EXIT_BUTTON.setVisible(false);
        IS_PAUSED = false;
    }

    /**
     * Enables and activates every button and pauses the controller updates.
     */
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

    /**
     * Initializes this view's sound effects.
     */
    private void getSoundEffects() {
        EDGE_HIT_EFFECT = game.getAssetManager().get("edge-hit.wav");
        JUMP_EFFECT = game.getAssetManager().get("jump.wav");
        END_EFFECT = game.getAssetManager().get("dead.mp3");
    }

    /**
     * creates a score Label shown on screen.
     *
     * @return the score Label.
     */
    private void createLabel() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("label.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 200;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 4;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        scoreLabel = new Label(Integer.toString(GameModel.getInstance().getGAME_SCORE()),
                new Label.LabelStyle(font, null));
        scoreLabel.setPosition(VIEWPORT_WIDTH / PIXEL_TO_METER / 2f,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2.5f);

        scoreLabel.setColor(Color.WHITE);
    }

    /**
     * Plays the edge's collision sound effect.
     * Accessible to all classes.
     */
    public static void playHit() {
        if (game.isFX_ENABLED())
            EDGE_HIT_EFFECT.play();
    }

    /**
     * Plays the jumping sound effect.
     * Accessible to all classes.
     */
    public static void playJump() {
        if (game.isFX_ENABLED())
            JUMP_EFFECT.play();
    }

    /**
     * Creates the camera used to show the viewport.
     *
     * @return the camera
     */
    private OrthographicCamera createCamera() {
        VIEWPORT_HEIGHT = ((float) Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth()) * VIEWPORT_WIDTH;
        OrthographicCamera camera = new OrthographicCamera(VIEWPORT_WIDTH / PIXEL_TO_METER,
                VIEWPORT_HEIGHT / PIXEL_TO_METER);

        camera.position.set(camera.viewportWidth / 2f,
                camera.viewportHeight / 2f,
                0);
        camera.update();

        if (DEBUG_PHYSICS) {
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

        handleInputs(delta);
        System.out.println("Altura: " + Gdx.graphics.getHeight() * PIXEL_TO_METER);
        System.out.println("Passaro: " + GameModel.getInstance().getBird().get(0).getY());

        //While not both players are ready the controller is not activated.
        if (READY_PLAYER_ONE && READY_PLAYER_TWO) {
            if (IS_RUNNING) {
                GameModel.getInstance().getBird().get(0).setFlying(true);
                if (isTWO_PLAYERS())
                    GameModel.getInstance().getBird().get(1).setFlying(true);

                //END boolean updated from the controller.
                END = GameController.getInstance().update(delta);
                if (GameController.getInstance().isToPlaySound())
                    playHit();
                GameController.getInstance().removeFlagged();
            } else {
                GameModel.getInstance().getBird().get(0).setFlying(false);
                if (isTWO_PLAYERS())
                    GameModel.getInstance().getBird().get(1).setFlying(false);
            }
        } else {
            //If at least one is not ready, the bird is not flying.
            GameModel.getInstance().getBird().get(0).setFlying(false);
                if (isTWO_PLAYERS())
                    GameModel.getInstance().getBird().get(1).setFlying(false);//If at least one is not ready, the bird is not flying.
        }

        game.getBatch().setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0 / 255f, 0 / 255f, 0 / 255f, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.getBatch().begin();
        drawBackground();

        //Game Over happened.
        if (END) {
            //Play sound effects.
            if (game.isMusicEnabled())
                game.getBACKGROUND_MUSIC().pause();

            if (!endHasPlayed) {
                END_EFFECT.play();
                endHasPlayed = true;
            }

            //Stop the bird's flying movement.
            GameModel.getInstance().getBird().get(0).setFlying(false);
            if (!TWO_PLAYERS) {
                drawScore();
                passedTime += delta;
            } else {
                drawWinner();
                GameModel.getInstance().getBird().get(1).setFlying(false);
                passedTime += delta;
            }
            //Stop the controller updates.
            IS_RUNNING = false;

            if (passedTime > 3) {
                if (TWO_PLAYERS) {
                    //Restart the next round of the game if there are still lives.
                    if (MyBouncyBird.getPLAYER_ONE_LIVES() < 1
                            || MyBouncyBird.getPLAYER_TWO_LIVES() < 1) {

                        if (game.isMusicEnabled())
                            MyBouncyBird.setPLAYER_ONE_LIVES(3);
                        MyBouncyBird.setPLAYER_TWO_LIVES(3);
                        FIRST_TIME = true;
                        game.setScreen(new MainMenuView(game, false));
                    } else {
                        GameModel.getInstance().dispose();
                        GameController.getInstance().dispose();
                        game.setScreen(new GameView(game, true));
                    }
                } else {
                    if (game.isMusicEnabled())
                        game.getBACKGROUND_MUSIC().play();
                    FIRST_TIME = true;
                    game.setScreen(new MainMenuView(game, false));
                }
            }
        }
        if (TWO_PLAYERS && !END && !IS_RUNNING)
            drawLives();

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

    /**
     * Draws the game winner's image.
     */
    private void drawWinner() {

        if (MyBouncyBird.getPLAYER_ONE_LIVES() < 1 || MyBouncyBird.getPLAYER_TWO_LIVES() < 1) {

            Texture t1 = game.getAssetManager().get(MyBouncyBird.getPLAYER_TWO_LIVES() < 1 ? "win_p1.png" : "win_p2.png", Texture.class);

            Image i1 = new Image(t1);

            i1.scaleBy(2);
            i1.setPosition(0,
                    VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f);

            i1.draw(game.getBatch(), 0.8f);
        }
    }

    /**
     * Draws the countdown seconds' images.
     */
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
     * Upon losing the scoring is displayed on screen.
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
        Texture t2;
        Image i2 = new Image();
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

    /**
     * Returns the IS TWO PLAYERS flag of the game.
     *
     * @return true if it is TWO PLAYERS, false otherwise.
     */
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
            //First touch is to ready up only, prevent controller updates right away.
            if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                playJump();
                GameController.getInstance().jump(0);
            }
            READY_PLAYER_ONE = true;
        } else if (System.currentTimeMillis() - lastShake > 250 && !TWO_PLAYERS) {

            float gyroY = Gdx.input.getGyroscopeY();

            if (Math.abs(gyroY) > 5) {
                ++lastTouch;
                if (lastTouch >= 0)
                    GameController.getInstance().jump(0);
                lastShake = System.currentTimeMillis();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && isTWO_PLAYERS()) {
                //First touch is to ready up only, prevent controller updates right away.
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

        //Score label for one player mode.
        if (!TWO_PLAYERS) {
            scoreLabel.setText(Integer.toString(GameModel.getInstance().getGAME_SCORE()));
            scoreLabel.draw(game.getBatch(), 1);
        }

        //Ceiling and floor spikes.
        for (int i = 0; i < floor_ceiling_spikes.size(); i++) {
            EntityView view = ViewFactory.makeView(game, floor_ceiling_spikes.get(i));
            view.update(floor_ceiling_spikes.get(i));
            view.draw(game.getBatch());
        }

        //Right wall spikes.
        for (int i = 0; i < right_wall_spikes.size(); i++) {
            EntityView view = ViewFactory.makeView(game, right_wall_spikes.get(i));
            view.update(right_wall_spikes.get(i));
            view.draw(game.getBatch());
        }

        //Left wall spikes.
        for (int i = 0; i < left_wall_spikes.size(); i++) {
            EntityView view = ViewFactory.makeView(game, left_wall_spikes.get(i));
            view.update(left_wall_spikes.get(i));
            view.draw(game.getBatch());
        }

        //Edges
        for (EdgeModel edge: edges) {
            EntityView view = ViewFactory.makeView(game, edge);
            view.update(edge);
            view.draw(game.getBatch());
        }

        //Bonus
        if (GameModel.getInstance().getBonus() != null) {
            BonusModel bonus = GameModel.getInstance().getBonus();
            EntityView bonusView = ViewFactory.makeView(game, bonus);
            bonusView.update(bonus);
            bonusView.draw(game.getBatch());
        }

        //Bird ONE
        BirdModel bird = GameModel.getInstance().getBird().get(0);
        EntityView view = ViewFactory.makeView(game, bird);
        view.update(bird);
        view.draw(game.getBatch());

        //Bird TWO
        if (TWO_PLAYERS) {
            BirdModel bird2 = GameModel.getInstance().getBird().get(1);
            EntityView view2 = ViewFactory.makeView(game, bird2);
            view2.update(bird2);
            view2.draw(game.getBatch());
        }
    }

    /**
     * Prints the respective Image for the current amount of lives of each player.
     */
    private void drawLives() {
        Texture t1 = game.getAssetManager().get("hearts" + GameModel.getInstance().getBird().get(0).getNUMBER_LIVES() + "_p1.png", Texture.class);
        Image i1 = new Image(t1);
        i1.scaleBy(2);
        i1.setPosition(VIEWPORT_WIDTH / PIXEL_TO_METER / 8f,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f - VIEWPORT_HEIGHT / PIXEL_TO_METER / 6f);

        Texture t2 = game.getAssetManager().get("hearts" + GameModel.getInstance().getBird().get(1).getNUMBER_LIVES() + "_p2.png", Texture.class);
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
        //Catch android back key.
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

    int bluePlayerPointer, redPlayerPointer;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //First touch is to ready up only, prevent controller updates right away.
        if (TWO_PLAYERS) {
            if (Gdx.input.getY() > Gdx.graphics.getHeight() / 2f) {
                //If touch height higher than half total height, touch is to player 1.
                if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                    playJump();
                    bluePlayerPointer = pointer;
                }
                READY_PLAYER_TWO = true;
            }
            else if (Gdx.input.getY() < Gdx.graphics.getHeight() / 2f) {
                //If touch height lower than half total height, touch is to player 2.
                if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                    playJump();
                    redPlayerPointer = pointer;
                }
                READY_PLAYER_ONE = true;
            }
        }
        else {
            //First touch is to ready up only, prevent controller updates right away.
            if (READY_PLAYER_ONE && READY_PLAYER_TWO && IS_RUNNING) {
                playJump();
                lastTouch = 1;
                redPlayerPointer = pointer;
            }
            READY_PLAYER_ONE = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (IS_RUNNING) {
            if (pointer == bluePlayerPointer)
                GameController.getInstance().jump(1);
            if (pointer == redPlayerPointer)
                GameController.getInstance().jump(0);
        }
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

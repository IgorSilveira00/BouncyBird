package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.GameModel;
import com.aor.bouncy.model.entities.*;
import com.aor.bouncy.view.entities.EntityView;
import com.aor.bouncy.view.entities.ViewFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.ArrayList;
import java.util.List;

import static com.aor.bouncy.controller.GameController.*;

/**
 * A view representing the game screen. Draws all the other views and
 * controls the camera.
 */
public class GameView extends ScreenAdapter {
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

    private List<Label> scoreLabels = new ArrayList<Label>();

    private static boolean TWO_PLAYERS;

    private static Sound EDGE_HIT_EFFECT;

    private static Sound JUMP_EFFECT;

    private static Sound END_EFFECT;

    private static boolean FIRST_TIME = true;

    private boolean endHasPlayed = false;

    private boolean END = false;

    /**
     * Creates this screen.
     * @param game The game that called this screen
     */
    public GameView(MyBouncyBird game, boolean TWO_PLAYERS) {
        this.game = game;
        this.TWO_PLAYERS = TWO_PLAYERS;

        camera = createCamera();
        //menuView.setCameras(debugRenderer, debugCamera);

        if (!TWO_PLAYERS) {
            createLabels();
            READY_PLAYER_TWO = true;
        }

        if (FIRST_TIME) {
            getSoundEffects();
            FIRST_TIME = false;
        }

        IS_RUNNING = false;
        passedTime = 4;

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
        Label scoreLabel = new Label(Integer.toString(GameModel.getInstance().getGAME_SCORE()),
                new Label.LabelStyle(new BitmapFont(), null));
        scoreLabel.setPosition(VIEWPORT_WIDTH / PIXEL_TO_METER  / 2f,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f);
        scoreLabel.setFontScale(20);

        scoreLabel.setColor(Color.WHITE);

        scoreLabels.add(scoreLabel);
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

         handleInputs(delta);

        if (READY_PLAYER_ONE && READY_PLAYER_TWO) {
            if (IS_RUNNING) {
            END = GameController.getInstance().update(delta);
            GameController.getInstance().removeFlagged();
            }
        }

        game.getBatch().setProjectionMatrix(camera.combined);

//        Gdx.gl.glClearColor( 103/255f, 69/255f, 117/255f, 1 );
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
                GameModel.getInstance().getBird().get(1).setFlying(false);
                passedTime += delta;
            }
            IS_RUNNING = false;

            if (passedTime > 3) {
                if (game.isMusicEnabled())
                    game.getBACKGROUND_MUSIC().play();
                game.setScreen(new MainMenuView(game, false));
            }
        }
        if (TWO_PLAYERS)
            drawLifes();

        drawEntities();

        if (!READY_PLAYER_ONE || !READY_PLAYER_TWO)
            drawReadies();
        else {
            if (!IS_RUNNING && !END) {
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            MainMenuView.playClick();
            game.setScreen(new MainMenuView(game, false));
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
            scoreLabels.get(0).setText(Integer.toString(GameModel.getInstance().getGAME_SCORE()));
            scoreLabels.get(0).draw(game.getBatch(), 1);
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


           /* List<LifeModel> lifes = GameModel.getInstance().getLifes();

            EntityView lifeView1 = ViewFactory.makeView(game, lifes.get(0));
            lifeView1.update(lifes.get(0));
            lifeView1.draw(game.getBatch());

            EntityView lifeView2 = ViewFactory.makeView(game, lifes.get(1));
            lifeView2.update(lifes.get(1));
            lifeView2.draw(game.getBatch());*/
        }
    }

    private void drawLifes() {
        Texture t1 = game.getAssetManager().get("hearts" + (GameModel.getInstance().getBird().get(0).getNUMBER_LIFES() > 0 ? GameModel.getInstance().getBird().get(0).getNUMBER_LIFES() : 1) + "_p1.png", Texture.class);
        Image i1 = new Image(t1);
        i1.scaleBy(2);
        i1.setPosition(VIEWPORT_WIDTH / PIXEL_TO_METER / 8f,
                VIEWPORT_HEIGHT / PIXEL_TO_METER / 2f - VIEWPORT_HEIGHT / PIXEL_TO_METER / 6f);

        Texture t2 = game.getAssetManager().get("hearts" + (GameModel.getInstance().getBird().get(1).getNUMBER_LIFES() > 0 ? GameModel.getInstance().getBird().get(1).getNUMBER_LIFES() : 1) + "_p2.png", Texture.class);
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
}

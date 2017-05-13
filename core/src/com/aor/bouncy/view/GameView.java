package com.aor.bouncy.view;

import com.aor.bouncy.MyBouncyBird;
import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.GameModel;
import com.aor.bouncy.model.entities.BirdModel;
import com.aor.bouncy.model.entities.BonusModel;
import com.aor.bouncy.model.entities.EdgeModel;
import com.aor.bouncy.model.entities.SpikeModel;
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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
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
    private final MyBouncyBird game;

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

    private static Sound EDGE_HIT;

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

        createLabels();
        EDGE_HIT = game.getAssetManager().get("edge-hit.wav");
    }

    /**
     * creates a score Label shown on screen.
     * @return the score Label.
     */
    private void createLabels() {
        Label scoreLabel = new Label(Integer.toString(GameController.getGameScore()), new Label.LabelStyle(new BitmapFont(), null));
        scoreLabel.setPosition(Gdx.graphics.getWidth() + (TWO_PLAYERS ? Gdx.graphics.getWidth() / 2f: 0),
                Gdx.graphics.getHeight());
        scoreLabel.setFontScale(20);

        scoreLabel.setColor(TWO_PLAYERS ? Color.BLUE: Color.WHITE);

        scoreLabels.add(scoreLabel);

        if (TWO_PLAYERS) {
            scoreLabel = new Label(Integer.toString(GameController.getGameScore()), new Label.LabelStyle(new BitmapFont(), null));
            scoreLabel.setPosition(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 2f,
                    Gdx.graphics.getHeight());
            scoreLabel.setFontScale(20);

            scoreLabel.setColor(Color.RED);

            scoreLabels.add(scoreLabel);
        }
    }

    public static void playHit() {
        EDGE_HIT.play();
    }

    /**
     * Creates the camera used to show the viewport.
     * @return the camera
     */
    private OrthographicCamera createCamera() {
        OrthographicCamera camera = new OrthographicCamera(VIEWPORT_WIDTH / PIXEL_TO_METER,
                VIEWPORT_WIDTH / PIXEL_TO_METER * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()) -12);

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
        GameController.getInstance(game.isFX_ENABLED()).removeFlagged();

        handleInputs(delta);

        END = GameController.getInstance(game.isFX_ENABLED()).update(delta);

        game.getBatch().setProjectionMatrix(camera.combined);

//        Gdx.gl.glClearColor( 103/255f, 69/255f, 117/255f, 1 );
        Gdx.gl.glClearColor( t1/255f, t2/255f, t3/255f, 1 );

        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        game.getBatch().begin();
        //drawBackground();
        drawEntities();
        game.getBatch().end();

        if (END)
            game.setScreen(new MainMenuView(game, false));

        if (DEBUG_PHYSICS) {
            debugCamera = camera.combined.cpy();
            debugCamera.scl(1 / PIXEL_TO_METER);
            debugRenderer.render(GameController.getInstance(game.isFX_ENABLED()).getWorld(), debugCamera);
        }
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
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            GameController.getInstance(game.isFX_ENABLED()).jump(0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            GameController.getInstance(game.isFX_ENABLED()).jump(1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
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
        for (int i = 0; i < scoreLabels.size(); i++) {
        scoreLabels.get(i).setText(Integer.toString(GameController.getGameScore()));
        scoreLabels.get(i).draw(game.getBatch(), 1);
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

    /**
     * Draws the background
     */
    private void drawBackground() {
        Texture background = game.getAssetManager().get("background.png", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        game.getBatch().draw(background, 0, 0, 0, 0, (int)(ROOM_WIDTH / PIXEL_TO_METER), (int) (ROOM_HEIGHT / PIXEL_TO_METER));
    }
}

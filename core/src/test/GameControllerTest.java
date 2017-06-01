package test;

import com.aor.bouncy.controller.GameController;
import com.aor.bouncy.model.GameModel;
import com.aor.bouncy.model.entities.SpikeModel;
import com.aor.bouncy.view.GameView;

import java.util.List;

import static org.junit.Assert.*;

public class GameControllerTest extends GameTest{

    /**
     * Tests if the game has ended with no inputs for the bird's movement.
     * Thus for, the final bird height should be less than
     * half the game height.
     **/
    @org.junit.Test
    public void gameOverWithNoInputs() throws Exception {
        GameModel.getInstance().dispose();
        GameController.getInstance().dispose();

        boolean END = false;

        while (!END)
            END =  GameController.getInstance().update(0.1f);

        assertTrue(GameController.getBirdBodies().get(0).getY() < GameController.ROOM_HEIGHT / 2);
    }

    /**
     * Tests if the game has ended multiple consecutive inputs for the bird's movement.
     * Thus for, the final bird height should be less than
     * half the game height.
     */
    @org.junit.Test
    public void gameOverWithRelentlessInputs() throws Exception {
        GameModel.getInstance().dispose();
        GameController.getInstance().dispose();

        boolean END = false;
        int counter = 0;

        while (!END) {

            if (counter % 34 == 0) {
                GameController.getInstance().jump(0);  //apply jump to bird one
                System.out.println(GameController.getInstance().getBirdBodies().get(0).getY());
            }
            END =  GameController.getInstance().update(0.1f);
            counter++;
        }

        assertTrue(GameController.getInstance().getBirdBodies().get(0).getY() > GameController.ROOM_HEIGHT / 2);
    }

    /**
     * Test if when the bird changes direction (X speed simmetric) the sprite changes direction.
     * (model's head right boolean is now negative)
     * @throws Exception
     */
    @org.junit.Test
    public void headingChangeAfterEdgeHit() throws Exception {
        GameModel.getInstance().dispose();
        GameController.getInstance().dispose();

        boolean END = false;

        while (!END) {
            GameController.getInstance().setJumpEnabled(false);
            GameController.getInstance().update(0.01f);
            END = (GameController.getInstance().getBirdXSpeed() < 0 && !GameModel.getInstance().getBird().get(0).isHeadRight());
        }

        assertFalse(GameModel.getInstance().getBird().get(0).isHeadRight());
    }

    /**
     * Tests if a spike that collided with the bird changes it's texture.
     * The bird can only collide with a single spike.
     * @throws Exception
     */
    @org.junit.Test
    public void spikeColorChangeAfterCollision() throws Exception {
        GameModel.getInstance().dispose();
        GameController.getInstance().dispose();

        boolean END = false;

        while (!END) {
            END = GameController.getInstance().update(0.01f);
        }
        boolean hasRedTexture = false;

        //Get floor spikes. Bird has collided with them.
        List<SpikeModel> spikeModels = GameModel.getInstance().getFloor_Ceiling_spikes();
        int count = 0;

        for (SpikeModel spikeModel: spikeModels)
            if (!spikeModel.isNormalTexture()) {
                hasRedTexture = true;
                count++;
            }

        assertTrue(hasRedTexture);
        assertTrue(count == 1);
    }

    /**
     * Tests if a bonus spawns in then somewhere in time disappears.
     * @throws Exception
     */
    @org.junit.Test
    public void bonusAppearsAndDisappears() throws Exception {
        GameModel.getInstance().dispose();
        GameController.getInstance().dispose();

        GameController.getInstance().setJumpEnabled(false);
        GameController.getInstance().setLosingEnabled(false);

        //Starts as null.
        assertTrue(GameModel.getInstance().getBonus() == null);

        boolean END1 = false;
        boolean END2 = false;

        while (!END1 || !END2) {
            GameController.getInstance().update(0.01f);
            GameController.getInstance().removeFlagged();

            //Has spawned.
            if (GameModel.getInstance().getBonus() != null)
                END1 = true;

            if (END1)
                if (GameModel.getInstance().getBonus() == null) {
                    //Has dissapeared again.
                    END2 = true;
                }
        }
    }

    /**
     * Test if score is incremented upon hitting an edge.
     */
    @org.junit.Test
    public void scoreIncrementAfterEdgeHit() {
        GameModel.getInstance().dispose();
        GameController.getInstance().dispose();

        GameController.getInstance().setJumpEnabled(false);
        GameController.getInstance().setLosingEnabled(false);

        assertTrue(GameModel.getInstance().getGAME_SCORE() == 0);

        boolean END1 = false;

        float currentSpeed = GameController.getInstance().getBirdXSpeed();
        int currentScore = 0;

        while (!END1) {
            GameController.getInstance().update(0.01f);

            if (GameController.getInstance().getBirdXSpeed() == currentSpeed * -1)
                if (GameModel.getInstance().getGAME_SCORE() == currentScore + 1)
                    END1 = true;

            currentScore = GameModel.getInstance().getGAME_SCORE();
            currentSpeed = GameController.getInstance().getBirdXSpeed();
        }
    }

    /**
     * Test if score is incremented upon colliding with a bonus.
     */
    @org.junit.Test(timeout = 5000)
    public void scoreIncrementAfterBonusCatch() {
        GameModel.getInstance().dispose();
        GameController.getInstance().dispose();

        GameController.getInstance().setJumpEnabled(false);
        GameController.getInstance().setLosingEnabled(false);
        GameModel.getInstance().setBonusStill(true);

        assertTrue(GameModel.getInstance().getGAME_SCORE() == 0);

        boolean END1 = false;

        int currentScore = 0;

        while (!END1) {
            GameController.getInstance().update(0.01f);

            if (GameModel.getInstance().getBonus() != null) {

                if (GameController.getInstance().isBonusCollided())
                    if (GameModel.getInstance().getGAME_SCORE() == currentScore + 1)
                        END1 = true;
            }
            currentScore = GameModel.getInstance().getGAME_SCORE();
        }
    }
}
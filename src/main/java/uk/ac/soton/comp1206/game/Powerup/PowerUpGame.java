package uk.ac.soton.comp1206.game.Powerup;

import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.Components.Game.Powerups.PowerUpGrid;
import uk.ac.soton.comp1206.Components.Game.Powerups.PowerUpGrid.Direction;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Scenes.PowerUpScene;
import uk.ac.soton.comp1206.Utility.MultiMedia;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * A game of Tetrecs with powerups involved
 * The powerups are stored in an enum with a price and icon
 * 
 * @author tcs1g20
 */
public class PowerUpGame extends Game {
    //How many points the user has to spend on powerups
    private SimpleIntegerProperty spendingPoints = new SimpleIntegerProperty(0);

    public PowerUpGame(GameWindow gameWindow, Communicator communicator) {
        super(gameWindow, communicator);
    }

    @Override
    public void buildGame() {
        logger.info("Building power-up game");

        this.challengeScene = new PowerUpScene(this.gameWindow, power -> usePowerUp(power));
        this.gameWindow.setGameScene(this.challengeScene);

        this.challengeScene.setHighScore(this.gameWindow.getScoresScene().getHighScore());

        this.createPowerUps();
        this.setGameStartListener();

        this.setKeyBindings();

        this.setUserPropertyListeners();

        this.setTileClickListeners();


    }


    @Override
    protected void setUserPropertyListeners() {
        super.setUserPropertyListeners();

        this.spendingPoints.addListener(event -> {
            ((PowerUpScene)this.challengeScene).setPoints(this.spendingPoints.get());
        });
    }

    @Override
    protected void score(int lines, int blocks) {
        var scoreGained = 10 * lines * blocks * this.multiplier.get();

        this.score.set(this.score.get() + scoreGained);

        //Adds points gained to out spending points
        this.spendingPoints.set(this.spendingPoints.get() + scoreGained);
    }


    /**
     * Adds actions to all the powerups
     */
    private void createPowerUps() {
        /**
         * ((PowerUpGrid)this.challengeScene.getBoard()) -> game grid cast to the powerup grid version
         */

        PowerUp.PUSH_LEFT.setAction(() -> 
            ((PowerUpGrid)this.challengeScene.getBoard()).push(Direction.LEFT)
        );

        PowerUp.PUSH_RIGHT.setAction(() -> 
            ((PowerUpGrid)this.challengeScene.getBoard()).push(Direction.RIGHT)
        );

        PowerUp.PUSH_UP.setAction(() -> 
            ((PowerUpGrid)this.challengeScene.getBoard()).push(Direction.UP)
        );

        PowerUp.PUSH_DOWN.setAction(() -> 
            ((PowerUpGrid)this.challengeScene.getBoard()).push(Direction.DOWN)
        );

        PowerUp.NUKE.setAction(() -> {
            ((PowerUpGrid)this.challengeScene.getBoard()).clearAll();
            MultiMedia.playSFX("SFX/explode.wav");
        });

        PowerUp.NEW_PIECE.setAction(() -> this.nextPiece());

        PowerUp.RESTORE_LIFE.setAction(() -> this.addLife());

        PowerUp.DOUBLE_POINTS.setAction(() -> this.multiplier.set(this.multiplier.get()*2));

    }

    /**
     * Uses a power up if the user has enough points
     * @param pu The power up to be used
     */
    public boolean usePowerUp(PowerUp pu) {
        if (pu.getPrice() > this.spendingPoints.get()) {
            logger.info("Not enough points");
            MultiMedia.playSFX("SFX/fail.wav");
            return false;
        } else {
            logger.info("Power up {} executed", pu);
            this.spendingPoints.set(this.spendingPoints.get() - pu.getPrice());
            pu.execute();
            this.timeline.playFromStart();
            if (pu != PowerUp.DOUBLE_POINTS) this.afterPiece();

            return true;
        }
    }

    @Override
    public void stopGame() {
        logger.info("GAME OVER");
        this.timeline.stop();
        this.gameOver = true;

        this.gameWindow.revertScene();
    }

    /**
     * Listener called when the user uses a powerup in game
     */
    public interface UsePowerUpListener {
        public boolean usePower(PowerUp pu);
    }
}

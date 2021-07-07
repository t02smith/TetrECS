package uk.ac.soton.comp1206.game;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Event.Action;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Scenes.ChallengeScene;
import uk.ac.soton.comp1206.Scenes.ScoresScene;
import uk.ac.soton.comp1206.Utility.MultiMedia;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Singleplayer game class
 * Handles the game logic
 * 
 * @author tcs1g20
 */
public class Game {
    protected static final Logger logger = LogManager.getLogger(Game.class);

    //Communicator to interact with server when needed
    protected final Communicator communicator;

    //User game properties
    protected SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    protected SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    protected SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    protected SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    //Whether the game is still on going
    protected boolean gameOver;

    //The next piece to be played
    protected GamePiece currentPiece;

    //Piece in reserve that can be switched to
    protected GamePiece reservePiece;

    //Main game loop timeline
    protected Timeline timeline;

    //visuals
    protected GameWindow gameWindow;
    protected ChallengeScene challengeScene;
    protected ScoresScene scoresScene;

    public Game(GameWindow gameWindow, Communicator communicator) {
        logger.info("Starting game");
        this.gameWindow = gameWindow;
        this.communicator = communicator;
    }

    /**
     * Sets up the game logic and scene
     */
    public void buildGame() {
        this.setupCommunicator();

        this.challengeScene = new ChallengeScene(this.gameWindow);
        this.gameWindow.setGameScene(this.challengeScene);

        //Creates a score scene so we can collect the scores
        this.scoresScene = this.gameWindow.getScoresScene();
        this.challengeScene.setHighScore(this.scoresScene.getHighScore());
        
        //Submits a score once the game has ended
        this.scoresScene.addSubmitScoreListener((name, score) -> {
            logger.info("Submitting new score");
            this.communicator.send(
                String.format("HISCORE %s:%d", name, score)
            );
            
            Utility.writeToFile(
                "scores/localScores.txt", 
                name + ":" + String.valueOf(score) + "\n",
                true
            );
        });

        //Sets what to do when the game starts
        this.setGameStartListener();

        //Assigns key bindings
        this.setKeyBindings();

        //Assigns the listeners for the user's game properties
        this.setUserPropertyListeners();

        //Sets the listeners for clicking on any of the grids
        this.setTileClickListeners();

        //Builds the game scene
        this.challengeScene.build();

    }

    /**
     * Sets the game's keybindings
     * By using an enum we can override individual commands in child classes
     *  as well as add to it without having to rewrite the switch case as
     *  setOnKeyReleased only allows one event
     */
    protected void setKeyBindings() {
        //Next piece alterations
        Action.ROTATE_LEFT.setEvent(() -> {
            this.currentPiece.rotateLeft();
            this.challengeScene.setNextPiece(this.currentPiece);
        });

        Action.ROTATE_RIGHT.setEvent(() -> {
            this.currentPiece.rotateRight();
            this.challengeScene.setNextPiece(this.currentPiece);
        });

        Action.SWAP.setEvent(() -> this.swapNextPiece());

        //Place a piece
        Action.PLACE.setEvent(() -> this.insertSelected());

        //Movement on the board
        Action.MOVE_UP.setEvent(() -> this.moveSelected(0, -1));
        Action.MOVE_LEFT.setEvent(() -> this.moveSelected(-1, 0));
        Action.MOVE_RIGHT.setEvent(() -> this.moveSelected(1, 0));
        Action.MOVE_DOWN.setEvent(() -> this.moveSelected(0, 1));

        
        Action.ESCAPE.setEvent(() -> {
            logger.info("Returning to menu");
            this.stopGame();
            this.gameWindow.revertScene();
        });


    }

    /**
     * Sets up the actions when a tile is clicked
     * What happens depends on what grid
     */
    protected void setTileClickListeners() {
        //On the game grid
        this.challengeScene.addTileClickListener("game-grid", (x, y) -> {
            this.insertPiece(x, y);
        });

        //On the next piece grid
        this.challengeScene.addTileClickListener("next-piece", (x, y) -> {
            if (x == 0) { //click middle square to rotate
                this.currentPiece.rotateLeft();
                this.challengeScene.setNextPiece(this.currentPiece);
            } else {
                this.currentPiece.rotateRight();
                this.challengeScene.setNextPiece(this.currentPiece);
            }
        });

        //On the reserve piece grid
        this.challengeScene.addTileClickListener("reserve-piece", (x, y) -> {
            this.swapNextPiece();
        });
    }

    /**
     * Sets the properties for altering the user's 
     */
    protected void setUserPropertyListeners() {
        //When the score updates
        this.score.addListener(event -> {
            //updates UI
            this.challengeScene.updateScore(score.get());

            //updates level
            if (this.score.get()/1000 != this.level.get()) {
                this.level.set(this.score.get()/1000);
                logger.info("level increased to {}", this.level.get());
            }
        });
        
        //When the user levels up
        this.level.addListener(event -> {
            if (this.timeline == null) return;

            logger.info("Level changed to {}", this.level.get());
            this.challengeScene.updateLevel(this.level.get());

            MultiMedia.playSFX("SFX/level.wav");

            //Decrease the time given to place a piece
            this.timeline.stop();
            this.timeline.getKeyFrames().set(1, this.updateTime());
            logger.info("Time decreased to {}ms", this.getTimerDelay());

        });

        //When the multiplier changes
        this.multiplier.addListener(event -> {
            this.challengeScene.updateMultiplier(this.multiplier.get());
        });
    }

    /**
     * Sets the action called when the game starts
     */
    protected void setGameStartListener() {
        //Called when the game is started//
        this.gameWindow.addGameStartListener(() -> {
            this.resetGame();
            this.nextPiece();
            this.nextPiece();

            this.gameOver = false;           

            //start timer
            this.gameLoop();
        });
    }

    /**
     * Game loop to keep track of the time
     */
    protected void gameLoop() {
        var timer = this.challengeScene.getTimer();      

        this.timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(timer.progressProperty(), 1)),
            this.updateTime()
        );

        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }

    /**
     * Updates the time after a piece is played
     * @return
     */
    protected KeyFrame updateTime() {
        return new KeyFrame(Duration.millis(this.getTimerDelay()), e -> {
            this.loseLife();

            if (this.lives.get() < 0) this.stopGame();
            else {
                this.nextPiece();
                this.multiplier.set(1);
            }
        }, new KeyValue(this.challengeScene.getTimer().progressProperty(), 0));
    }

    /**
     * Stops the current game
     */
    public void stopGame() {
        logger.info("GAME OVER");
        this.timeline.stop();
        this.gameOver = true;

        //Bring up scoreboard
        this.scoresScene.setUserScore(this.score.get());
        this.scoresScene.setHasPlayed(true);

        this.gameWindow.replaceScene(this.scoresScene);


    }

    /**
     * Resets the game
     *  Puts properties back to default
     */
    public void resetGame() {
        logger.info("Resetting game");
        this.score.set(0);
        this.lives.set(3);
        this.level.set(0);
        this.multiplier.set(1);

    }

    /**
     * Called when a piece needs to be inserted
     * @param x
     * @param y
     */
    protected void insertPiece(int x, int y) {
        var grid = this.challengeScene.getBoard();

        if (!this.gameOver && grid.placePiece(this.currentPiece, x, y)) {
            MultiMedia.playSFX("SFX/place.wav");
            this.afterPiece();

            //Grabs the next piece
            this.nextPiece();
    
            //Resets the timer
            this.timeline.playFromStart();
        } else {
            MultiMedia.playSFX("SFX/fail.wav");
        }
    }

    /**
     * Called after a piece is played
     */
    protected void afterPiece() {
        var board = this.challengeScene.getBoard();

        //Buffers for which rows/columns are full
        //rows and columns share tiles so we can't remove any tiles before checking both
        var rowBuffer = new ArrayList<Integer>();
        var columnBuffer = new ArrayList<Integer>();

        for (int i = 0; i < this.challengeScene.getGridHeight(); i++) {
            if (board.checkRow(i)) rowBuffer.add(i);
            if (board.checkColumn(i)) columnBuffer.add(i);
        }

        if (rowBuffer.size() > 0 || columnBuffer.size() > 0) MultiMedia.playSFX("SFX/explode.wav");

        //Clears the rows and columns that are full
        rowBuffer.forEach(row -> {
            board.clearRow(row);
        });

        columnBuffer.forEach(column -> {
            board.clearColumn(column);
        });

        //Values needed for the score
        int linesCleared = rowBuffer.size()+columnBuffer.size();
        int blocks = columnBuffer.size()*board.getGridHeight() + rowBuffer.size() * (board.getGridWidth() - columnBuffer.size());

        //Update the score
        this.score(linesCleared, blocks); 

        //Updates the multiplier
        if (columnBuffer.size() == 0 && rowBuffer.size() == 0) this.multiplier.set(1);
        else this.multiplier.set(this.multiplier.get() + 1);

        logger.info("Multiplier set to {}", this.multiplier.get());

    }

    /**
     * Updates the score 
     * @param lines How many lines have been cleared
     * @param blocks How many blocks have been cleared
     */
    protected void score(int lines, int blocks) {
        this.score.set(this.score.get() + 10 * lines * blocks * this.multiplier.get());
    }

    /**
     * Swaps the next piece to be played with the reserve
     */
    protected void swapNextPiece() {
        logger.info("Swapping pieces");
        var temp = this.currentPiece;
        this.currentPiece = this.reservePiece;
        this.reservePiece = temp;

        this.challengeScene.setNextPiece(this.currentPiece);
        this.challengeScene.setReservePiece(this.reservePiece);
    }

    /**
     * Called after a successful move
     * Moves the reserve piece to be played next
     * Randomly gets the next piece
     */
    protected void nextPiece() {
        this.currentPiece = this.reservePiece;
        this.challengeScene.setNextPiece(this.currentPiece);

        this.reservePiece = GamePiece.createPiece();
        this.challengeScene.setReservePiece(this.reservePiece);
        logger.info("Piece {} added", this.reservePiece);
    }

    /**
     * Moves selected tile
     * @param byX
     * @param byY
     */
    protected void moveSelected(int byX, int byY) {
        this.challengeScene.getBoard().moveSelected(byX, byY);
    }
    
    /**
     * Attempt to insert piece at the selected tile
     *  (the one with the pointer on)
     */
    protected void insertSelected() {
        int[] selected = this.challengeScene.getBoard().getSelectedPos();
        this.insertPiece(selected[0], selected[1]);
    }

    /**
     * The user loses a life
     * Updates the UI
     */
    public void loseLife() {
        this.challengeScene.loseLife();
        this.lives.set(this.lives.get()-1);
        MultiMedia.playSFX("SFX/lifelose.wav");
    }

    /**
     * The user gains a life
     * Updates UI
     */
    public void addLife() {
        this.challengeScene.addLife();
        this.lives.set(this.lives.get()+1);
    }

    //Timer//

    /**
     * Returns the delay time in ms
     * @return time to make a move
     */
    protected int getTimerDelay() {
        if (this.level.get() < 19) return (12000 - 500*this.level.get());
        else return 2500;
    }

    //Network//

    /**
     * Sets up the communicator for the game
     */
    protected void setupCommunicator() {
        // :(
    }
}

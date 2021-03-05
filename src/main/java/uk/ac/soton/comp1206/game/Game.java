package uk.ac.soton.comp1206.game;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.Scenes.GameScene;
import uk.ac.soton.comp1206.ui.GameWindow;

public class Game {
    private static final Logger logger = LogManager.getLogger(Game.class);

    //Game properties
    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(1);
    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    //The next piece to be played
    private GamePiece currentPiece;

    //Piece in reserve that can be switched to
    private GamePiece reservePiece;

    private GameWindow gameWindow;
    private GameScene gameScene;

    public Game(GameWindow gameWindow) {
        logger.info("Starting game");
        this.gameWindow = gameWindow;

        this.buildGame();

    }

    public void buildGame() {
        this.gameScene = new GameScene(this.gameWindow);
        this.gameWindow.setGameScene(this.gameScene);

        //When the score updates
        this.score.addListener(event -> {
            this.gameScene.updateScore(score.get());
            if ((this.score.get()/1000) + 1 != this.level.get()) {
                this.level.set((this.score.get()/1000) + 1);
                logger.info("level increased to {}", this.level.get());
            } 
        });

        //Clicking a tile//

        //On the game grid
        this.gameScene.addTileClickListener("game-grid", (x, y) -> {
            this.insertPiece(x, y);
        });

        //On the next piece grid
        this.gameScene.addTileClickListener("next-piece", (x, y) -> {
            if (x == 1 && y == 1) {
                this.currentPiece.rotate();
                this.gameScene.setNextPiece(this.currentPiece);
            } else {
                this.swapNextPiece();
            }
        });

        //On the reserve piece grid
        this.gameScene.addTileClickListener("reserve-piece", (x, y) -> {
            this.swapNextPiece();
        });

        //When the game is started//
        this.gameWindow.addGameStartListener(() -> {
            this.nextPiece();
            this.nextPiece();

            //start timer
        });

        this.gameScene.build();

    }

    public void insertPiece(int x, int y) {
        logger.info("Location {} {}", x, y);

        //Blocks that can be added to the game board go here
        //We have to wait for all squares to be checked
        var buffer = new ArrayList<int[]>();

        int[][] pieceBlocks = this.currentPiece.getBlocks();
        var board = this.gameScene.getBoard();

        //Checks the availability of every tile
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                //Checks if the position is going to be filled in
                if (pieceBlocks[row][column] == 1) {
                    //Checks if the position can be filled in
                    if (board.isSquareEmpty(x+column-1, y+row-1)) {
                        buffer.add(new int[] {x+column-1, y+row-1});
                    } else {
                        //The whole shape must fit in to insert the block
                        logger.error("Failed to add tile.");
                        return;
                    }
                }
            }
        }

        //Fills in the tile
        buffer.forEach(pos -> {
            board.changeTile(this.currentPiece.getColour(), pos[1], pos[0]);
        });

        this.afterPiece();

        //Grabs the next piece
        this.nextPiece();
    }

    /**
     * Called after a piece is played
     */
    private void afterPiece() {
        var board = this.gameScene.getBoard();

        //Buffers for which rows/columns are full
        //rows and columns share tiles so we can't remove any tiles before checking both
        var rowBuffer = new ArrayList<Integer>();
        var columnBuffer = new ArrayList<Integer>();

        for (int i = 0; i < this.gameScene.getGridHeight(); i++) {
            if (board.checkRow(i)) rowBuffer.add(i);
            if (board.checkColumn(i)) columnBuffer.add(i);
        }

        rowBuffer.forEach(row -> {
            board.clearRow(row);
        });

        columnBuffer.forEach(column -> {
            board.clearColumn(column);
        });

        int linesCleared = rowBuffer.size()+columnBuffer.size();
        this.score(linesCleared, linesCleared*board.getGridWidth()); 
    }

    private void score(int lines, int blocks) {
        this.score.set(this.score.get() + 100 * lines * blocks * this.multiplier.get());
    }

    /**
     * Swaps the next piece to be played with the reserve
     */
    public void swapNextPiece() {
        logger.info("Swapping pieces");
        var temp = this.currentPiece;
        this.currentPiece = this.reservePiece;
        this.reservePiece = temp;

        this.gameScene.setNextPiece(this.currentPiece);
        this.gameScene.setReservePiece(this.reservePiece);
    }

    /**
     * Called after a successful move
     * Moves the reserve piece to be played next
     * Randomly gets the next piece
     */
    public void nextPiece() {
        this.currentPiece = this.reservePiece;
        this.gameScene.setNextPiece(this.currentPiece);

        this.reservePiece = GamePiece.createPiece();
        this.gameScene.setReservePiece(this.reservePiece);
        logger.info("Piece {} added", this.reservePiece);
    }
}

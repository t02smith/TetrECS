package uk.ac.soton.comp1206.game;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Scenes.GameScene;
import uk.ac.soton.comp1206.ui.GameWindow;

public class Game {
    private static final Logger logger = LogManager.getLogger(Game.class);

    //The next piece to be played
    private GamePiece nextPiece;

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

        this.gameScene.addTileClickListener("game-grid", (x, y) -> {
            this.insertPiece(x, y);
        });

        this.gameScene.addTileClickListener("next-piece", (x, y) -> {
            if (x == 1 && y == 1) {
                this.nextPiece.rotate();
                this.gameScene.setNextPiece(this.nextPiece);
            } else {
                this.swapNextPiece();
            }
        });

        this.gameScene.addTileClickListener("reserve-piece", (x, y) -> {
            this.swapNextPiece();
        });

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

        int[][] pieceBlocks = this.nextPiece.getBlocks();
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
            board.changeTile(this.nextPiece.getColour(), pos[1], pos[0]);
        });

        //Grabs the next piece
        this.nextPiece();
    }

    /**
     * Swaps the next piece to be played with the reserve
     */
    public void swapNextPiece() {
        logger.info("Swapping pieces");
        var temp = this.nextPiece;
        this.nextPiece = this.reservePiece;
        this.reservePiece = temp;

        this.gameScene.setNextPiece(this.nextPiece);
        this.gameScene.setReservePiece(this.reservePiece);
    }

    /**
     * Called after a successful move
     * Moves the reserve piece to be played next
     * Randomly gets the next piece
     */
    public void nextPiece() {
        this.nextPiece = this.reservePiece;
        this.gameScene.setNextPiece(this.nextPiece);

        this.reservePiece = GamePiece.getRandomPiece();
        this.gameScene.setReservePiece(this.reservePiece);
        logger.info("Piece {} added", this.reservePiece);
    }
}

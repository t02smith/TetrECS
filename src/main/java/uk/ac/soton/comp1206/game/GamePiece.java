package uk.ac.soton.comp1206.game;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Components.Game.Colour;

/**
 * This enum is used to store each possible gamepiece
 * Each piece can be rotated
 * This will also let you fetch one at random
 */
public enum GamePiece {
    C           (new int[][] {{0, 0, 0}, {1, 1, 1}, {1, 0, 1}}),
    J           (new int[][] {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}}),
    L           (new int[][] {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}}),
    S           (new int[][] {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}}),
    T           (new int[][] {{1, 0, 0}, {1, 1, 0}, {1, 0, 0}}),
    X           (new int[][] {{1, 0, 1}, {0, 1, 0}, {1, 0, 1}}),
    Z           (new int[][] {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}}),
    DOT         (new int[][] {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}),
    LINE        (new int[][] {{0, 0, 0}, {1, 1, 1}, {0, 0, 0}}),
    PLUS        (new int[][] {{0, 1, 0}, {1, 1, 1}, {0, 1, 0}}),
    SQUARE      (new int[][] {{1, 1, 0}, {1, 1, 0}, {0, 0, 0}}),
    DOUBLE      (new int[][] {{0, 1, 0}, {0, 1, 0}, {0, 0, 0}}),
    CORNER      (new int[][] {{0, 0, 0}, {1, 1, 0}, {1, 0, 0}}),
    DIAGONAL    (new int[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}),
    INV_CORNER  (new int[][] {{1, 0, 0}, {1, 1, 0}, {0, 0, 0}});

    //Stores 2D grid representation of the piece
    private int[][] blocks;

    //The colour the piece will be
    private Colour colour = Colour.nextColour();

    /**
     * Constructor for each piece
     * @param blocks The 2D grid layout of the piece
     */
    private GamePiece(int[][] blocks) {
        this.blocks = blocks;
    }

    private static final Logger logger = LogManager.getLogger(GamePiece.class);

    /**
     * Returns a random gamepiece
     * @return a random gamepiece
     */
    public static GamePiece createPiece() {
        var pieces = GamePiece.values();
        return pieces[
            new Random().nextInt(pieces.length)
        ];
    }

    /**
     * Rotates a piece 90deg clockwise
     */
    public void rotateLeft() {
        logger.info("Rotating {} left", this);
        int[][] rotated = new int[this.blocks.length][this.blocks[0].length];

        for (int row = 0; row < rotated.length; row++) {
            for (int column = 0; column < rotated[0].length; column++) {
                //Uses a matrix transformation in an adjusted 3x3 grid
                rotated [2-column][row] = this.blocks[row][column];
            }
        }

        this.blocks = rotated;
    }

    public void rotateRight() {
        logger.info("Rotating {} right", this);
        int[][] rotated = new int[this.blocks.length][this.blocks[0].length];

        for (int row = 0; row < rotated.length; row++) {
            for (int column = 0; column < rotated[0].length; column++) {
                //Uses a matrix transformation in an adjusted 3x3 grid
                rotated [column][2-row] = this.blocks[row][column];
            }
        }

        this.blocks = rotated;
    }

    /**
     * Returns the block makeup of this piece
     * @return 2D representaion of piece
     */
    public int[][] getBlocks() {
        return this.blocks;
    }

    /**
     * Returns the colour of the game piece
     * @return colour
     */
    public Colour getColour() {
        return this.colour;
    }
}

package uk.ac.soton.comp1206.Components.multiplayer;

import java.util.ArrayList;

import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Tile.TileClickListener;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * game grid specifically for multiplayer
 * The only thing this does different is keep track of the grid history
 *  so that we can transmit it to the server for validation
 * @author tcs1g20
 */
public class MultiplayerGrid extends Grid {
    //Stores the history of the board to send off to the server
    private ArrayList<int[][]> gridHistory = new ArrayList<>();

    /**
     * Creates a multiplayer grid
     * @param width width in squares
     * @param height height in squares
     * @param sideLength pixel length of a square
     * @param tcl What to do when a sqare is clicked
     */
    public MultiplayerGrid(int width, int height, GridSize sideLength, TileClickListener tcl) {
        super(width, height, sideLength, tcl);
    }

    /**
     * Creates a multiplayer grid with no action
     * @param width
     * @param height
     * @param sideLength
     */
    public MultiplayerGrid(int width, int height, GridSize sideLength) {
        super(width, height, sideLength);
    }

    /**
     * Fills in the tiles where a piece is placed
     * Adds grid changes to a grid history
     */
    @Override
    protected void fillTiles(GamePiece piece, ArrayList<int[]> buffer) {
        super.fillTiles(piece, buffer);

        int playedPiece = piece.getValue()+1;
        int[][] next = this.gridHistory.get(this.gridHistory.size()-1).clone();

        buffer.forEach(pos -> {
            next[pos[1]][pos[0]] = playedPiece;
        });

        this.gridHistory.add(next);
    }

    /**
     * Gets the history of the user's grid
     * @return the user's grid history
     */
    public ArrayList<int[][]> getGridHistory() {
        return this.gridHistory;
    }
}

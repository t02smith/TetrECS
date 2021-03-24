package uk.ac.soton.comp1206.Components.Game;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import uk.ac.soton.comp1206.Components.Game.Tile.TileClickListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Represents any of the grids shown on screen
 */
public class Grid extends GridPane {
    protected static final Logger logger = LogManager.getLogger(Grid.class);

    //the game tiles
    protected final Tile[][] tiles;

    //The tile with the hover icon on it
    protected Tile selectedTile;

    //An overlay can be locked to a square or off entirely
    protected final SimpleBooleanProperty lockSelected = new SimpleBooleanProperty(false);

    //The dimensions of the board (in number of squares)
    protected final int width;
    protected final int height;

    //The actual size of the grid squares
    protected final GridSize tileLength;

    //Called when a tile is clicked
    protected TileClickListener tcl;

    /**
     * Different preset sizes of board
     */
    public enum GridSize {
        LARGE(100),     //Meant for the main game board
        MEDIUM(40),     //Meant to display a piece
        SMALL(25);      //Meant for reserve grid and online user's boards

        //The side length in pixels
        private int sideLength;

        private GridSize(int length) {
            this.sideLength = length;
        }

        /**
         * Gets the side length
         * @return side length
         */
        public int getSideLength() {
            return this.sideLength;
        }
        
    }

    /**
     * Creates a board to display game pieces of
     * @param width squares wide
     * @param height squares tall
     * @param sideLength length in pixels for each square
     * @param tcl what happens when a square is clicked
     */
    public Grid(int width, int height, GridSize sideLength, TileClickListener tcl) {
        this.width = width;
        this.height = height;
        this.tileLength = sideLength;

        this.tcl = tcl;

        this.tiles = new Tile[height][width];
        this.build();
    }

    /**
     * Constructor if no tile click listener is needed
     * @param width
     * @param height
     * @param sideLength
     */
    public Grid(int width, int height, GridSize sideLength) {
        this(width, height, sideLength, null);
    }

    /**
     * Called to build the grid component
     */
    public void build() {
        this.getStyleClass().add("game-board");
        var overlayImg = Utility.getImage("ECS.png");

        //Creates a grid of tiles
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                var tile = new Tile(x, y, this.tileLength.sideLength);
                //tile.clearTile(); //Gives every tile the transparent icon
                tile.setOverlay(overlayImg);

                //Calls the given function when clicked
                tile.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        //right click
                    } else if (event.getButton() == MouseButton.PRIMARY) {
                        if (this.tcl != null) this.tcl.onClick(tile.getXPos(), tile.getYPos());
                        this.selectTile(tile.getXPos(), tile.getYPos());
                        tile.requestFocus();
                    }
                    
                }); 

                //When a tile is hovered over it will be selected
                tile.setOnMouseEntered(event -> {
                    this.selectTile(tile.getXPos(), tile.getYPos());
                });

                this.tiles[y][x] = tile;
                this.add(tile, x, y);
            }
        }

        this.lockSelected.addListener(event -> {
            if (!this.lockSelected.get()) this.clearSelected();
        });

        this.setMaxWidth(this.tileLength.sideLength*this.width);
    }

    /**
     * Changes the colour of a tile
     * @param colour The new colour
     * @param x column number
     * @param y row number
     */
    public void changeTile(Colour colour, int x, int y) {
        this.tiles[x][y].setTile(colour);
    }

    /**
     * Returns whether a square is empty or not
     * @param x the column
     * @param y the row
     * @return whether it is empty
     */
    public boolean canPlayPiece(int x, int y) {
        //If the square is not on the board return false instantly
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) return false;

        //Otherwise check if it is available
        return this.tiles[y][x].isEmpty();
    }

    /**
     * Attempts to place a game piece onto the grid
     * @param piece The piece to place
     * @param x X coordinate
     * @param y Y coordinate
     * @return Whether the placement was successful
     */
    public boolean placePiece(GamePiece piece, int x, int y) {
        //Blocks that can be added to the game board go here
        //We have to wait for all squares to be checked
        var buffer = new ArrayList<int[]>();

        int[][] pieceBlocks = piece.getBlocks();

        //Checks the availability of every tile in the 3x3 square
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                //We are trying to place a tile at this square
                if (pieceBlocks[row][column] == 1) {
                    //Can we play a tile at this square
                    if (this.canPlayPiece(x+column-1, y+row-1)) {
                        buffer.add(new int[] {x+column-1, y+row-1});
                    } else {
                        //The whole shape must fit
                        logger.error("Failed to add tile");
                        return false;
                    }
                }
            }
        }

        //If it completed the loop then it is a valid placement

        this.fillTiles(piece, buffer);
        return true;
    }

    /**
     * Fill in a given list of tiles a certain colour
     * @param colour The colour they will be
     * @param buffer The list of tiles to change
     */
    protected void fillTiles(GamePiece piece, ArrayList<int[]> buffer) {
        //Fills in the tile
        buffer.forEach(pos -> {
            this.changeTile(piece.getColour(), pos[1], pos[0]);
        });
    }

    /**
     * Checks if a row has been filled
     * @param rowNo the row number
     * @return whether it is full
     */
    public boolean checkRow(int rowNo) {
        var row = this.tiles[rowNo];
        for (Tile tile: row) {
            if (tile.isEmpty()) return false;
        }

        return true;
    }

    /**
     * Removes all coloured tiles from a given row
     * @param rowNo the number of the row
     */
    public void clearRow(int rowNo) {
        logger.info("row {} cleared", rowNo);
        var row = this.tiles[rowNo];
        for (Tile tile: row) {
            tile.clearTile();
        }
    }

    /**
     * Checks whether a column has been filled
     * @param columnNo the column number
     * @return whether it is full
     */
    public boolean checkColumn(int columnNo) {
        for (Tile[] row: this.tiles) {
            if (row[columnNo].isEmpty()) return false;
        }

        return true;
    }

    /**
     * Removes all coloured tiles from a given column
     * @param columnNo The column number
     */
    public void clearColumn(int columnNo) {
        logger.info("column {} cleared", columnNo);
        for (Tile[] row: this.tiles) {
            row[columnNo].clearTile();
        }
    }

    /**
     * Clears all coloured tiles from the entire board
     */
    public void clearAll() {
        for (Tile[] row: this.tiles) {
            for(Tile column: row) {
                column.clearNoAnimation();
            }
        }
    }

    /**
     * Selects a tile to show the overlay
     * @param x x coordinate
     * @param y y coordinate
     */
    protected void selectTile(int x, int y) {
        if (this.lockSelected.get()) return;

        this.clearSelected();
        this.selectedTile = this.tiles[y][x];
        this.selectedTile.showOverlay();
    }

    /**
     * Moves the selected tile by a given amount
     * @param byX spaces moved horizontally
     * @param byY spaces move vertically
     */
    public void moveSelected(int byX, int byY) {
        logger.info("{} {}", byX, byY);
        if (this.selectedTile != null) { 
            //Moves as requested and wrap round the board if necessary
            this.selectTile(
                (this.selectedTile.getXPos() + byX + this.width) % this.width,
                (this.selectedTile.getYPos() + byY + this.height) % this.height
            );
        } else { //If there is no selected grid (e.g. at start of game)
            //Places grid in center by default and moves as requested
            this.selectTile((int)Math.ceil(this.width/2), (int)Math.ceil(this.height/2));
            this.moveSelected(byX, byY);
        }

    }

    /**
     * Clears the currently selected tile's overlay
     */
    protected void clearSelected() {
        if (this.selectedTile != null) this.selectedTile.hideOverlay();
    }

    /**
     * Locks the selected overlay to a specific tile
     * @param x x coordinate
     * @param y y coordinate
     */
    public void lockSelected(int x, int y) {
        this.selectTile(x, y);
        this.lockSelected.set(true);
    }

    /**
     * Unlocks the selected property
     */
    public void unlockSelected() {
        this.lockSelected.set(false);
    }

    /**
     * THe property for locking the selected tile
     * @return the lock selected property
     */
    public SimpleBooleanProperty lockSelectedProperty() {
        return this.lockSelected;
    }

    /**
     * Locks any tile from being selected
     */
    public void lockSelected() {
        this.clearSelected();
        this.lockSelected.set(true);
    }

    /**
     * Gets the currently selected tile
     * @return the {x, y} coordinates of the selected tile
     */
    public int[] getSelectedPos() {
        return new int[] {this.selectedTile.getXPos(), this.selectedTile.getYPos()};
    }

    /**
     * Gets the width of the grid
     * @return grid width (tiles)
     */
    public int getGridWidth() {
        return this.width;
    }

    /**
     * Gets the grid height
     * @return grid height (squares)
     */
    public int getGridHeight() {
        return this.height;
    }
}

package uk.ac.soton.comp1206.Components.Game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import uk.ac.soton.comp1206.Event.TileClickListener;
import uk.ac.soton.comp1206.Utility.Utility;

final public class Grid extends GridPane {
    private static final Logger logger = LogManager.getLogger(Grid.class);

    //the game tiles
    private final Tile[][] tiles;
    private Tile selectedTile;

    //An overlay can be locked to a square or off entirely
    private final SimpleBooleanProperty lockSelected = new SimpleBooleanProperty(false);

    //The dimensions of the board
    private final int width;
    private final int height;

    private final GridSize tileLength;

    //Called when a tile is clicked
    private TileClickListener tcl;

    /**
     * Different sizes of board
     */
    public enum GridSize {
        LARGE(100),
        MEDIUM(40),
        SMALL(25);

        private int sideLength;

        private GridSize(int length) {
            this.sideLength = length;
        }

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
                        this.tcl.onClick(tile.getXPos(), tile.getYPos());
                        this.selectTile(tile.getXPos(), tile.getYPos());
                    }
                    
                }); 

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
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) return false;

        return this.tiles[y][x].isEmpty();
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

    public void clearColumn(int columnNo) {
        logger.info("column {} cleared", columnNo);
        for (Tile[] row: this.tiles) {
            row[columnNo].clearTile();
        }
    }

    /**
     * Selects a tile to show the overlay
     * @param x x coordinate
     * @param y y coordinate
     */
    private void selectTile(int x, int y) {
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
        if (this.selectedTile != null) {
            this.selectTile(
                (this.selectedTile.getXPos() + byX) % this.width,
                (this.selectedTile.getYPos() + byY) % this.height
            );
        } else {
            this.selectTile((int)Math.ceil(this.width/2), (int)Math.ceil(this.height/2));
            this.moveSelected(byX, byY);
        }

    }

    /**
     * Clears the currently selected tile's overlay
     */
    private void clearSelected() {
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

    public int[] getSelectedPos() {
        return new int[] {this.selectedTile.getXPos(), this.selectedTile.getYPos()};
    }

    public int getGridWidth() {
        return this.width;
    }

    public int getGridHeight() {
        return this.height;
    }
}

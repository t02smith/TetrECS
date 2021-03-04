package uk.ac.soton.comp1206.Components.Game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.layout.GridPane;
import uk.ac.soton.comp1206.Event.TileClickListener;

public class Board extends GridPane {
    private static final Logger logger = LogManager.getLogger(Board.class);

    private Tile[][] tiles;

    //The dimensions of the board
    private int width;
    private int height;

    private BoardSize tileLength;

    //Called when a tile is clicked
    private TileClickListener tcl;

    public enum BoardSize {
        LARGE(100),
        MEDIUM(40),
        SMALL(25);

        private int sideLength;

        private BoardSize(int length) {
            this.sideLength = length;
        }
        
    }

    public Board(int width, int height, BoardSize sideLength, TileClickListener tcl) {
        this.width = width;
        this.height = height;
        this.tileLength = sideLength;

        this.tcl = tcl;

        this.tiles = new Tile[height][width];
        this.build();
    }

    public void build() {
        this.getStyleClass().add("game-board");

        //Creates a grid of tiles
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                var tile = new Tile(x, y, this.tileLength.sideLength);
                tile.clearTile(); //Gives every tile the transparent icon

                //Calls the given function when clicked
                tile.setOnMouseClicked(event -> {
                    this.tcl.onClick(tile.getXPos(), tile.getYPos());
                });

                

                this.tiles[y][x] = tile;
                this.add(tile, x, y);
            }
        }
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
    public boolean isSquareEmpty(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) return false;

        return this.tiles[y][x].isEmpty();
    }
}

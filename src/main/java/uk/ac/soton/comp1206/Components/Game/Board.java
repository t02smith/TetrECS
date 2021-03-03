package uk.ac.soton.comp1206.Components.Game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.layout.GridPane;
import uk.ac.soton.comp1206.Errors.TileFullException;
import uk.ac.soton.comp1206.Event.TileClickListener;
import uk.ac.soton.comp1206.game.GamePiece;

public class Board extends GridPane {
    private static final Logger logger = LogManager.getLogger(Board.class);

    private Tile[][] tiles;

    //The dimensions of the board
    private int width;
    private int height;

    //Called when a tile is clicked
    private TileClickListener tcl;

    public Board(int width, int height, TileClickListener tcl) {
        this.width = width;
        this.height = height;

        this.tcl = tcl;

        this.tiles = new Tile[height][width];
        this.build();
    }

    public void build() {
        this.getStyleClass().add("game-board");

        //Creates a grid of tiles
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                var tile = new Tile(x, y);
                try {tile.setTile(Colour.TRANSPARENT);}
                catch (TileFullException e) {}

                //Calls the given function when clicked
                tile.setOnMouseClicked(event -> {
                    tcl.onClick(tile.getXPos(), tile.getYPos());
                });

                this.tiles[y][x] = tile;
                this.add(tile, x, y);
            }
        }
    }

    /**
     * Adds a game piece to the board
     * @param gp the piece being added
     */
    public void addGamePiece(GamePiece gp) {
        logger.info("Attempting to add gamepiece: {}", gp);
        
    }
}

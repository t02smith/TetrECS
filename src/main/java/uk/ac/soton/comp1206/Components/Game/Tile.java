package uk.ac.soton.comp1206.Components.Game;

import javafx.scene.image.ImageView;
import uk.ac.soton.comp1206.Errors.TileFullException;

/**
 * This class represents each individual tile on the game board
 * Each tile can change colour to match some image icons I made
 */
public class Tile extends ImageView {
    private boolean isEmpty = true;

    private final int x;
    private final int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Will change a tile to a specific colour unless it's already taken
     * @param colour The colour it is changing to
     * @throws TileFullException If the tile is already taken
     */
    public void setTile(Colour colour) throws TileFullException {
        if (isEmpty) {
            this.setImage(colour.getIcon());
        } else throw new TileFullException();
    }

    public void clearTile() {
        this.setImage(null);
        //Some animation
    }

    public int getXPos() {
        return this.x;
    }

    public int getYPos() {
        return this.y;
    }
}

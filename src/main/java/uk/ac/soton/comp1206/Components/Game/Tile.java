package uk.ac.soton.comp1206.Components.Game;

import javafx.scene.image.ImageView;

/**
 * This class represents each individual tile on the game board
 * Each tile can change colour to match some image icons I made
 */
public class Tile extends ImageView {
    private boolean isEmpty;

    private final int x;
    private final int y;

    public Tile(int x, int y, int sideLength) {
        this.x = x;
        this.y = y;

        this.setPreserveRatio(true);
        this.setFitHeight(sideLength);
    } 

    public Tile(int x, int y) {
        this(x, y, 100);
    }

    /**
     * Will change a tile to a specific colour unless it's already taken
     * @param colour The colour it is changing to
     */
    public void setTile(Colour colour) {
        this.setImage(colour.getIcon());
        this.isEmpty = false;
    }

    public void clearTile() {
        this.setImage(Colour.TRANSPARENT.getIcon());
        this.isEmpty = true;
        //Some animation
        
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public int getXPos() {
        return this.x;
    }

    public int getYPos() {
        return this.y;
    }
}

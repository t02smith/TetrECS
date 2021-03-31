package uk.ac.soton.comp1206.Components.Game;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * This class represents each individual tile on the game board
 * Each tile can change colour to match some image icons I made
 * @author tcs1g20
 */
public class Tile extends StackPane {
    //If the tile is empty or not
    private boolean isEmpty = true;

    //Coordinates of the tile
    private final int x;
    private final int y;

    //Image on the tile//

    //Background that never changes
    private final ImageView outline = new ImageView(Colour.TRANSPARENT.getIcon());

    //Anything placed on the grid
    private ImageView tile = new ImageView();

    //Tile overlay -> i.e. to show that tile is selected
    private ImageView overlay = new ImageView();

    /**
     * Constructor for the tile
     * @param x It's x coordinate
     * @param y It's y coordinate
     * @param sideLength It's sidelenth in pixels
     */
    public Tile(int x, int y, int sideLength) {
        this.x = x;
        this.y = y;

        this.getChildren().addAll(this.outline, this.tile, overlay);

        this.tile.setPreserveRatio(true);
        this.tile.setFitHeight(sideLength);

        this.outline.setPreserveRatio(true);
        this.outline.setFitHeight(sideLength);

        this.overlay.setPreserveRatio(true);
        this.overlay.setFitHeight(sideLength * 0.75);
        this.overlay.setOpacity(0);

    } 

    /**
     * Constructor for a large grid tile
     * @param x
     * @param y
     */
    public Tile(int x, int y) {
        this(x, y, 100);
    }

    /**
     * Interface for when a tile is clicked
     *  Keeping it here for relevance
     */
    public interface TileClickListener {
        public void onClick(int x, int y);
    }
    

    /**
     * Will change a tile to a specific colour unless it's already taken
     * @param colour The colour it is changing to
     */
    public void setTile(Colour colour) {
        Platform.runLater(() -> this.tile.setImage(colour.getIcon()));
        this.isEmpty = false;
    }

    /**
     * Clears a tile of a block 
     */
    public void clearTile() {
        //A tile will fade out
        var fadeOut = new FadeTransition(Duration.millis(500), this.tile);
        fadeOut.setByValue(-1);

        //Once it has faded remove the colouring and return its opacity to 1
        fadeOut.setOnFinished(e -> {
            this.tile.setImage(null);
            this.isEmpty = true;
            this.tile.setOpacity(1);
        });

        fadeOut.play();        
    }

    /**
     * Clears a tile without any animation
     */
    public void clearNoAnimation() {
        this.tile.setImage(null);
        this.isEmpty = true;
    }

    /**
     * Animates the overlay when a block is clicked
     * Not sure if this is actually implemented?
     */
    public void select() {
        var initial = this.overlay.getFitWidth();
        var timeline = new Timeline(
            new KeyFrame(Duration.millis(350), new KeyValue(this.overlay.fitWidthProperty(), initial*0.5)),
            new KeyFrame(Duration.millis(350), new KeyValue(this.overlay.fitWidthProperty(), initial))
        );

        timeline.play();
    }


    /**
     * Shows the tile's overlay
     */
    public void showOverlay() {
        this.overlay.setOpacity(0.8);
    }

    /**
     * Hides the tile's overlay
     */
    public void hideOverlay() {
        this.overlay.setOpacity(0);
    }

    /**
     * Sets the overlay's image
     * @param img the overlay image
     */
    public void setOverlay(Image img) {
        this.overlay.setImage(img);
    }

    /**
     * Returns whether the tile is empty
     * @return the tile's state
     */
    public boolean isEmpty() {
        return this.isEmpty;
    }

    /**
     * Returns the tile's x coordinate
     * @return Tile's X coordinate
     */
    public int getXPos() {
        return this.x;
    }

    /**
     * Returns the tiles Y coordinate
     * @return Tile's Y coordinate
     */
    public int getYPos() {
        return this.y;
    }
}

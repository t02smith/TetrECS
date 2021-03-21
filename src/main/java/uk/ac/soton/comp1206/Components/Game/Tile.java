package uk.ac.soton.comp1206.Components.Game;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * This class represents each individual tile on the game board
 * Each tile can change colour to match some image icons I made
 */
public class Tile extends StackPane {
    private boolean isEmpty = true;

    //Coordinates of the tile
    private final int x;
    private final int y;

    //Image on the tile
    private final ImageView outline = new ImageView(Colour.TRANSPARENT.getIcon());
    private ImageView tile = new ImageView();

    //Tile overlay -> i.e. to show that tile is selected
    private ImageView overlay = new ImageView();

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

    public Tile(int x, int y) {
        this(x, y, 100);
    }

    public interface TileClickListener {
        public void onClick(int x, int y);
    }
    

    /**
     * Will change a tile to a specific colour unless it's already taken
     * @param colour The colour it is changing to
     */
    public void setTile(Colour colour) {
        this.tile.setImage(colour.getIcon());
        this.isEmpty = false;
    }

    /**
     * Clears a tile of a block 
     */
    public void clearTile() {
        var fadeOut = new FadeTransition(Duration.millis(500), this.tile);
        fadeOut.setByValue(-1);
        fadeOut.setOnFinished(e -> {
            this.tile.setImage(null);
            this.isEmpty = true;
            this.tile.setOpacity(1);
        });

        fadeOut.play();        
    }

    /**
     * Animates the overlay when a block is clicked
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

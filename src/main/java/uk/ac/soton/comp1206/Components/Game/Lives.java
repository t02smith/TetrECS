package uk.ac.soton.comp1206.Components.Game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Utility.Utility;

import java.util.Stack;

/**
 * This class will display the user's remaining lives
 * By default the user will have 3 lives
 */
public class Lives extends HBox {
    //The starting number of lives
    private final int lives;

    //Lives left
    private Stack<ImageView> remaining = new Stack<>();

    public Lives(int lives) {
        this.lives = lives;
        this.build();
    }

    /**
     * Builds the heart components
     */
    public void build() {
        this.getStyleClass().add("lives");

        Image lifeImg = Utility.getImage("heart.png");
        this.setAlignment(Pos.CENTER);

        //For every life the user has
        for (int i = 0; i < this.lives; i++) {
            var life = new ImageView(
                lifeImg
            );

            life.setPreserveRatio(true);
            life.setFitHeight(65);

            this.remaining.push(life);
            this.getChildren().add(life);
        }
    
    }

    /**
     * Called when the user loses a life in game
     */
    public void loseLife() {
        //If they have no lives left the game will end
        if (this.remaining.size() == 0) return;

        //Remove the rightmost heart from display
        var life = this.remaining.pop();
        life.setImage(null);

        //If the user now has no lives remove the background
        if (this.remaining.size() == 0) {
            //this.setStyle("-fx-background-color: transparent;");
            this.warningLabel();
        }
    }

    /**
     * When the user runs out of lives 
     *  this displays a warning message that flashes
     */
    public void warningLabel() {
        var warning = new Label("NO LIVES!");
        warning.getStyleClass().add("warning");

        //Flashing animation
        var timeline = new Timeline(
            new KeyFrame(Duration.millis(1000), new KeyValue(warning.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(1000), new KeyValue(warning.opacityProperty(), 1))
        );

        //Plays until the game ends
        timeline.setCycleCount(Animation.INDEFINITE);
        
        this.getChildren().add(warning);
        timeline.play();
    }
}

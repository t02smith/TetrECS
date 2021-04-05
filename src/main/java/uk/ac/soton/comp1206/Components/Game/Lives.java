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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * This class will display the user's remaining lives
 * By default the user will have 3 lives
 * @author tcs1g20
 */
public class Lives extends VBox {
    //The starting number of lives
    private int lives;

    public Lives(int lives) {
        this.lives = lives;
        this.build();
    }

    /**
     * Builds the heart components
     */
    public void build() {
        this.getStyleClass().add("lives");
        this.setAlignment(Pos.CENTER);

        this.buildLives();
    
    }

    /**
     * Builds the component that shows the user's current lives
     */
    public void buildLives() {
        this.getChildren().clear();

        if (this.lives <= 0) {
            this.warningLabel();
            return;
        }

        Image lifeImg = Utility.getImage("heart.png");

        HBox row = null;
        //For every life the user has
        for (int i = 0; i < this.lives; i++) {
            var life = new ImageView(
                lifeImg
            );

            life.setPreserveRatio(true);
            life.setFitHeight(this.lives > 6 ? 42: 65);

            //The hearts will be smaller if there are lots of them
            if ((i%3 == 0 && this.lives <= 6) || (i%6 == 0 && this.lives > 6)) {
                if (row != null) {
                    var emptyR = new Region();
                    HBox.setHgrow(emptyR, Priority.ALWAYS);
                    row.getChildren().add(emptyR);

                    this.getChildren().add(row);
                }

                var emptyL = new Region();
                HBox.setHgrow(emptyL, Priority.ALWAYS);
                row = new HBox(emptyL);
            }

            row.getChildren().add(life);
        }

        if (row != null) {
            var emptyR = new Region();
            HBox.setHgrow(emptyR, Priority.ALWAYS);
            row.getChildren().add(emptyR);

            this.getChildren().add(row);
        }
    }

    /**
     * Called when a user gains a life
     */
    public void addLife() {
        this.lives++;
        this.buildLives();
    }

    /**
     * Called when the user loses a life in game
     */
    public void loseLife() {
        this.lives--;
        this.buildLives();
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

package uk.ac.soton.comp1206.Components.Game;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import uk.ac.soton.comp1206.Utility.Utility;

import java.util.Stack;

/**
 * This class will display the user's remaining lives
 * By default the user will have 3 lives
 */
public class Lives extends HBox {
    //The starting number of lives
    private int lives;

    //Lives left
    private Stack<ImageView> remaining = new Stack<>();

    public Lives(int lives) {
        this.lives = lives;
        this.build();
    }

    public void build() {
        this.getStyleClass().add("lives");

        Image lifeImg = Utility.getImage("heart.png");
        this.setAlignment(Pos.CENTER);

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

    public void loseLife() {
        if (this.remaining.size() == 0) return;

        var life = this.remaining.pop();
        life.setImage(null);

        if (this.remaining.size() == 0) this.setStyle("-fx-background-color: transparent;");
    }
}

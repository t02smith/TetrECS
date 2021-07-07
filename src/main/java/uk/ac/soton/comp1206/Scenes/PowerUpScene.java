package uk.ac.soton.comp1206.Scenes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Components.Game.Powerups.PowerUpGrid;
import uk.ac.soton.comp1206.Event.Action;
import uk.ac.soton.comp1206.game.Powerup.PowerUp;
import uk.ac.soton.comp1206.game.Powerup.PowerUpGame.UsePowerUpListener;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Scene for power up game mode
 * @author tcs1g20
 */
public class PowerUpScene extends ChallengeScene {
    //Called when a powerup is used
    private UsePowerUpListener powerUpListener;

    //The label showing how many points a user has
    private Label points;

    /**
     * Creates a new PowerUpScene
     * @param window The window its being shown on
     * @param listener What to do when a user tries to use a powerup
     */
    public PowerUpScene(GameWindow window, UsePowerUpListener listener) {
        super(window);
        this.powerUpListener = listener;
    }

    @Override
    public void build() {
        super.build();

        this.windowWidth = 1000;

        //Power up options on the left
        var powerUpMenu = new VBox(12);
        powerUpMenu.setAlignment(Pos.TOP_CENTER);
        powerUpMenu.setMinWidth(this.getWidth()*0.25);
        powerUpMenu.setMaxWidth(this.getWidth()*0.25);

        this.points = new Label("Points: 0");
        this.points.getStyleClass().add("sidebar-text");
        this.points.setStyle("-fx-font-size: 16");


        var powerUpGrid = new GridPane();
        powerUpGrid.setAlignment(Pos.CENTER);
        powerUpGrid.setVgap(10);
        
        //Iterates through the power ups and creates icons in a 2xn grid
        //Adds required events

        var powerUps = PowerUp.values();
        for (int i = 0; i < powerUps.length; i++) {
            var power = powerUps[i];

            var icon = new ImageView(power.getIcon());
            icon.setPreserveRatio(true);
            icon.setFitHeight(75);

            var price = new Label(String.valueOf(power.getPrice()));
            price.getStyleClass().add("sidebar-text");
            price.setStyle("-fx-font-size: 12");

            //When you use a power up change its price
            icon.setOnMouseClicked(event -> {
                if (this.powerUpListener.usePower(power)) {
                    price.setText(String.valueOf(power.getPrice()));
                }
                
            });

            var powerUpShell = new VBox(icon, price);
            powerUpShell.setAlignment(Pos.CENTER);

            powerUpGrid.add(powerUpShell, i%2, i/2);
        }

        powerUpMenu.getChildren().addAll(this.points, powerUpGrid);
        this.root.setLeft(powerUpMenu);
    }

    /**
     * Uses a new powerup grid
     * Same as normal grid but has extra functions like push
     */
    @Override
    protected void buildGrid() {
        this.grid = new PowerUpGrid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));        
        this.grid.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Action.ROTATE_RIGHT.execute();
            } else if (event.getButton() == MouseButton.MIDDLE) {
                Action.SWAP.execute();
            }
        });
    }

    /**
     * Sets the listener for when using one of the power ups
     * @param listener The listener
     */
    public void setUsePowerUpListener(UsePowerUpListener listener) {
        this.powerUpListener = listener;
    }

    /**
     * Update the user's points in the UI
     * @param points the user's points
     */
    public void setPoints(int points) {
        this.points.setText("Points: " + points);
    }
}

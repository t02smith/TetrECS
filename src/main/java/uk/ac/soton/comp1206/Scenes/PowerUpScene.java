package uk.ac.soton.comp1206.Scenes;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Components.Game.Powerups.PowerUpGrid;
import uk.ac.soton.comp1206.Event.KeyBinding;
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

    public PowerUpScene(GameWindow window, UsePowerUpListener listener) {
        super(window);
        this.powerUpListener = listener;
    }

    @Override
    public void build() {
        super.build();

        this.windowWidth = 1000;

        //Power up options on the left
        var options = new VBox(12);

        var powerUps = PowerUp.values();
        for (PowerUp power: powerUps) {
            var button = new Button(power.toString());
            button.setOnAction(event -> this.powerUpListener.usePower(power));
            options.getChildren().add(button);
        }

        this.root.setLeft(options);
    }

    @Override
    protected void buildGrid() {
        this.grid = new PowerUpGrid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));        
        this.grid.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                KeyBinding.ROTATE_RIGHT.execute();
            } else if (event.getButton() == MouseButton.MIDDLE) {
                KeyBinding.SWAP.execute();
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
}

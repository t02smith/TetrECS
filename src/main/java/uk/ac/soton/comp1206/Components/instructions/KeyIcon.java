package uk.ac.soton.comp1206.Components.instructions;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Event.KeyBinding;
import uk.ac.soton.comp1206.Utility.Utility;

public class KeyIcon extends StackPane {
    private Label value;
    private ImageView icon;

    private KeyCode key;

    public KeyIcon(KeyCode key) {
        this.value = new Label(this.getSymbol(key));
        this.value.getStyleClass().add("key-icon");

        this.icon = new ImageView(Utility.getImage("key.png"));
        this.icon.setPreserveRatio(true);
        this.icon.setFitHeight(75);

        this.key = key;

        this.getChildren().addAll(this.icon, this.value);

    } 

    /**
     * Gets the string version of a key
     * Only really necessary for symbols where we use the unicode characters
     * @param key The key being checked
     * @return The string version of the key
     */
    private String getSymbol(KeyCode key) {
        if (key.isArrowKey() || key.isWhitespaceKey() || key.equals(KeyCode.ESCAPE)) {
            switch(key) {
                case LEFT:
                    return "\u2190";
                case RIGHT:
                    return "\u2192";
                case UP:
                    return "\u2191";
                case DOWN:
                    return "\u2193";
                case ENTER:
                    return "\u21B2";
                case SPACE:
                    return "\u2334";
                case ESCAPE:
                    return "ESC";
                default:
                    return "";
            }
        } 

        return String.valueOf((char)key.getCode());
    }

    public interface ClickKeyListener {
        public void click(KeyCode key);
    }

    public void flashRed() {
        var red = new ImageView(Utility.getImage("red-key.png"));
        red.setPreserveRatio(true);
        red.setFitHeight(75);

        var flash = new Timeline(
            new KeyFrame(Duration.ZERO, event -> this.getChildren().set(0, red)),
            new KeyFrame(Duration.millis(175), event -> this.getChildren().set(0, this.icon))
        );

        flash.play();
    }

    public void setKey(KeyCode newKey) {
        var action = KeyBinding.getAction(this.key);

        if (action.assignKey(this.key, newKey)) {
            this.value.setText(this.getSymbol(newKey));
        } else {
            this.flashRed();
        }

        this.value.setOpacity(1);
    }

    public void hideValue() {
        this.value.setOpacity(0);
    }
}

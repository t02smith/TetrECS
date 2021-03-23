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

/**
 * Each individual key icon
 * TODO keys show up again after being replaced
 */
public class KeyIcon extends StackPane {
    //The character displayed on the key
    private Label value;

    //The background key icon
    private ImageView icon;

    //The key code it corresponds to
    private KeyCode key;

    /**
     * Creates a new key icon
     * @param key the key to create an icon of
     */
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
                //Excpetions where i've used specific unicode characters that keycode didn't provide
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

    /**
     * Listener for when one of these icons is clicked
     */
    public interface ClickKeyListener {
        public void click(KeyCode key);
    }

    /**
     * Will flash the the key icon red
     * Used when a user tries to assign a key to another key that's already bound
     */
    public void flashRed() {
        var red = new ImageView(Utility.getImage("red-key.png"));
        red.setPreserveRatio(true);
        red.setFitHeight(75);

        //Changes the key to the red version briefly
        var flash = new Timeline(
            new KeyFrame(Duration.ZERO, event -> this.getChildren().set(0, red)),
            new KeyFrame(Duration.millis(175), event -> this.getChildren().set(0, this.icon))
        );

        flash.play();
    }

    /**
     * Changes the key on display
     * @param newKey The key to change it to
     */
    public void setKey(KeyCode newKey) {
        //Get the corresponding action
        var action = KeyBinding.getAction(this.key);

        //Attempt to assign the key
        if (action.assignKey(this.key, newKey)) {
            //If successful then change the symbol on the key
            this.value.setText(this.getSymbol(newKey));
            this.key = newKey;
        } else {
            this.flashRed();
        }

        this.value.setOpacity(1);
    }

    /**
     * Hides the key's value
     */
    public void hideValue() {
        this.value.setOpacity(0);
    }
}

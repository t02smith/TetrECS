package uk.ac.soton.comp1206.Components.instructions;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Event.Action;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * Each individual key icon
 * @author tcs1g20
 */
public class KeyIcon extends StackPane {
    //The character displayed on the key
    private Label value;

    //The background key icon
    private ImageView icon;

    //The key code it corresponds to
    private KeyCode key;

    //The action this keyIcon will bind
    private Action action;

    /**
     * Creates a new key icon
     * @param key the key to create an icon of
     * @param action the key action this icon will bind to
     */
    public KeyIcon(KeyCode key, Action action) {
        this.value = new Label(this.getSymbol(key));
        this.value.getStyleClass().add("key-icon");

        this.icon = (key == null) ? 
            new ImageView(Utility.getImage("keys/no-binding.png")) :
            new ImageView(Utility.getImage("keys/key.png"));
        this.icon.setPreserveRatio(true);
        this.icon.setFitHeight(75);

        this.setRemoveKey();

        this.key = key;
        this.action = action;

        this.getChildren().addAll(this.icon, this.value);

    } 

    /**
     * Gets the string version of a key
     * Only really necessary for symbols where we use the unicode characters
     * @param key The key being checked
     * @return The string version of the key
     */
    protected String getSymbol(KeyCode key) {
        if (key == null) return "";
        else if (key.isArrowKey() || key.isWhitespaceKey() || key.equals(KeyCode.ESCAPE)) {
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
                case TAB:
                    return "\u21b9";
                default:
                    return "";
            }
        } 

        return String.valueOf((char)key.getCode());
    }

    /**
     * Will flash the the key icon red
     * Used when a user tries to assign a key to another key that's already bound
     */
    public void flashRed() {
        var red = new ImageView(Utility.getImage("keys/red-key.png"));
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
        //Attempt to assign the key
        if (this.action.assignKey(this.key, newKey)) {
            if (newKey == null) {
                this.icon.setImage(Utility.getImage("keys/no-binding.png"));
            } else if (this.key == null) {
                this.icon.setImage(Utility.getImage("keys/key.png"));
                this.setRemoveKey();
            }

            //If successful then change the symbol on the key
            this.value.setText(this.getSymbol(newKey));
            this.key = newKey;
        } else {
            this.flashRed();
        }

        this.value.setOpacity(1);
    }

    public void setRemoveKey() {
        var remove = new Label("X");
        remove.getStyleClass().add("remove-key");
        remove.setOpacity(0);

        this.setOnMouseEntered(event -> {if (this.key != null) remove.setOpacity(1);});
        this.setOnMouseExited(event -> remove.setOpacity(0));

        remove.setOnMouseClicked(event -> {
            this.removeKey();
        });

        var vbox = new VBox(remove);
        vbox.setAlignment(Pos.TOP_RIGHT);

        Platform.runLater(() -> this.getChildren().add(vbox));
    }

    /**
     * Hides the key's value
     */
    public void hideValue() {
        this.value.setOpacity(0);
    }

    /**
     * Removes a key binding for the currently stored key
     */
    public void removeKey() {
        this.action.removeKey(this.key);
        this.setKey(null);
    }
}

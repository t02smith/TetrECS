package uk.ac.soton.comp1206.Components.multiplayer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.ac.soton.comp1206.Event.KeyBinding;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * The toolbar to type and send messages in
 * @author tcs1g20
 */
public class TextToolbar extends HBox {
    //The place to type in
    private TextField content;

    //The send button
    private Button send;

    //Called when text is submitted
    private SubmitListener sml;

    //Whether key actions are enabled
    private boolean keysDisabled;
    
    public TextToolbar(SubmitListener submitListener) {
        this.sml = submitListener;
        this.build();
    }

    /**
     * Constructs the text toolbar
     */
    public void build() {
        //Message input area
        this.content = new TextField();
        this.content.setMaxHeight(20);
        this.content.getStyleClass().add("msg-input");
        this.content.setAlignment(Pos.CENTER_LEFT);
        this.content.setFocusTraversable(false);

        //Stretches to fit the space its in
        HBox.setHgrow(this.content, Priority.ALWAYS);

        //send icon
        ImageView icon = new ImageView(Utility.getImage("send.png"));
        icon.setPreserveRatio(true);
        icon.setFitHeight(20);

        //Send button
        this.send = new Button();
        this.send.setGraphic(icon);
        this.send.getStyleClass().add("msg-send");

        this.setSubmitListener(this.sml);

        this.getChildren().addAll(this.content, this.send);
        this.getStyleClass().add("msg-toolbar");

        //Changes the state of keys disabled when in use
        this.content.focusedProperty().addListener(event -> {
            if (this.content.isFocused()) {
                this.keysDisabled = KeyBinding.getKeysDisabled();
                if (!this.keysDisabled) KeyBinding.setKeysDisabled(true);
            } else if (!this.keysDisabled) KeyBinding.setKeysDisabled(this.keysDisabled);
        });
    }

    /**
     * Sets the listener for what to do when submitting a string
     * Reassigns all the corresponding events too
     * @param submitListener The new listener
     */
    public void setSubmitListener(SubmitListener submitListener) {
        if (submitListener == null) return;

        this.sml = submitListener;

        //Sending the message
        this.send.setOnAction(event -> {
            this.submit();
        });

        //If the user hits enter
        this.content.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.submit();
            }
        });
    }

    /**
     * Submits the text inside the textfield
     *  There must be text in it to submit
     */
    private void submit() {
        if (this.content.getText().length() > 0) {
            this.sml.submit(this.content.getText());
            this.content.setText("");
        }
    }

    /**
     * Set the prompt text to show up in the textfield
     * @param promptText the prompt text
     */
    public void setPromptText(String promptText) {
        this.content.setPromptText(promptText);
    }

    /**
     * Sets the alignemnt of text in the toolbar
     * @param alignment
     */
    public void setTextAlignment(Pos alignment) {
        this.content.setAlignment(alignment);
    }

    /**
     * A listener to be called when submitting any text
     */
    public interface SubmitListener {
        public void submit(String text);
    }
}

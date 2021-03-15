package uk.ac.soton.comp1206.Components.misc;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Event.OnClickListener;

/**
 * A button that when clicked expands out a textfield for the user to type in
 * 
 */
public class ExpandingTextField extends HBox {
    private Label name;
    private TextField input;

    private HBox shell;

    private SimpleBooleanProperty open = new SimpleBooleanProperty(false);

    private Align align;

    public enum Align {
        LEFT,
        CENTER,
        RIGHT;
    }

    public ExpandingTextField(String name, Align align, OnClickListener onClick) {
        this.align = align;
        this.setAlignment(Pos.CENTER);

        this.name = new Label(name);
        this.name.getStyleClass().add("expanding-tf-name");

        this.input = new TextField();
        this.input.getStyleClass().add("expanding-tf-input");

        //By default this field isn't shown
        this.input.setMaxWidth(0);
        this.input.setOpacity(0);
        this.input.setFocusTraversable(false);


        this.input.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onClick.onClick(this.input.getText());
            }
        });

        this.input.setMinHeight(10);

        this.name.setOnMouseClicked(event -> {
            this.toggleTextField();
        });

        var empty = new Region();
        HBox.setHgrow(empty, Priority.ALWAYS);

        this.shell = null;
        switch(align) {
            case LEFT:
                shell = new HBox(this.name, this.input);
                shell.getStyleClass().add("tf-left");
                this.getChildren().addAll(empty, shell);
                break;
            case RIGHT:
                shell = new HBox(this.input, this.name);
                shell.getStyleClass().add("tf-right");
                this.getChildren().addAll(empty, shell);
                break;
            case CENTER:
                shell = new HBox(this.name, this.input);
                shell.getStyleClass().add("tf-center");
    
                var newEmpty = new Region();
                HBox.setHgrow(newEmpty, Priority.ALWAYS);
                this.getChildren().addAll(empty, shell, newEmpty);
                break;
        }

        this.input.setVisible(false);
        shell.getStyleClass().add("expanding-tf-shell");
        shell.setAlignment(Pos.CENTER);
        shell.setSpacing(5);

    }

    public void toggleTextField() {
        if (!this.open.get()) this.input.setVisible(true);

        var timeline = new Timeline(
            new KeyFrame(Duration.millis(500), new KeyValue(this.input.maxWidthProperty(), this.open.get() ? 0: 300)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.input.minWidthProperty(), this.open.get() ? 0: 300)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.input.opacityProperty(), this.open.get() ? 0: 1))
       
        );
        
        timeline.play();
        this.open.set(!this.open.get());
        if (!this.open.get()) this.input.setVisible(false);

    }

    public void setPromptText(String text) {
        this.input.setPromptText(text);
    }
}

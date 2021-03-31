package uk.ac.soton.comp1206.Components.misc;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
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
 * @author tcs1g20
 */
public class ExpandingTextField extends HBox {
    //The text on the outside of the box
    private SimpleStringProperty title = new SimpleStringProperty();

    //Two halves of the title that are split apart
    private Label nameA = new Label();
    private Label nameB = new Label();

    //The input field
    private TextField input;

    private HBox shell;

    private SimpleBooleanProperty open = new SimpleBooleanProperty(false);

    /**
     * Creates a button that expands into a textfield
     * @param title The label to go on the front
     */
    public ExpandingTextField(String title) {
        this.title.set(title);
        
        this.build();

    }

    /**
     * Builds the component
     */
    public void build() {
        this.setAlignment(Pos.CENTER);

        //Title//
        //First half
        this.nameA.getStyleClass().add("expanding-tf-name");
        this.nameA.setAlignment(Pos.CENTER_RIGHT);

        //second half
        this.nameB.getStyleClass().add("expanding-tf-name");
        this.nameB.setAlignment(Pos.CENTER_LEFT);

        //Initially sets the title
        this.updateTitle();

        //If the title is changed then it will be updated visually
        this.title.addListener(event -> this.updateTitle());

        //Text input field//
        this.input = new TextField();
        this.input.getStyleClass().add("expanding-tf-input");

        //Initially it will be invisible
        this.input.setMaxWidth(0);
        this.input.setVisible(false);
        this.input.setOpacity(0);

        this.setPadding(new Insets(0, 0, 50, 0));
        this.input.setAlignment(Pos.CENTER);

        this.nameA.setOnMouseClicked(event -> this.toggleTextField());
        this.nameB.setOnMouseClicked(event -> this.toggleTextField());

        var emptyL = new Region();
        HBox.setHgrow(emptyL, Priority.ALWAYS);
        var emptyR = new Region();
        HBox.setHgrow(emptyR, Priority.ALWAYS);

        this.shell = new HBox(this.nameA, this.input, this.nameB);
        this.shell.getStyleClass().add("expanding-tf-shell");
        this.shell.setAlignment(Pos.CENTER);
        this.shell.setSpacing(0);

        this.getChildren().addAll(emptyL, this.shell, emptyR);

        this.toggleTextField();
        this.toggleTextField();

    }

    /**
     * Opens/closes the component
     */
    public void toggleTextField() {
        if (!this.open.get()) {
            this.input.setVisible(true);
            //this.input.requestFocus();
        }

        var timeline = new Timeline(
            new KeyFrame(Duration.millis(500), new KeyValue(this.input.maxWidthProperty(), this.open.get() ? 0: 250)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.input.minWidthProperty(), this.open.get() ? 0: 250)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.nameA.maxWidthProperty(), this.open.get() ? 90: 0)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.nameA.minWidthProperty(), this.open.get() ? 90: 0)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.nameB.maxWidthProperty(), this.open.get() ? 90: 0)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.nameB.minWidthProperty(), this.open.get() ? 90: 0)),
            new KeyFrame(Duration.millis(500), new KeyValue(this.input.opacityProperty(), this.open.get() ? 0: 1))
        );
        
        timeline.play();

        this.open.set(!this.open.get());
        if (!this.open.get()) this.input.setVisible(false);


    }

    /**
     * Sets what happens when a user hits enter
     * @param listener 
     */
    public void setOnClickListener(OnClickListener listener) {
        this.input.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (this.input.getText().length() > 0) {
                    listener.onClick(this.input.getText());  
                    this.input.setText("");
                }

                this.toggleTextField();
            }    
        });
    }


    public void setPromptText(String text) {
        this.input.setPromptText(text);
    }

    private void updateTitle() {
        this.nameA.setText(this.title.get().substring(0, (this.title.get().length()/2)));
        this.nameB.setText(this.title.get().substring((this.title.get().length()/2)));
    }

    public SimpleStringProperty titleProperty() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
}

package uk.ac.soton.comp1206.Components.misc;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Simple toggle switch that can be clicked on/off
 * can include an icon to go beside it and choose its size from a set of prefits
 */
public class ToggleSwitch extends HBox {
    private SimpleBooleanProperty state = new SimpleBooleanProperty();

    private ImageView icon;
    private Rectangle back;
    private final Button button = new Button();

    private final double radius = 2.0;
    private final ToggleSize size;


    public enum ToggleSize {
        SMALL(30,15),
        MEDIUM(45,22.5),
        LARGE(120,60);

        private double width;
        private double height;

        private ToggleSize(double width, double height) {
            this.width = width;
            this.height = height;
        }
    }

    public ToggleSwitch(boolean initial, ToggleSize size) {
        this(initial, size, null);
    }

    public ToggleSwitch(boolean initial, ToggleSize size, Image img) {
        this.size = size;
        this.icon = new ImageView(img);

        this.build();
        this.state.set(initial);
 

    }

    public void build() {
        var shell = new StackPane();
        shell.setMinSize(size.width, size.height);
        shell.setMaxSize(size.width, size.height);

        if (this.icon != null) {
            this.icon.setPreserveRatio(true);
            this.icon.setFitHeight(size.height * 1.5);
            this.setAlignment(Pos.CENTER);
            this.setSpacing(5);
        }

        this.back = new Rectangle(size.width, size.height, Color.GREEN);
        this.back.maxWidth(size.width);
        this.back.minWidth(size.width);
        this.back.maxHeight(size.height);
        this.back.minHeight(size.height);
        this.back.setArcHeight(size.width);
        this.back.setArcWidth(size.height);
        this.back.setFill(Color.GREEN);

        this.button.setShape(new Circle(radius));
        this.button.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2);");
        StackPane.setAlignment(this.button, Pos.CENTER_RIGHT);
        this.button.setMaxSize(size.height, size.height);
        this.button.setMinSize(size.height, size.height);
        this.button.setFocusTraversable(false);

        this.button.setOnAction(e -> {this.state.set(!this.state.get());});
        this.back.setOnMouseClicked(e -> {this.state.set(!this.state.get());});

        this.state.addListener(event -> {
            if (this.state.get()) {
                this.back.setFill(Color.RED);
                StackPane.setAlignment(this.button, Pos.CENTER_LEFT);
            } else {
                this.back.setFill(Color.GREEN);
                StackPane.setAlignment(this.button, Pos.CENTER_RIGHT);
            }

        });

        
        shell.getChildren().addAll(this.back, this.button);
        this.getChildren().addAll(this.icon, shell);
    }

    public SimpleBooleanProperty stateProperty() {
        return this.state;
    }

    public boolean getState() {
        return this.state.get();
    }
}

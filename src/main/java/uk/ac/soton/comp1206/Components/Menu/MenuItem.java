package uk.ac.soton.comp1206.Components.Menu;

import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Scenes.Menu.MenuItemListener;

/**
 * Used to display each menu item
 * @author tcs1g20
 */
public class MenuItem extends StackPane {
    //When a menu item is clicked
    private MenuItemListener mil;

    //Isn't implemented properly
    private SimpleDoubleProperty scale = new SimpleDoubleProperty(1);
   
    /**
     * Constructor to create a new menu item
     * @param text The text to display on it
     * @param img The background image
     * @param onClick What to do when it is clicked
     */
    public MenuItem(String text, Image img, MenuItemListener onClick) {
        this.mil = onClick;

        this.getStyleClass().add("menu-item-shell");

        //Text to go on the item
        var lbl = new Label(text);
        lbl.getStyleClass().add("menu-item-text");
        StackPane.setAlignment(lbl, Pos.BOTTOM_LEFT);

        //Adds a linear gradient overlay to the image
        //This makes the white text easier to read

        //default//
        Stop[] stops = new Stop[] {new Stop(0.2, new Color(0, 0, 0, 0.8)), new Stop(1, new Color(1, 1, 1, 0))};
        var gradient = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
        var rectangle = new Rectangle();

        rectangle.setFill(gradient);

        //hover over//
        Stop[] hoverStops = new Stop[] {new Stop(0.45, new Color(0, 0, 0, 0.8)), new Stop(1, new Color(1, 1, 1, 0))};
        var hoverGradient = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, hoverStops);

        var up = new TranslateTransition(Duration.millis(60), lbl);

        //When hovering over an icon
        this.hoverProperty().addListener(event -> {
            if (this.isHover()) {
                up.stop();
                up.setFromY(0);
                up.setByY(-25);
                up.play();

                rectangle.setFill(hoverGradient);
            } else {
                up.stop();
                up.setFromY(-25);
                up.setByY(25);
                up.play();
                
                rectangle.setFill(gradient);
            }
        });

        //Item background image
        var background = new ImageView(img);

        //background.fitHeightProperty().bind(this.scale);
        //background.fitWidthProperty().bind(this.scale);

        background.setFitHeight(this.scale.get() * img.getHeight());
        background.setFitWidth(this.scale.get() * img.getWidth());

        background.getStyleClass().add("menu-item-image");

        //Changes the background gradient to match the image
        rectangle.widthProperty().bind(background.fitWidthProperty());
        rectangle.heightProperty().bind(background.fitHeightProperty());

        //Fills thhe vbox
        this.getChildren().addAll(
            background,
            rectangle,
            lbl
        );

        //Do this when clicked
        this.setOnMouseClicked(event -> {
            this.mil.onClick();
        });
    }

    /**
     * Sets the menu item click listener
     * @param mil
     */
    public void setOnAction(MenuItemListener mil) {
        this.mil = mil;
    }

    /**
     * Sets the scale of menu components
     * @param scale new scale
     */
    public void setScale(double scale) {
        this.scale.set(scale);
    }

    /**
     * Returnst the current menu scale
     * @return
     */
    public double getScale() {
        return this.scale.get();
    }

    /**
     * Returns the scale property
     * @return
     */
    public SimpleDoubleProperty getScaleProperty() {
        return this.scale;
    }


}

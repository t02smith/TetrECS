package uk.ac.soton.comp1206.Components.Menu;

import javafx.animation.TranslateTransition;
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
import uk.ac.soton.comp1206.Event.MenuItemListener;

public class MenuItem extends StackPane {
    private MenuItemListener mil;
   
    public MenuItem(String text, Image img, MenuItemListener onClick) {
        this.setMaxHeight(img.getHeight());
        this.setMaxWidth(img.getWidth());

        this.mil = onClick;

        this.getStyleClass().add("menu-item-shell");

        //Text to go on the item
        var lbl = new Label(text);
        lbl.getStyleClass().add("menu-item-text");
        StackPane.setAlignment(lbl, Pos.BOTTOM_LEFT);

        //Adds a linear gradient overlay to the image
        //This makes the white text easier to read

        //default//
        Stop[] stops = new Stop[] {new Stop(0.42, new Color(0, 0, 0, 0.8)), new Stop(1, new Color(1, 1, 1, 0))};
        var gradient = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
        var rectangle = new Rectangle(img.getWidth(), img.getHeight());
        rectangle.setFill(gradient);

        //hover over//
        Stop[] hoverStops = new Stop[] {new Stop(0.6, new Color(0, 0, 0, 0.8)), new Stop(1, new Color(1, 1, 1, 0))};
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
        background.getStyleClass().add("menu-item-image");

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

    public void setOnAction(MenuItemListener mil) {
        this.mil = mil;
    }
}

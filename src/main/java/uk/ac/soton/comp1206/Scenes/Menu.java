package uk.ac.soton.comp1206.Scenes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Components.Menu.MenuItem;
import uk.ac.soton.comp1206.Utility.Utility;

public class Menu extends Scene {
    private BorderPane root = new BorderPane();

    public Menu(double width, double height) {
        super(new BorderPane(), width, height);
        this.getStylesheets().add(Utility.getStyle("Menu.css"));

        //Sets the root as a border pane
        this.setRoot(this.root);
        this.root.getStyleClass().add("menu-shell");

        var menuComponents = new VBox(
            this.createTitle(), 
            this.createMenuItems()
        );

        menuComponents.setAlignment(Pos.CENTER);
        menuComponents.setSpacing(50);

        this.root.setCenter(
            menuComponents
        );
    }

    /**
     * Creates and animates the title for the menu
     * @return The title
     */
    private Label createTitle() {
        var title = new Label("TetrECS!");
        title.getStyleClass().add("menu-title");

        //Causes the title to bob up and down forever
        var timeline = new Timeline(
            new KeyFrame(Duration.millis(750), new KeyValue(title.translateYProperty(), 12)),
            new KeyFrame(Duration.millis(750), new KeyValue(title.translateYProperty(), -12))
        );

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        return title;
    }

    /**
     * Creates all the options for the main menu
     * @return The menu options
     */
    private HBox createMenuItems() {
        var vbox = new VBox(
            new MenuItem("Settings", Utility.getImage("smallMe.jpg")),
            new MenuItem("How\nTo Play", Utility.getImage("smallMe.jpg"))
        );

        vbox.setSpacing(20);


        var options = new HBox();

        options.getChildren().addAll(
            new MenuItem("Single\nPlayer", Utility.getImage("me.jpg")),
            new MenuItem("Multi\nPlayer", Utility.getImage("me.jpg")),
            vbox
        );

        options.setAlignment(Pos.BOTTOM_CENTER);
        options.setSpacing(20);
        options.setPadding(new Insets(20, 12, 30, 12));

        return options;
    }
    
}

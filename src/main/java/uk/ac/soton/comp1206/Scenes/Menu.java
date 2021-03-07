package uk.ac.soton.comp1206.Scenes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Components.Menu.MenuItem;
import uk.ac.soton.comp1206.Utility.Media;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

public class Menu extends BaseScene {

    private SimpleDoubleProperty scale = new SimpleDoubleProperty(1);

    public Menu(GameWindow window) {
        super(window);
    }

    public void build() {
        logger.info("Creating Menu scene");
        this.getStylesheets().add(Utility.getStyle("Menu.css"));
        this.getRoot().getStyleClass().add("menu-shell");

        var menuComponents = new VBox(
            this.createTitle(), 
            this.createMenuItems()
        );

        menuComponents.setAlignment(Pos.CENTER);
        menuComponents.setSpacing(50);

        ((BorderPane)this.getRoot()).setCenter(
            menuComponents
        );

        Media.playMusic("menu.mp3");
    }

    /**
     * Creates and animates the title for the menu
     * @return The title
     */
    private ImageView createTitle() {
        //Title//
        var title = new ImageView(Utility.getImage("TetrECS.png"));
        title.setPreserveRatio(true);
        title.setFitHeight(this.getHeight()*0.2);

        //Changes size based upon height of window
        this.heightProperty().addListener(event -> {
            title.setFitHeight(this.getHeight()*0.2);
        });

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
        //Smaller boxes//
        var vbox = new VBox(
            new MenuItem("Settings", Utility.getImage("smallMe.jpg"), () -> {
                logger.info("Opening settings");
            }),
            new MenuItem("Help", Utility.getImage("help.png"), () -> {logger.info("Opening instructions");})
        );

        vbox.setSpacing(20);

        //All options//
        var options = new HBox();

        options.getChildren().addAll(
            new MenuItem("Single\nPlayer", Utility.getImage("singleplayer.png"), () -> {
                logger.info("Opening singleplayer");
                this.window.loadGame();
            }),
            new MenuItem("Multi\nPlayer", Utility.getImage("multiplayer.png"), () -> {logger.info("Opening multiplayer");}),
            vbox
        );

        options.setAlignment(Pos.BOTTOM_CENTER);
        options.setSpacing(20);
        options.setPadding(new Insets(20, 12, 30, 12));

        return options;
    }
    
}

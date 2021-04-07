package uk.ac.soton.comp1206.Scenes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.Components.Menu.MenuItem;
import uk.ac.soton.comp1206.Utility.MultiMedia;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Menu scene for the user to navigate to the different scenes
 * @author tcs1g20
 */
public class Menu extends BaseScene {

    public Menu(GameWindow window) {
        super(window);
    }

    @Override
    public void build() {
        logger.info("Creating Menu scene");
        this.getStylesheets().add(Utility.getStyle("Menu.css"));
        this.root.getStyleClass().add("menu-shell");

        this.windowWidth = 800;
        this.windowHeight = 700;

        var menuComponents = new VBox(
            this.createTitle(), 
            this.createMenuItems()
        );

        menuComponents.setAlignment(Pos.CENTER);
        menuComponents.setSpacing(50);

        this.root.setCenter(
            menuComponents
        );

        //TOP//
        
        var empty = new Region();
        HBox.setHgrow(empty, Priority.ALWAYS);

        var audioIcon = new ImageView(Utility.getImage("menu/audio.png"));
        audioIcon.setPreserveRatio(true);
        audioIcon.setFitHeight(50);

        var cross = new ImageView(Utility.getImage("menu/cross.png"));
        cross.setPreserveRatio(true);
        cross.setFitHeight(50);
        cross.setOpacity(0);

        var audio = new StackPane(audioIcon, cross);
        audio.setOnMouseClicked(event -> {
            MultiMedia.toggleAudioEnabled();
            cross.setOpacity(cross.getOpacity() == 0 ? 1: 0);
        });


        var topBar = new HBox(empty, audio);
        this.root.setTop(topBar);
    }

    /**
     * Creates and animates the title for the menu
     * @return The title
     */
    private ImageView createTitle() {
        //Title//
        var title = new ImageView(Utility.getImage("menu/TetrECS.png"));
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
    private VBox createMenuItems() {
        //Smaller boxes//
        var smallIcons = new HBox (
            new MenuItem("Scores", Utility.getImage("menu/score.png"), () -> {
                logger.info("Opening leaderboard");
                this.window.loadScores();
            }),
            new MenuItem("Help", Utility.getImage("menu/help.png"), () -> {
                logger.info("Opening instructions");
                this.window.loadInstruction();
            })
        );

        smallIcons.setSpacing(20);
        smallIcons.setAlignment(Pos.BASELINE_CENTER);
        smallIcons.setPadding(new Insets(20, 12, 30, 12));

        //All options//
        var bigIcons = new HBox();

        bigIcons.getChildren().addAll(
            new MenuItem("Single\nPlayer", Utility.getImage("menu/singleplayer.png"), () -> {
                logger.info("Opening singleplayer");
                App.getInstance().openGame();
            }),
            new MenuItem("Multi\nPlayer", Utility.getImage("menu/multiplayer.png"), () -> {
                logger.info("Opening multiplayer");
                App.getInstance().openMultiplayer();
            }),
            new MenuItem("Power\nup", Utility.getImage("menu/powerup.png"), () -> {
                logger.info("Openign power up mode");
                App.getInstance().openPowerUpGame();
            })
        );

        bigIcons.setAlignment(Pos.BOTTOM_CENTER);
        bigIcons.setSpacing(20);
        bigIcons.setPadding(new Insets(20, 12, 5, 12));

        var icons = new VBox(bigIcons, smallIcons);
        icons.setAlignment(Pos.CENTER);

        return icons;
    }

    @Override
    public void playBackgroundMusic() {
        MultiMedia.playMusic("menu.mp3");
    }

    /**
     * Called when a menu item is clicked
     */
    public interface MenuItemListener {
        public void onClick();
    }
    
}

package uk.ac.soton.comp1206.ui;

import javafx.stage.Stage;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.Event.GameStartListener;
import uk.ac.soton.comp1206.Scenes.BaseScene;
import uk.ac.soton.comp1206.Scenes.GameScene;
import uk.ac.soton.comp1206.Scenes.Menu;
import uk.ac.soton.comp1206.Utility.Utility;

public class GameWindow {
    private final Stage stage;

    private int width;
    private int height;

    private Menu menu;
    private GameScene gameScene;

    private GameStartListener gsl;

    public GameWindow(Stage stage, int width, int height) {
        this.stage = stage;
        this.width = width;
        this.height = height;

        this.setupStage();  

        this.loadMenu();
    }

    private void setupStage() {
        this.stage.setTitle("Tetrecs");
        this.stage.getIcons().add(Utility.getImage("icon.png"));
        this.stage.setMinWidth(this.width);
        this.stage.setMinHeight(this.height);
        this.stage.setOnCloseRequest(event -> {
            App.getInstance().shutdown();
        });
        
    }

    public void loadScene(BaseScene scene) {
        scene.build();
        this.stage.setScene(scene);
    }

    /**
     * Called to load the menu into the stage
     */
    public void loadMenu() {
        this.menu = new Menu(this);
        this.loadScene(this.menu);
    }

    /**
     * Called to load the game onto the stage
     */
    public void loadGame() {
        this.loadScene(this.gameScene);
        this.stage.setMinWidth(850);
        this.stage.setMinHeight(700);

        this.gsl.start();
    }

    public void addGameStartListener(GameStartListener gsl) {
        this.gsl = gsl;
    }

    public GameScene getGameScene() {
        return this.gameScene;
    }

    public void setGameScene(GameScene scene) {
        this.gameScene = scene;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

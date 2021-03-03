package uk.ac.soton.comp1206.ui;

import javafx.stage.Stage;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.Scenes.BaseScene;
import uk.ac.soton.comp1206.Scenes.Menu;
import uk.ac.soton.comp1206.Utility.Utility;

public class GameWindow {
    private final Stage stage;

    private int width;
    private int height;

    private Menu menu;

    public GameWindow(Stage stage, int width, int height) {
        this.stage = stage;
        this.width = width;
        this.height = height;

        this.setupStage();

        this.menu = new Menu(this);
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

    public void loadMenu() {
        this.loadScene(this.menu);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

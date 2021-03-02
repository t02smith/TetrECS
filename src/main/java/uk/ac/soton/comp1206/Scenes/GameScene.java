package uk.ac.soton.comp1206.Scenes;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

public class GameScene extends BaseScene {
    private GridPane grid = new GridPane();
    private BorderPane root;

    public GameScene(GameWindow window) {
        super(window);
        this.root = (BorderPane)this.getRoot();
    }

    public void build() {
        logger.info("Creating game scene");
        this.getStylesheets().add(Utility.getStyle("Game.css"));
        this.root.getStyleClass().add("game-shell");

        //Game grid//
        this.root.setCenter(this.grid);
    }
}

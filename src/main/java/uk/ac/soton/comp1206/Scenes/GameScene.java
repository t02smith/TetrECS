package uk.ac.soton.comp1206.Scenes;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.Components.Game.Board;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

public class GameScene extends BaseScene {
    private Board grid;
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
        this.grid = new Board(8, 8, (x, y) -> {
            logger.info("[{}, {}]", x, y);
        });
        this.root.setCenter(this.grid);

        //Key events//
        this.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logger.info("Returning to menu");
                this.window.loadMenu();
            }
        });
    }
}

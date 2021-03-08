package uk.ac.soton.comp1206.Scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.ui.GameWindow;

public abstract class BaseScene extends Scene {
    protected static final Logger logger = LogManager.getLogger(BaseScene.class);

    protected final GameWindow window;

    protected BorderPane root;
    //protected GamePane root;

    public BaseScene(GameWindow window) {
        super(new BorderPane(), window.getWidth(), window.getHeight());
        this.window = window;
        this.root = (BorderPane)this.getRoot();

        this.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logger.info("Returning to menu");
                this.window.loadMenu();
            }
        });
    }

    public abstract void build();
}

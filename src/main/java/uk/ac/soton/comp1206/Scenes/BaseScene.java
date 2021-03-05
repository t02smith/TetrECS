package uk.ac.soton.comp1206.Scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public abstract class BaseScene extends Scene {
    protected static final Logger logger = LogManager.getLogger(BaseScene.class);

    protected final GameWindow window;
    protected GamePane root;

    public BaseScene(GameWindow window) {
        super(new BorderPane(), window.getWidth(), window.getHeight());
        this.window = window;
    }

    public abstract void build();
}

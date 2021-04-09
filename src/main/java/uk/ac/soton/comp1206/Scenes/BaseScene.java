package uk.ac.soton.comp1206.Scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.Event.KeyBinding;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Abstract base scene class
 * Sets any default components and properties found in all scenes
 * @author tcs1g20
 */
public abstract class BaseScene extends Scene {
    protected static final Logger logger = LogManager.getLogger(BaseScene.class);

    //The window that the scene is displayed on
    protected final GameWindow window;

    //The root component
    protected BorderPane root;

    protected double windowWidth = 700;
    protected double windowHeight = 500;

    /**
     * Creates a basescene
     *  creates root node, and assigns default key bindings
     * @param window
     */
    public BaseScene(GameWindow window) {
        super(new BorderPane(), window.getWidth(), window.getHeight());
        this.window = window;
        this.root = (BorderPane)this.getRoot();

        this.setKeyBindings();
    }

    /**
     * Sets any default key bindings
     */
    public void setKeyBindings() {
        this.setOnKeyReleased(event -> {
            KeyBinding.executeEvent(event.getCode());
        });

        KeyBinding.ESCAPE.setEvent(() -> {
            this.window.revertScene();
        });

    }
    
    /**
     * Sets the dimensions of the window
     */
    public void setDimension() {
        this.window.setSize(this.windowWidth, this.windowHeight);
    }

    /**
     * Builds the components in the current scene
     */
    public abstract void build();

    /**
     * Plays background music
     */
    public abstract void playBackgroundMusic();



}

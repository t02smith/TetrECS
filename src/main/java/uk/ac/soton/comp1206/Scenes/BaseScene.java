package uk.ac.soton.comp1206.Scenes;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.Event.Action;
import uk.ac.soton.comp1206.Event.ActionTag;
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

    //Default dimensions
    protected double windowWidth = 700;
    protected double windowHeight = 500;

    //Actions available when in this scene
    protected ArrayList<ActionTag> tags = new ArrayList<>();

    /**
     * Creates a basescene
     *  creates root node, and assigns default key bindings
     * @param window
     * @param tags List of action tags available on this scene 
     */
    public BaseScene(GameWindow window, ActionTag... tags) {
        super(new BorderPane(), window.getWidth(), window.getHeight());
        this.window = window;
        this.root = (BorderPane)this.getRoot();

        //Adds any tags
        this.tags.addAll(Arrays.asList(tags));

        this.setKeyBindings();
    }

    /**
     * Sets any default key bindings
     */
    public void setKeyBindings() {
        this.setOnKeyReleased(event -> {
            Action.executeEvent(event.getCode());
        });

        Action.ESCAPE.setEvent(() -> {
            this.window.revertScene();
        });

    }
    
    /**
     * Sets the dimensions of the window
     */
    public void setDimension() {
        this.window.setSize(this.windowWidth, this.windowHeight);
    }

    public void resetActiveTags() {
        //Removes any 
        ActionTag.resetActiveTags();

        //Adds any needed tags to allow certain actions
        ActionTag.activeTags.addAll(this.tags);
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

package uk.ac.soton.comp1206.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.text.Font;
import javafx.stage.Stage;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.Event.GameStartListener;
import uk.ac.soton.comp1206.Scenes.BaseScene;
import uk.ac.soton.comp1206.Scenes.ChallengeScene;
import uk.ac.soton.comp1206.Scenes.ChannelScene;
import uk.ac.soton.comp1206.Scenes.InstructionScene;
import uk.ac.soton.comp1206.Scenes.LobbyScene;
import uk.ac.soton.comp1206.Scenes.Menu;
import uk.ac.soton.comp1206.Scenes.ScoresScene;
import uk.ac.soton.comp1206.Utility.Stack;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.Multiplayer.Channel;

/**
 * This class will store the stage for each scene
 * You would use this class to transition between different scenes
 *  and change aspects like size
 * @author tcs1g20
 */
public class GameWindow {
    private static final Logger logger = LogManager.getLogger(GameWindow.class);

    //The stage the scenes will be shown on
    private final Stage stage;

    //The stage's dimensions
    private int width;
    private int height;

    //Stores scenes as they are loaded so that they can be traversed back
    private Stack<BaseScene> scenes = new Stack<>();

    //SCENES

    private Menu menu;
    private InstructionScene instructionScene;

    private ChallengeScene gameScene;
    private ScoresScene scoresScene;

    private LobbyScene lobbyScene;
    private ChannelScene channel;

    //Called when the game starts
    private GameStartListener gsl;

    public GameWindow(Stage stage, int width, int height) {
        this.stage = stage;
        this.width = width;
        this.height = height;

        this.scoresScene = new ScoresScene(this);
        this.instructionScene = new InstructionScene(this);
        this.menu = new Menu(this);

        this.setupStage();
        
        this.getResources();

        this.loadMenu();
    }

    /**
     * Sets up the initial stage
     */
    private void setupStage() {
        logger.info("Setting up stage");

        this.stage.setTitle("Tetrecs");
        this.stage.getIcons().add(Utility.getImage("icon.png"));
        this.stage.setMinWidth(this.width);
        this.stage.setMinHeight(this.height);
        this.stage.setOnCloseRequest(event -> {
            App.getInstance().shutdown();
        });

    }

    /**
     * Collects any necessaruy resources at launch
     */
    private void getResources() {
        Font.loadFont(this.getClass().getResourceAsStream("/font/ka1.ttf"), 16);
    }

    /**
     * Loads a given scene and builds it
     * @param scene The scene to be shown
     */
    public void loadScene(BaseScene scene) {
        logger.info("Loading scene {}", scene);

        scene.playBackgroundMusic();
        scene.build();
        scene.setDimension();
        if (this.stage.getScene() != null) this.scenes.push((BaseScene)this.stage.getScene());
        this.stage.setScene(scene);
    }

    /**
     * Moves the scene back to what it was previously
     */
    public void revertScene() {
        if (this.scenes.size() > 0) {
            logger.info("Reverting scene");
            var previous = this.scenes.pop();
            previous.setKeyBindings();
            this.stage.setScene(previous);
            previous.setDimension();

            previous.playBackgroundMusic();
        }
    }

    /**
     * displays a scene without adding the previous one to the scene stack
     * @param scene the new scene
     */
    public void replaceScene(BaseScene scene) {
        logger.info("Replacing current scene");
        scene.build();
        scene.playBackgroundMusic();
        scene.setDimension();
        this.stage.setScene(scene);
    }

    /**
     * Used to set the desired dimensions of a window
     * @param width The min/desired width
     * @param height The min/desired height
     */
    public void setSize(double width, double height) {
        logger.info("Setting window to {} x {}", width, height);
        this.stage.setMinWidth(width);
        this.stage.setWidth(width);

        this.stage.setMinHeight(height);
        this.stage.setHeight(height);
    }

    //Loading scenes//

    //Menu//

    /**
     * Called to load the menu into the stage
     */
    public void loadMenu() {
        this.loadScene(this.menu);
    }

    //Single player//

    /**
     * Called to load the game onto the stage
     */
    public void loadGame() {
        this.loadScene(this.gameScene);

        this.gsl.start();
    }

    /**
     * Sets the game scene
     * Different game modes will have different scenes 
     *  all of which will be ChallengeScene or a child of it
     * @param scene The new game scene
     */
    public void setGameScene(ChallengeScene scene) {
        this.gameScene = scene;
    }

    /**
     * @return The current game scene
     */
    public ChallengeScene getGameScene() {
        return this.gameScene;
    }

    //Multiplayer//

    /**
     * Sets the current lobby scene
     * @param scene The lobby scene
     */
    public void setLobbyScene(LobbyScene scene) {
        this.lobbyScene = scene;
    }

    /**
     * Loads the lobby scene
     */
    public void loadLobby() {
        this.loadScene(this.lobbyScene);
    }

    /**
     * Loads a current channel
     * @param channel The channel we are going to display
     */
    public void loadChannel(Channel channel) {
        this.channel = new ChannelScene(this, channel);
        this.loadScene(this.channel);
    }

    /**
     * @return The current channel scene
     */
    public ChannelScene getChannelScene() {
        return this.channel;
    }


    //Help screen//

    /**
     * Loads the instruction scene
     */
    public void loadInstruction() {
        this.loadScene(this.instructionScene);
    }

    //Scores//

    /**
     * Loads the scoreboard once the game has finished
     */
    public void loadScores() {
        this.loadScene(this.scoresScene);
    }

    /**
     * @return the current scores scene
     */
    public ScoresScene getScoresScene() {
        return this.scoresScene;
    }

    /**
     * Sets the listener for whenever we start a game
     * @param gsl the listener called when we load a game
     */
    public void addGameStartListener(GameStartListener gsl) {
        this.gsl = gsl;
    }

    //DIMENSIONS//

    /**
     * @return The stage's width
     */
    public double getWidth() {
        return this.stage.getWidth();
    }

    /**
     * @return The stage's height
     */
    public double getHeight() {
        return this.stage.getHeight();
    }

}

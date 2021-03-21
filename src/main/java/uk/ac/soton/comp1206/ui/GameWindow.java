package uk.ac.soton.comp1206.ui;

import javafx.scene.text.Font;
import javafx.stage.Stage;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.Event.GameStartListener;
import uk.ac.soton.comp1206.Scenes.BaseScene;
import uk.ac.soton.comp1206.Scenes.ChallengeScene;
import uk.ac.soton.comp1206.Scenes.InstructionScene;
import uk.ac.soton.comp1206.Scenes.LobbyScene;
import uk.ac.soton.comp1206.Scenes.Menu;
import uk.ac.soton.comp1206.Scenes.ScoresScene;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * This class will store the stage for each scene
 * You would use this class to transition between different scenes
 *  and change aspects like size
 */
public class GameWindow {
    private final Stage stage;

    private int width;
    private int height;

    private Menu menu;
    private ChallengeScene gameScene;
    private ScoresScene scoresScene;
    private LobbyScene lobbyScene;
    private InstructionScene instructionScene;

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
        Font.loadFont(this.getClass().getResourceAsStream("/font/Tally Mark.ttf"), 16);
    }

    /**
     * Loads a given scene and builds it
     * @param scene The scene to be shown
     */
    public void loadScene(BaseScene scene) {
        scene.build();
        this.stage.setScene(scene);
    }

    /**
     * Used to set the desired dimensions of a window
     * @param width The min/desired width
     * @param height The min/desired height
     */
    public void setSize(double width, double height) {
        this.stage.setMinWidth(width);
        this.stage.setWidth(width);

        this.stage.setMinHeight(height);
        this.stage.setHeight(height);
    }

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


    public void setGameScene(ChallengeScene scene) {
        this.gameScene = scene;
    }

    public ChallengeScene getGameScene() {
        return this.gameScene;
    }


    //Multiplayer

    public void loadMultiplayer() {
        this.loadScene(this.gameScene);

        this.gsl.start();
    }

    public void setLobbyScene(LobbyScene scene) {
        this.lobbyScene = scene;
    }

    public void loadLobby() {
        this.loadScene(this.lobbyScene);
    }


    //Help screen//

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

    public ScoresScene getScoresScene() {
        return this.scoresScene;
    }

    public void addGameStartListener(GameStartListener gsl) {
        this.gsl = gsl;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }






}

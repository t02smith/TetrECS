package uk.ac.soton.comp1206;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Network.NetworkProtocol;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Multiplayer.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GameWindow;

public class App extends Application {
    private static App instance;
    private static final Logger logger = LogManager.getLogger(App.class);

    private Stage stage;

    //Window to display the game on
    private GameWindow gameWindow; 

    //Current instance of game
    //Singleplayer and multiplayer cannot both be played at once
    private Game game;

    //Communicator to communicate with the ECS server for online play
    private Communicator communicator;

    public static void main(String[] args) {
        logger.info("Starting client");
        launch();
    }

    /**
     * Starts the application
     */
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        this.gameWindow = new GameWindow(this.stage, 700, 500);
        this.communicator = new Communicator("ws://discord.ecs.soton.ac.uk:9700");



        this.stage.show();

    }

    /**
     * Opens up a singlepayer game onto the window
     */
    public void openGame() {
        logger.info("Opening game window");
        this.game = new Game(this.gameWindow, this.communicator);
        this.game.buildGame();
        this.gameWindow.loadGame();
    }

    /**
     * Opens up a multiplayer game onto the window
     */
    public void openMultiplayer() {
        logger.info("Starting multiplayer");
        this.game = new MultiplayerGame(this.gameWindow, this.communicator);
        this.game.buildGame();
        this.gameWindow.loadLobby();

    }

    public void openScores() {
        logger.info("Opening scores");
        this.communicator.send("HISCORES");
        this.gameWindow.loadScores();
    }

    /**
     * Shuts down the game
     */
    public void shutdown() {
        logger.info("Shutting down");
        this.communicator.send("QUIT");
        System.exit(0);
    }

    public void setupCommunicator() {
        //Checks if we have setup a network protocol for a received message
        this.communicator.addListener(message -> {
            for (NetworkProtocol received: NetworkProtocol.values()) {
                if (message.matches(received.getResult())) {
                    received.doAction(message);
                    break;
                }
            }
        });

        NetworkProtocol.HISCORES.addListener(message -> {
            //this.gameWindow.setOnlineScores(message)
            
        });

    }


    /**
     * Returns the current instance of App
     * @return
     */
    public static App getInstance() {
        return instance;
    }


    
}
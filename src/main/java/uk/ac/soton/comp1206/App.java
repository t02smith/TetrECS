package uk.ac.soton.comp1206;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Network.NetworkProtocol;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Multiplayer.MultiplayerGame;
import uk.ac.soton.comp1206.game.Powerup.PowerUpGame;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * App class to run the javafx program
 * @author tcs1g20
 */
public class App extends Application {
    //The instance of the app currently in use
    private static App instance;
    private static final Logger logger = LogManager.getLogger(App.class);

    //The stage displayed by the app
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
    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        this.gameWindow = new GameWindow(this.stage, 700, 500);

        this.communicator = new Communicator("ws://discord.ecs.soton.ac.uk:9700");
        new Thread(this.communicator, "Communicator Thread").start();

        this.setupCommunicator();

        this.stage.show();

    }

    //OPENING SCENES//

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
     * Opens up a new power up game
     */
    public void openPowerUpGame() {
        logger.info("Opening power up game");
        this.game = new PowerUpGame(this.gameWindow, this.communicator);
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

    /**
     * Shuts down the game
     */
    public void shutdown() {
        logger.info("Shutting down");
        this.communicator.send("QUIT");
        System.exit(0);
    }

    /**
     * Sets up any default communicator settings
     */
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

        //By default log any errors
        NetworkProtocol.ERROR.addListener(message -> logger.error(message));
        
        //We set this up here so we can retrieve the scores when we load the program
        NetworkProtocol.HISCORES.addListener(message -> {
            logger.info("Setting online scores");
            message = message.substring(9);
            System.out.println(message);

            this.gameWindow.getScoresScene().setOnlineScores(message);
            Utility.writeToFile(
                "scores/remoteScores.txt", 
                message, 
                false
            );
        });

        this.communicator.send("HISCORES");

    }


    /**
     * Returns the current instance of App
     * @return
     */
    public static App getInstance() {
        return instance;
    }

}
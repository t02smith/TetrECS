package uk.ac.soton.comp1206;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

public class App extends Application {
    private static App instance;
    private static final Logger logger = LogManager.getLogger(App.class);

    private Stage stage;

    private Communicator communicator;

    public static void main(String[] args) {
        logger.info("Starting client");
        launch();
    }

    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        this.communicator = new Communicator("ws://discord.ecs.soton.ac.uk:9700");

        this.openGame();

    }

    @SuppressWarnings("unused")
    public void openGame() {
        logger.info("Opening game window");
        var window = new GameWindow(this.stage, 700, 500);
        var game = new Game(window, this.communicator);

        this.stage.show();
    }

    /**
     * Shutdowns the game
     */
    public void shutdown() {
        logger.info("Shutting down");
        System.exit(0);
    }

    /**
     * Returns the current instance of App
     * @return
     */
    public static App getInstance() {
        return instance;
    }



    
}
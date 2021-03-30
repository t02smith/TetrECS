package uk.ac.soton.comp1206.Scenes;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import uk.ac.soton.comp1206.Components.Game.Scoreboard;
import uk.ac.soton.comp1206.Components.multiplayer.TextToolbar;
import uk.ac.soton.comp1206.Event.SubmitScoreListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Scene to display the list of local and online scores
 * After a game a user can submit their score
 * @author tcs1g20
 */
public class ScoresScene extends BaseScene {
    private Scoreboard localScores;
    private Scoreboard onlineScores;

    private VBox components;

    private SimpleBooleanProperty userPlayed = new SimpleBooleanProperty(false);
    private int userScore;

    private SubmitScoreListener ssl;

    public ScoresScene(GameWindow gw) {
        super(gw);
        this.localScores = new Scoreboard("Local Score", Utility.readFromFile("scores/localScores.txt"));
    }

    /**
     * Builds the score scene when loaded
     */
    @Override
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Scores.css"));
        this.root.getStyleClass().add("score-shell");

        //Holds all the main components on the scene
        this.components = new VBox();
        this.components.setSpacing(25);
        this.components.setAlignment(Pos.CENTER);

        //SCORES//
        var scoreLists = new HBox();
        scoreLists.getChildren().addAll(
            this.localScores,
            this.onlineScores
        );

        scoreLists.setAlignment(Pos.TOP_CENTER);
        scoreLists.setSpacing(25);

        if (this.userPlayed.get()) {
            this.buildSendUsername();
        }
        
        this.components.getChildren().add(scoreLists);       

        this.root.setCenter(this.components);
    }

    /**
     * Creates the component to send off the user's name and score
     */
    private void buildSendUsername() {
        var finalScore = new Label("Final Score: " + this.userScore);
        finalScore.getStyleClass().add("final-score");

        var nameInput = new TextToolbar(name -> {
            this.ssl.submit(name, this.userScore);
            this.localScores.addUser(name, this.userScore);
            this.onlineScores.addUser(name, this.userScore);
            this.userPlayed.set(false);
            
        });

        //Removes the name input after the score has been submitted
        this.userPlayed.addListener(event -> {
            if (!this.userPlayed.get()) {
                this.components.getChildren().remove(nameInput);
            }
        });

        nameInput.getStyleClass().add("name-input");
        nameInput.setPromptText("Enter your name :)");
        nameInput.setMaxWidth(250);

        nameInput.setAlignment(Pos.CENTER);
        nameInput.setTextAlignment(Pos.CENTER);

        this.components.getChildren().add(0, nameInput);
        this.components.getChildren().add(0, finalScore);
    }


    /**
     * called when the list of online scores is retrieved
     * @param onlineScores The string received from the server
     */
    public void setOnlineScores(String onlineScores) {
        logger.info("Retrieved online scores");
        ArrayList<String> online = new ArrayList<>(Arrays.asList(onlineScores.split("\\s+")));
        online.remove(0);

        //Utility.writeToFile("/scores/remoteScores.txt", onlineScores);
        this.onlineScores = new Scoreboard("Online Scores", online);
    }

    /**
     * Returns the highest score
     * @return the highest score
     */
    public Pair<String, Integer> getHighScore() {
        var localHigh = this.localScores.getHighScore();

        //If there has been an error getting the online scores
        if (this.onlineScores == null) return (localHigh.toPair());

        //If we have a copy of online highscores return the highest from both local and online
        var onlineHigh = this.onlineScores.getHighScore();
        return (localHigh.getValue() > onlineHigh.getValue()) ? localHigh.toPair() : onlineHigh.toPair();
    }

    /**
     * Returns the local scores component for use outside of the scorescene
     * The local scores are guaranteed to be available
     * @return the local scoreboard
     */
    public VBox getLocalScoreboard() {
        return this.localScores;
    }

    public void addSubmitScoreListener(SubmitScoreListener ssl) {
        this.ssl = ssl;
    }

    public void setUserScore(int score) {
        this.userScore = score;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.userPlayed.set(hasPlayed);
    }
}

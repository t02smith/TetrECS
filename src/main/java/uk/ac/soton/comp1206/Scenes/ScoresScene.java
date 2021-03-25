package uk.ac.soton.comp1206.Scenes;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import uk.ac.soton.comp1206.Components.Game.Scoreboard;
import uk.ac.soton.comp1206.Event.SubmitScoreListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresScene extends BaseScene {
    private Scoreboard localScores;
    private Scoreboard onlineScores;

    private boolean userPlayed = false;
    private int userScore;

    private SubmitScoreListener ssl;

    public ScoresScene(GameWindow gw) {
        super(gw);
        this.localScores = new Scoreboard("Local Score", Utility.readFromFile("/scores/localScores.txt"));
    }

    /**
     * Builds the score scene when loaded
     */
    @Override
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Scores.css"));
        this.root.getStyleClass().add("score-shell");

        //Holds all the main components on the scene
        var components = new VBox();
        components.setSpacing(25);
        components.setAlignment(Pos.CENTER);

        //Enter usernme//

        HBox submit = null;
        Label finalScore = null;

        //If the user has completed a game then give them the option to submit a score
        if (this.userPlayed) {
            //Displays the user's score
            finalScore = new Label("Final Score " + this.userScore);
            finalScore.getStyleClass().add("final-score");

            //name input and button to submit their name and score
            var name = new TextField();
            name.getStyleClass().add("name-input");
            name.setPromptText("Enter your name :)");
            name.setMaxWidth(250);
            name.setMinHeight(30);
    
            //Click to submit a score online
            var submitScore = new Button();
            submitScore.setOnAction(e -> {
                this.ssl.submit(name.getText(), this.userScore);
                this.userPlayed = false;
                this.window.loadScores();
            });
    
            submit = new HBox(
                name,
                submitScore
            );

            submit.setAlignment(Pos.CENTER);
        }


        //SCORES//
        var scoreLists = new HBox();
        scoreLists.getChildren().addAll(
            this.localScores,
            this.onlineScores
        );

        scoreLists.setAlignment(Pos.TOP_CENTER);
        scoreLists.setSpacing(25);

        //If the user hasn't played, exclude the submit score function
        if (this.userPlayed) components.getChildren().addAll(finalScore, submit, scoreLists);
        else components.getChildren().addAll(scoreLists);
        

        this.root.setCenter(components);
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
        if (this.onlineScores == null) return (localHigh);

        //If we have a copy of online highscores return the highest from both local and online
        var onlineHigh = this.onlineScores.getHighScore();
        return (localHigh.getValue() > onlineHigh.getValue()) ? localHigh : onlineHigh;
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
        this.userPlayed = hasPlayed;
    }
}

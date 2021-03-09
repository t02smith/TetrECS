package uk.ac.soton.comp1206.Scenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import uk.ac.soton.comp1206.Event.SubmitScoreListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresScene extends BaseScene {
    private SimpleListProperty<Pair<String, Integer>> localScores;
    private SimpleListProperty<Pair<String, Integer>> onlineScores;

    private boolean userPlayed = false;
    private int userScore;

    private SubmitScoreListener ssl;

    private static final Color[] scoreColors = {
        Color.HOTPINK, Color.RED, Color.ORANGE, Color.YELLOW, Color.YELLOWGREEN, Color.LIME, Color.GREEN, Color.DARKGREEN, Color.CYAN, Color.BLUE};

    public ScoresScene(GameWindow gw) {
        super(gw);
        this.localScores = this.createScoreList(Utility.readFromFile("/scores/localScores.txt"));
    }

    @Override
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Scores.css"));
        this.root.getStyleClass().add("score-shell");

        var components = new VBox();
        components.setSpacing(25);
        components.setAlignment(Pos.CENTER);

        //Enter usernme//

        HBox submit = null;
        Label finalScore = null;
        if (this.userPlayed) {
            finalScore = new Label("Final Score " + this.userScore);
            finalScore.getStyleClass().add("final-score");

            var name = new TextField();
            name.getStyleClass().add("name-input");
            name.setPromptText("Enter your name :)");
            name.setMaxWidth(250);
            name.setMinHeight(30);
    
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
            this.createScoreboard("Local scores", this.localScores), 
            this.createScoreboard("Online score", this.onlineScores)
        );

        scoreLists.setAlignment(Pos.TOP_CENTER);
        scoreLists.setSpacing(25);

        if (this.userPlayed) components.getChildren().addAll(finalScore, submit, scoreLists);
        else components.getChildren().addAll(scoreLists);
        

        this.root.setCenter(components);
    }

    /**
     * Creates a scoreboard component to visually show the scores
     * @param name The scoreboard's title
     * @param scores The arraylist of scores
     * @return The scoreboard component
     */
    private VBox createScoreboard(String name, SimpleListProperty<Pair<String, Integer>> scores) {
        var scoreList = new GridPane();

        int max = scores.getSize() < 10 ? scores.getSize(): 10;
        for (int i = 0; i < max; i++) {
            var score = scores.get(i);

            var pos = new Label(String.valueOf(i+1) + " ");
            pos.setStyle("-fx-text-fill: '#" + scoreColors[i].toString().substring(2) + "';");
            scoreList.add(pos, 0, i);

            var username = new Label(score.getKey() + " ");
            username.setStyle("-fx-text-fill: '#" + scoreColors[i].toString().substring(2) + "';");
            scoreList.add(username, 1, i);

            var scoreLbl = new Label(score.getValue().toString());
            scoreLbl.setStyle("-fx-text-fill: '#" + scoreColors[i].toString().substring(2) + "';");
            scoreList.add(scoreLbl, 2, i);


        }

        var vbox = new VBox(
            new Label(name),
            scoreList
        );

        vbox.getStyleClass().add("scoreboard");
        vbox.setAlignment(Pos.TOP_CENTER);

        return vbox;
    }

    /**
     * called when the list of online scores is retrieved
     * @param onlineScores The string received from the server
     */
    public void setOnlineScores(String onlineScores) {
        ArrayList<String> online = new ArrayList<>(Arrays.asList(onlineScores.split("\\s+")));
        online.remove(0);

        this.onlineScores = this.createScoreList(online);
    }

    /**
     * Creates a sorted list of pairs for users and their scores
     * @param lines a list of lines containing names and scores
     * @return a sorted list of users and scores
     */
    private SimpleListProperty<Pair<String, Integer>> createScoreList(ArrayList<String> lines) {
        if (lines == null) return null;

        var pairList = new ArrayList<Pair<String, Integer>>();
        lines.forEach(score -> {
            String[] scoreSplit = score.split(":");
            pairList.add(
                new Pair<String, Integer>(scoreSplit[0], Integer.parseInt(scoreSplit[1]))
            );
        });

        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList(pairList);

        //Sorts the scores highest to lowest
        Comparator<Pair<String, Integer>> comparator = Comparator.comparingInt(Pair::getValue);
        FXCollections.sort(observableList, comparator.reversed());

        logger.info("Scores loaded");
        return new SimpleListProperty<>(observableList);
    }

    /**
     * Sends the score off to the server
     * @param name
     * @param score
     */
    public void sendScore(String name, int score) {

    }

    /**
     * Returns the highest score
     * @return the highest score
     */
    public String getHighScore() {
        var localHigh = this.localScores.get(0);
        var onlineHigh = this.onlineScores.get(0);

        return 
           (localHigh.getValue() > onlineHigh.getValue()) ?
            String.format("%s: %d", localHigh.getKey(), localHigh.getValue())
            : String.format("%s: %d", onlineHigh.getKey(), onlineHigh.getValue());
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

package uk.ac.soton.comp1206.Scenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresScene extends BaseScene {
    private SimpleListProperty<Pair<String, Integer>> localScores;
    private SimpleListProperty<Pair<String, Integer>> onlineScores;

    public ScoresScene(GameWindow gw) {
        super(gw);
        this.localScores = this.createScoreList(Utility.readFromFile("/scores/localScores.txt"));
    }

    @Override
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Scores.css"));

        var components = new VBox();
        components.setAlignment(Pos.CENTER);

        //Enter usernme//

        var name = new TextField();
        name.getStyleClass().add("name-input");
        name.setPromptText("Enter your name :)");
        name.setMaxWidth(250);
        name.setMinHeight(100);

        //SCORES//
        var scoreLists = new HBox();
        scoreLists.getChildren().addAll(
            this.createScoreboard("Local scores", this.localScores), 
            this.createScoreboard("Online score", this.onlineScores)
        );

        scoreLists.setAlignment(Pos.TOP_CENTER);
        scoreLists.setSpacing(25);

        components.getChildren().addAll(name, scoreLists);

        this.root.setCenter(components);
    }

    /**
     * Creates a scoreboard component to visually show the scores
     * @param name The scoreboard's title
     * @param scores The arraylist of scores
     * @return The scoreboard component
     */
    private VBox createScoreboard(String name, SimpleListProperty<Pair<String, Integer>> scores) {
        var vbox = new VBox(
            new Label(name)
        );

        int max = scores.getSize() < 10 ? scores.getSize(): 10;
        for (int i = 0; i < max; i++) {
            var score = scores.get(i);
            var lbl = new Label(
                String.format("%d. %-15s %d", i+1, score.getKey(), score.getValue())
            );

            vbox.getChildren().add(lbl);
        }

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

    public void addScore(String name, int score) {

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
}

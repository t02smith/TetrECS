package uk.ac.soton.comp1206.Components.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 * Scoreboard to display a list of scores
 */
public class Scoreboard extends VBox {
    private String name;
    private SimpleListProperty<Pair<String, Integer>> scores;

    private GridPane scoreBoard;

    //Different colours the scores can be
    private static final Color[] scoreColors = {
        Color.HOTPINK, Color.RED, Color.ORANGE, Color.YELLOW, Color.YELLOWGREEN, Color.LIME, Color.GREEN, Color.DARKGREEN, Color.CYAN, Color.BLUE
    };

    /**
     * Constructor to create scoreboard from scores
     * @param scores List of scores expect <name>:<score>\n
     */
    public Scoreboard(String name, Collection<String> scores) {
        this.name = name;
        this.createScoreList(scores);
        this.build();
    }

    /**
     * Builds the scoreboard
     */
    public void build() {
        this.scoreBoard = new GridPane();

        //Finds the top 10 or less if there aren't 10
        int max = this.scores.getSize() < 10 ? scores.getSize(): 10;
        for (int i = 0; i < max; i++) {
            var score = this.scores.get(i);

            //Adds the position, name and score on the scoreboard with a selected colour
            var pos = new Label(String.valueOf(i+1) + " ");
            pos.setStyle("-fx-text-fill: '#" + scoreColors[i].toString().substring(2) + "';");
            this.scoreBoard.add(pos, 0, i);

            var username = new Label(score.getKey() + " ");
            username.setStyle("-fx-text-fill: '#" + scoreColors[i].toString().substring(2) + "';");
            this.scoreBoard.add(username, 1, i);

            var scoreLbl = new Label(score.getValue().toString());
            scoreLbl.setStyle("-fx-text-fill: '#" + scoreColors[i].toString().substring(2) + "';");
            this.scoreBoard.add(scoreLbl, 2, i);
        }

        var title = new Label(this.name);
        title.setStyle("-fx-text-fill: '#ffffff';");

        this.getChildren().addAll(title, this.scoreBoard);
        //this.getStyleClass().add("scoreboard");

        this.setStyle(
            "-fx-font-family: 'Karmatic Arcade';"
          + "-fx-font-size: 20;"
          + "-fx-padding: 20;"
          + "-fx-background-insets: 20;"
          + "-fx-border-insets: 20;"
        );

        this.setAlignment(Pos.TOP_CENTER);
    }

    /**
     * Creates the simplelistproperty from a given array
     * @param scores the list of scores
     */
    private void createScoreList(Collection<String> scores) {
        //Collects names and scores from a given set of lines (i.e. from the server)       
        var pairList = new ArrayList<Pair<String, Integer>>();
        scores.forEach(score -> {
            String[] scoreSplit = score.split(":");
            pairList.add(
                new Pair<String, Integer>(scoreSplit[0], Integer.parseInt(scoreSplit[1]))
            );
        });

        
        ObservableList<Pair<String, Integer>> observableList = FXCollections.observableArrayList(pairList);

        //Sorts the scores highest to lowest
        Comparator<Pair<String, Integer>> comparator = Comparator.comparingInt(Pair::getValue);
        FXCollections.sort(observableList, comparator.reversed());

        this.scores = new SimpleListProperty<>(observableList);

    }

    /**
     * Returns the highest score
     * @return the high score
     */
    public Pair<String, Integer> getHighScore() {
        return this.scores.get(0);
    }

}

package uk.ac.soton.comp1206.Scenes;

import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Sidebar;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Event.TileClickListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ChallengeScene extends BaseScene {
    private Grid grid;
    private ProgressBar timer;

    private VBox localScores;
    private Sidebar sidebar;

    private HashMap<String,TileClickListener> listeners = new HashMap<>();

    private int width = 5;
    private int height = 5;

    private Pair<String, Integer> highScore;

    public ChallengeScene(GameWindow window) {
        super(window);
    }

    @Override
    public void build() {
        logger.info("Creating game scene");
        this.getStylesheets().add(Utility.getStyle("Game.css"));
        this.root.getStyleClass().add("game-shell");

        //Top of Screen//

        var title = new Label("CHALLENGE MODE!");
        title.getStyleClass().add("banner");
        var empty = new Region();
        HBox.setHgrow(empty, Priority.ALWAYS);

        var highScoreLbl = new Label(
            String.format("High Score %s -> %d", this.highScore.getKey(), this.highScore.getValue())
        );
        highScoreLbl.getStyleClass().add("banner");

        var banner = new HBox(title, empty, highScoreLbl);
        this.root.setTop(banner);

        //Center of screen//
        //Game grid//
        this.grid = new Grid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));        

        this.timer = new ProgressBar();
        this.timer.setMinWidth(GridSize.LARGE.getSideLength()*this.width);
        this.timer.setMinHeight(GridSize.LARGE.getSideLength()/4);
        this.timer.getStyleClass().add("timer");

        //Changes the timer's colur based on the time left
        this.timer.progressProperty().addListener(event -> {
            if (this.timer.getProgress() < 0.25) this.timer.setStyle("-fx-accent: red;");
            else if (this.timer.getProgress() < 0.5) this.timer.setStyle("-fx-accent: orange;");
            else if (this.timer.getProgress() < 0.75) this.timer.setStyle("-fx-accent: yellow;");
            else this.timer.setStyle("-fx-accent: green;");
        });

        var center = new VBox(this.grid, this.timer);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(25);

        this.root.setCenter(center);

        //Sidebar//
        this.sidebar = new Sidebar();

        //Listeners
        this.sidebar.addTileClickListener("next-piece", this.listeners.get("next-piece"));
        this.sidebar.addTileClickListener("reserve-piece", this.listeners.get("reserve-piece"));
        this.sidebar.build();

        this.sidebar.getToggle().stateProperty().bindBidirectional(this.grid.lockSelectedProperty());

        var sidePanel = new HBox(this.sidebar);

        //When the window gets wider
        /*
        this.widthProperty().addListener(event -> {
            if (this.getWidth() > 1000) {
                if (sidePanel.getChildren().size() != 2) {
                    sidePanel.getChildren().add(this.localScores);
                }
            } else {
                if (sidePanel.getChildren().size() == 2) {
                    sidePanel.getChildren().remove(1);
                }
            }
        });*/

        this.root.setRight(sidePanel);
    }

    /**
     * Displays the next piece to be played in the appropriate grid
     * @param gp the new piece
     */
    public void setNextPiece(GamePiece gp) {
        this.sidebar.setNextElement(gp);
    }

    /**
     * Displays the reserve piece on the appropraite grid
     * @param gp the reserve piece
     */
    public void setReservePiece(GamePiece gp) {
        this.sidebar.setReserveElement(gp);
    }

    /**
     * Removes a life from display
     */
    public void loseLife() {
        this.sidebar.getLives().loseLife();
    }

    /**
     * Sets the list of local scores to be displayed
     * @param localScores the scores
     */
    public void setLocalScores(VBox localScores) {
        this.localScores = localScores;
        this.localScores.setAlignment(Pos.CENTER);
        this.localScores.getStyleClass().add("scoreboard");
    }

    public void setHighScore(Pair<String, Integer> highScore) {
        this.highScore = highScore;
    }

    /**
     * Updates the user's current score
     * @param score the user's score
     */
    public void updateScore(int score) {
        this.sidebar.updateScore(score);
    }

    /**
     * Updates the user's multiplier
     * @param multiplier the new multiplier
     */
    public void updateMultiplier(int multiplier) {
        this.sidebar.updateMultiplier(multiplier);
    }

    /**
     * Adds the listener for whenever a tile is clicked
     * @param tcl the listener
     */
    public void addTileClickListener(String name, TileClickListener tcl) {
        this.listeners.put(name, tcl);
    }

    //Getters//

    public ProgressBar getTimer() {
        return this.timer;
    }

    public Grid getBoard() {
        return this.grid;
    }

    public int getGridWidth() {
        return this.width;
    }

    public int getGridHeight() {
        return this.height;
    }
}
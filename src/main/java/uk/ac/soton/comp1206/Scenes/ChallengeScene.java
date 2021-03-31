package uk.ac.soton.comp1206.Scenes;

import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Sidebar;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Components.Game.Tile.TileClickListener;
import uk.ac.soton.comp1206.Event.KeyBinding;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scene in which the game is displayed
 * @author tcs1g20
 */
public class ChallengeScene extends BaseScene {
    //The game grid for user's to play on
    protected Grid grid;

    //The timer
    protected ProgressBar timer;

    //The label showing the high score
    protected Label highScoreLbl;

    //The high score
    protected Pair<String, Integer> highScore;

    //The sidebar with all the game properties
    protected Sidebar sidebar;

    //Any required listeners for the grids on display
    protected HashMap<String,TileClickListener> listeners = new HashMap<>();

    //The grids dimension
    protected int width = 5;
    protected int height = 5;

    /**
     * Creates a new challenge scene to play a game
     * @param window the window it's on
     */
    public ChallengeScene(GameWindow window) {
        super(window);
    }

    @Override
    public void build() {
        logger.info("Creating game scene");
        this.getStylesheets().add(Utility.getStyle("Game.css"));
        this.root.getStyleClass().add("game-shell");

        this.window.setSize(880, 700);

        //Top of Screen//

        var title = new Label("CHALLENGE MODE!");
        title.getStyleClass().add("banner");
        var empty = new Region();
        HBox.setHgrow(empty, Priority.ALWAYS);

        
        var highScoreWord = new Label("HIGH SCORE");
        highScoreWord.getStyleClass().add("banner");

        this.highScoreLbl = new Label(
            this.highScore.getKey() + "\n" + this.highScore.getValue()
        );

        highScoreLbl.getStyleClass().add("banner");
        highScoreLbl.setStyle("-fx-font-size: 20;");

        var highScore = new HBox(highScoreWord, highScoreLbl);

        var banner = new HBox(title, empty, highScore);
        this.root.setTop(banner);

        //Center of screen//
        //Game grid//
        this.grid = new Grid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));        
        this.grid.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                KeyBinding.ROTATE_RIGHT.execute();
            } else if (event.getButton() == MouseButton.MIDDLE) {
                KeyBinding.SWAP.execute();
            }
        });

        this.buildTimer();
        this.buildSidebar();

        var centerComponents = new VBox(this.grid, this.timer);
        centerComponents.setAlignment(Pos.CENTER);
        centerComponents.setSpacing(25);

        this.root.setCenter(centerComponents);

        this.root.setRight(this.sidebar);
    }

    /**
     * Builds the game timer
     */
    private void buildTimer() {
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
    }

    /**
     * Builds the sidebar
     */
    private void buildSidebar() {
        this.sidebar = new Sidebar();

        //Listeners
        this.sidebar.addTileClickListener("next-piece", this.listeners.get("next-piece"));
        this.sidebar.addTileClickListener("reserve-piece", this.listeners.get("reserve-piece"));
        this.sidebar.build();
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
     * Sets the high score from the scoreboards
     * @param highScore The name and highscore
     */
    public void setHighScore(Pair<String, Integer> highScore) {
        this.highScore = highScore;
    }

    /**
     * Updates the user's current score
     * @param score the user's score
     */
    public void updateScore(int score) {
        this.sidebar.updateScore(score);

        //If the user has a new high score
        
        if (score > this.highScore.getValue()) {
            this.highScoreLbl.setText(
                "USER\n" + score 
            );
            
            this.highScoreLbl.setTextFill(Color.ORANGE);
        }
    }

    /**
     * Updates the user's level
     * @param level the new level
     */
    public void updateLevel(int level) {
        this.sidebar.updateLevel(level);
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

    /**
     * @return the timer
     */
    public ProgressBar getTimer() {
        return this.timer;
    }

    /**
     * @return the game grid
     */
    public Grid getBoard() {
        return this.grid;
    }

    /**
     * @return the width of the grid in squares
     */
    public int getGridWidth() {
        return this.width;
    }

    /**
     * @return the height of the grid in squares
     */
    public int getGridHeight() {
        return this.height;
    }
}

package uk.ac.soton.comp1206.Scenes;

import java.util.HashMap;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Sidebar;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Event.TileClickListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;

public class GameScene extends BaseScene {
    private Grid grid;

    private Sidebar sidebar;
    private ProgressBar timer;

    private HashMap<String,TileClickListener> listeners = new HashMap<>();

    private int width = 5;
    private int height = 5;

    public GameScene(GameWindow window) {
        super(window);
    }

    @Override
    public void build() {
        logger.info("Creating game scene");
        this.getStylesheets().add(Utility.getStyle("Game.css"));
        this.root.getStyleClass().add("game-shell");

        //Center of screen//
        //Game grid//
        this.grid = new Grid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));        

        this.timer = new ProgressBar();
        this.timer.setMinWidth(GridSize.LARGE.getSideLength()*this.width);
        this.timer.setMinHeight(GridSize.LARGE.getSideLength()/4);
        this.timer.getStyleClass().add("timer");

        var center = new VBox(this.grid, this.timer);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(25);

        this.root.setCenter(center);

        //Sidebar//
        this.sidebar = new Sidebar();
        this.sidebar.addTileClickListener("next-piece", this.listeners.get("next-piece"));
        this.sidebar.addTileClickListener("reserve-piece", this.listeners.get("reserve-piece"));
        this.sidebar.build();

        this.root.setRight(this.sidebar);


    }

    public void setNextPiece(GamePiece gp) {
        this.sidebar.setNextElement(gp);
    }

    public void setReservePiece(GamePiece gp) {
        this.sidebar.setReserveElement(gp);
    }

    public void updateScore(int score) {
        this.sidebar.updateScore(score);
    }

    public void loseLife() {
        this.sidebar.getLives().loseLife();
    }

    /**
     * Adds the listener for whenever a tile is clicked
     * @param tcl the listener
     */
    public void addTileClickListener(String name, TileClickListener tcl) {
        this.listeners.put(name, tcl);
    }

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

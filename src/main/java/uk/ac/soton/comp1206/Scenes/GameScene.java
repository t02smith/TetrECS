package uk.ac.soton.comp1206.Scenes;

import java.util.HashMap;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.Components.Game.Board;
import uk.ac.soton.comp1206.Components.Game.Sidebar;
import uk.ac.soton.comp1206.Components.Game.Board.BoardSize;
import uk.ac.soton.comp1206.Event.TileClickListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;

public class GameScene extends BaseScene {
    private Board grid;
    private BorderPane root;

    private Sidebar sidebar;

    private HashMap<String,TileClickListener> listeners = new HashMap<>();

    private int width = 5;
    private int height = 5;

    public GameScene(GameWindow window) {
        super(window);
        this.root = (BorderPane)this.getRoot();
    }

    public void build() {
        logger.info("Creating game scene");
        this.getStylesheets().add(Utility.getStyle("Game.css"));
        this.root.getStyleClass().add("game-shell");

        //Game grid//
        this.grid = new Board(this.width, this.height, BoardSize.LARGE, this.listeners.get("game-grid"));        

        this.root.setCenter(this.grid);

        //Sidebar//
        this.sidebar = new Sidebar();
        this.sidebar.addTileClickListener("next-piece", this.listeners.get("next-piece"));
        this.sidebar.addTileClickListener("reserve-piece", this.listeners.get("reserve-piece"));
        this.sidebar.build();

        this.root.setRight(this.sidebar);

        //Key events//
        this.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logger.info("Returning to menu");
                this.window.loadMenu();
            }
        });
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

    /**
     * Adds the listener for whenever a tile is clicked
     * @param tcl the listener
     */
    public void addTileClickListener(String name, TileClickListener tcl) {
        this.listeners.put(name, tcl);
    }

    public Board getBoard() {
        return this.grid;
    }

    public int getGridWidth() {
        return this.width;
    }

    public int getGridHeight() {
        return this.height;
    }
}

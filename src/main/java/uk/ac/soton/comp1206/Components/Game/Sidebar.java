package uk.ac.soton.comp1206.Components.Game;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Event.TileClickListener;
import uk.ac.soton.comp1206.game.GamePiece;

public class Sidebar extends StackPane {
    private static final Logger logger = LogManager.getLogger(Sidebar.class);

    private VBox components = new VBox();

    private Lives lives;
    private Label score = new Label("Score: 0");
    private Label multiplier = new Label("x 1");

    private Grid nextPiece;
    private Grid reservePiece;

    private HashMap<String,TileClickListener> listeners = new HashMap<>();

    public void build() {
        logger.info("Building sidebar");
        this.components.setAlignment(Pos.CENTER);
        this.components.setSpacing(12);

        this.components.getStyleClass().add("sidebar");

        this.setMaxWidth(300);
        this.setMinWidth(300);

        //Title

        //lives//
        this.lives = new Lives(3);

        //Score//
        this.score.setTextAlignment(TextAlignment.CENTER);
        this.score.getStyleClass().addAll("sidebar-text");

        //multiplier//
        this.multiplier.getStyleClass().addAll("sidebar-text");

        var props = new VBox(this.lives, this.score, this.multiplier);
        props.getStyleClass().add("props");
        props.setAlignment(Pos.TOP_CENTER);

        //Next piece//
        this.nextPiece = new Grid(3, 3, GridSize.MEDIUM, this.listeners.get("next-piece"));

        //Reserve piece
        this.reservePiece = new Grid(3, 3, GridSize.SMALL, this.listeners.get("reserve-piece"));

        this.components.getChildren().addAll(props, this.nextPiece, this.reservePiece);
        this.components.setSpacing(24);

        this.getChildren().add(this.components);
    }

    public void setNextElement(GamePiece gp) {
        this.displayPiece(this.nextPiece, gp);
    }

    public void setReserveElement(GamePiece gp) {
        this.displayPiece(this.reservePiece, gp);
    }

    /**
     * Displays a game piece on the board
     * @param board The board being added to
     * @param gp The game piece being added
     */
    private void displayPiece(Grid board, GamePiece gp) {
        if (gp == null) return;

        var blocks = gp.getBlocks();
        var colour = gp.getColour();

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                board.changeTile(
                    (blocks[row][column] == 1) ? colour: Colour.TRANSPARENT
                    ,row ,column);
            }
        }
    }

    /**
     * Updates the score when it changes
     * @param score the new score
     */
    public void updateScore(int score) {
        this.score.setText("Score " + score);
    }

    /**
     * Updates the multipler displayed on screen
     * @param multiplier the new multiplier
     */
    public void updateMultiplier(int multiplier) {
        this.multiplier.setText("x " + multiplier);
    }

    public void addTileClickListener(String name, TileClickListener tcl) {
        this.listeners.put(name, tcl);
    }

    public Lives getLives() {
        return this.lives;
    }
}

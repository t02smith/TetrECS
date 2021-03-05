package uk.ac.soton.comp1206.Components.Game;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Board.BoardSize;
import uk.ac.soton.comp1206.Event.TileClickListener;
import uk.ac.soton.comp1206.game.GamePiece;

public class Sidebar extends StackPane {
    private static final Logger logger = LogManager.getLogger(Sidebar.class);

    private VBox components = new VBox();

    private Label score = new Label("Score: 0");

    private Board nextPiece;
    private Board reservePiece;

    private HashMap<String,TileClickListener> listeners = new HashMap<>();

    public void build() {
        this.components.setAlignment(Pos.CENTER);
        this.components.setSpacing(12);
        this.components.setPadding(new Insets(0, 30, 0, 15));

        //Title
        this.score.getStyleClass().addAll("score");

        //Next piece//
        this.nextPiece = new Board(3, 3, BoardSize.MEDIUM, this.listeners.get("next-piece"));

        //Reserve piece
        this.reservePiece = new Board(3, 3, BoardSize.SMALL, this.listeners.get("reserve-piece"));

        this.components.getChildren().addAll(this.score, this.nextPiece, this.reservePiece);
        this.getChildren().add(this.components);
    }

    public void setNextElement(GamePiece gp) {
        this.displayPiece(this.nextPiece, gp);
    }

    public void setReserveElement(GamePiece gp) {
        this.displayPiece(this.reservePiece, gp);
    }

    private void displayPiece(Board board, GamePiece gp) {
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

    public void updateScore(int score) {
        this.score.setText("Score: " + score);
    }

    public void addTileClickListener(String name, TileClickListener tcl) {
        this.listeners.put(name, tcl);
    }
}

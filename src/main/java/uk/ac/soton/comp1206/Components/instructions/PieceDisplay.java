package uk.ac.soton.comp1206.Components.instructions;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Used to display all the different game pieces at once
 */
public class PieceDisplay extends VBox {
    private final GamePiece[] pieces;

    public PieceDisplay() {
        this.pieces = GamePiece.values();
        this.build();
    }

    public void build() {
        for (int i = 0; i < this.pieces.length; i += 3) {
            this.getChildren().add(
                this.createRow(i, i+3)
            );
        }

        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);
    }

    /**
     * Creates a row of pieces from a given sub array
     * @param lowerBound The first index to get a piece from
     * @param upperBound The index to stop adding pieces at
     * @return The row of piece grids
     */
    private HBox createRow(int lowerBound, int upperBound) {
        var row = this.newRow();

        Grid grid;
        for (int i = lowerBound; i < upperBound; i++) {
            grid = new Grid(3, 3, GridSize.MEDIUM);

            grid.lockSelected();
            grid.placePiece(this.pieces[i], 1, 1);

            row.getChildren().add(grid);
        }

        return row;
    }

    private HBox newRow() {
        var row = new HBox();

        row.setSpacing(7);
        row.setAlignment(Pos.CENTER);

        return row;
    }
}

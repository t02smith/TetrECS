package uk.ac.soton.comp1206.Components.multiplayer;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Each user's grid will appear on the screen
 *  so that a user can see what other players are 
 *  doing in real time
 */
public class User extends BorderPane {
    //That user's grid
    private Grid userGrid;

    //Their name
    private Label name;

    //Their current score
    private Label score;

    //Their lives will be shown as a red background that fades down

    public User(String name) {
        this.getStyleClass().add("opponent-grid");

        this.name = new Label(name);
        this.name.getStyleClass().add("opponent-name");

        this.score = new Label("0");
        this.score.getStyleClass().add("opponent-score");

        this.userGrid = new Grid(5, 5, GridSize.SMALL);
        this.userGrid.lockSelected();

        this.setCenter(this.userGrid);

        var props = new VBox(this.name, this.score);

        this.setBottom(props);
    }

    /**
     * Updates the grid with the user's new grid
     * @param newGrid the new grid
     */
    public void applyGridChanges(int[][] newGrid) {
        //Empties the grid of all tiles
        this.userGrid.clearAll();

        //Loops through each square and adds colour where necessary
        for (int y = 0; y < newGrid.length; y++) {
            for (int x = 0; x < newGrid[0].length; x++) {
                if (newGrid[y][x] == 0) continue;

                var colour = GamePiece.getByValue(newGrid[y][x] - 1).getColour();

                this.userGrid.changeTile(colour, x, y);
            }
        }
    }

    public void setScore(int score) {
        this.score.setText(String.valueOf(score));
    }

    public void setLives(int lives) {
        switch(lives) {
            case 3:
                break;
            case 2:
                break;
            case 1:
                break;
            case 0:
                break;
        }
    }

    public void setName(String name) {
        this.name.setText(name);
    }
    
}

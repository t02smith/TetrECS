package uk.ac.soton.comp1206.game.Multiplayer;

import java.util.ArrayList;

import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Represents each user in a given channel
 * Once a channel is left this will be discarded
 */
public class User { 
    private String name;
    private int score = 0;
    private int lives = 3;

    private int[][] grid = new int[5][5];
    private Grid userGrid;

    private ArrayList<String> messages = new ArrayList<String>();

    public User(String name) {
        this.name = name;
        this.userGrid = new Grid(5, 5, GridSize.SMALL);
        this.userGrid.lockSelected();
    }

    public void setUserGrid(int[][] grid) {
        this.grid = grid;
        this.applyGridChanges();
    }

    /**
     * Applies any changes to the grid received
     */
    private void applyGridChanges() {
        this.userGrid.clearAll();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == 0) return;

                var colour = GamePiece.getByValue(grid[y][x] - 1).getColour();
                this.userGrid.changeTile(colour, x, y);
            }
        }
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public ArrayList<String> getMessages() {
        return this.messages;
    }

    public Grid getGrid() {
        return this.userGrid;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setName(String name) {
        this.name = name;
    }
}

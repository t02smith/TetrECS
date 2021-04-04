package uk.ac.soton.comp1206.Components.Game.Powerups;

import java.util.ArrayList;

import uk.ac.soton.comp1206.Components.Game.Colour;
import uk.ac.soton.comp1206.Components.Game.Grid;
import uk.ac.soton.comp1206.Components.Game.Tile.TileClickListener;

/**
 * Child class of Grid
 *  Will have special abilties to match powerups that
 *  would never be used in the normal grid
 * 
 * @author tcs1g20
 */
public class PowerUpGrid extends Grid {
    
    public PowerUpGrid(int width, int height, GridSize sideLength, TileClickListener tcl) {
        super(width, height, sideLength, tcl);
    }

    public PowerUpGrid(int width, int height, GridSize sideLength) {
        super(width, height, sideLength);
    }

    //PUSHING BLOCKS

    /**
     * Using an enum for directions means we have a simpler interface
     * Whilst it only effects a small amount of code it is easier to understand
     */

    public enum Direction{
        LEFT, RIGHT, UP, DOWN;
    }

    /**
     * Pushes all blocks on the grid a given direction
     * @param direction The direction to push the blocks
     */
    public void push(Direction direction) {
        switch(direction) {
            default: break;
            case LEFT: 
                this.pushHorizontal(true); break;
            case RIGHT:
                this.pushHorizontal(false); break;
            case UP:
                this.pushVertical(true); break;
            case DOWN:
                this.pushVertical(false); break;
        }
    }

    /**
     * Pushes all blocks horizontally in a direction
     * @param left if the blocks are being pushed left; if false they are pushed right
     */
    private void pushHorizontal(boolean left) {
        for (int row = 0; row < this.height; row++) {
            var newRow = new ArrayList<Colour>();
            for (int column = 0; column < this.width; column++) {
                if (this.tiles[row][column].getColour() != Colour.TRANSPARENT) {
                    newRow.add(this.tiles[row][column].getColour());
                }

            }

            int fillTransparent = this.width-newRow.size();

            for (int i = 0; i < fillTransparent; i++) {
                if (left) newRow.add(Colour.TRANSPARENT);
                else newRow.add(0, Colour.TRANSPARENT);
            }


            for (int i = 0; i < this.width; i++) {
                if (newRow.get(i) == Colour.TRANSPARENT) this.tiles[row][i].clearNoAnimation();
                else this.tiles[row][i].setTile(newRow.get(i));
            }
        }
    }

    /**
     * Pushes all the blocks on the grid along the vertical axis
     *  essentially the same as above function :/
     * @param up whether it is up or down
     */
    private void pushVertical(boolean up) {
        for (int column = 0; column < this.width; column++) {
            var newColumn = new ArrayList<Colour>();
            for (int row = 0; row < this.height; row++) {
                if (this.tiles[row][column].getColour() != Colour.TRANSPARENT) {
                    newColumn.add(this.tiles[row][column].getColour());
                }
            }

            int fillTransparent = this.height-newColumn.size();

            for (int i = 0; i < fillTransparent; i++) {
                if (up) newColumn.add(Colour.TRANSPARENT);
                else newColumn.add(0, Colour.TRANSPARENT);
            }

            for (int i = 0; i < this.height; i++) {
                if (newColumn.get(i) == Colour.TRANSPARENT) this.tiles[i][column].clearNoAnimation();
                else this.tiles[i][column].setTile(newColumn.get(i));
            }
        }
    }

       

}

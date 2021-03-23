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
import uk.ac.soton.comp1206.Components.Game.Tile.TileClickListener;
import uk.ac.soton.comp1206.Components.misc.ToggleSwitch;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The section on screen to display game info:
 *  -> next and reserve piece
 *  -> score
 *  -> lives left
 *  -> multiplier
 */
public class Sidebar extends StackPane {
    private static final Logger logger = LogManager.getLogger(Sidebar.class);

    //Components inside the sidebar
    private VBox components = new VBox();

    //deprecated -> used to toggle the selected tile from being on
    private ToggleSwitch toggle;

    //PROPERTIES//

    //Lives left
    private Lives lives;

    //Current score
    private Label score = new Label("Score 0");

    //Current multiplier
    private Label multiplier = new Label("x 1");

    //PIECES//

    //The next piece to be played
    private Grid nextPiece;

    //The reserve piece that can be swapped to
    private Grid reservePiece;

    //Any listeners that are needed in this class for the grids
    private HashMap<String,TileClickListener> listeners = new HashMap<>();

    public void build() {
        logger.info("Building sidebar");
        this.components.setAlignment(Pos.CENTER);
        this.components.setSpacing(12);

        this.components.getStyleClass().add("sidebar");

        this.setMaxWidth(300);
        this.setMinWidth(300);

        //Title

        //toggle// -> deprecated
        //this.toggle = new ToggleSwitch(true, ToggleSize.MEDIUM, Utility.getImage("ECS.png"));

        //lives//
        this.lives = new Lives(3);

        //Score//
        this.score.setTextAlignment(TextAlignment.CENTER);
        this.score.getStyleClass().addAll("sidebar-text");
        
        //multiplier//
        this.multiplier.getStyleClass().addAll("sidebar-text");

        //Collects all the properties together
        var props = new VBox(this.lives, this.score, this.multiplier);
        props.getStyleClass().add("props");
        props.setAlignment(Pos.TOP_CENTER);

        //Next piece//
        this.nextPiece = new Grid(3, 3, GridSize.MEDIUM, this.listeners.get("next-piece"));
        this.nextPiece.lockSelected(1, 1);

        //Reserve piece
        this.reservePiece = new Grid(3, 3, GridSize.SMALL, this.listeners.get("reserve-piece"));
        this.reservePiece.lockSelected();

        this.components.getChildren().addAll(props, this.nextPiece, this.reservePiece);
        this.components.setSpacing(24);

        this.getChildren().add(this.components);
    }

    /**
     * Displays the next piece to be played
     * @param gp the next piece
     */
    public void setNextElement(GamePiece gp) {
        this.displayPiece(this.nextPiece, gp);
    }

    /**
     * Displays the piece in reserve
     * @param gp the reserve piece
     */
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

        //Position of tiles filled in
        int[][] blocks = gp.getBlocks();

        //The colour of those tiles
        Colour colour = gp.getColour();

        //Looks through the 3x3 grids and fills them in
        //Separate to grid's implementation as it will just overwrite any squares
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

    /**
     * Adds a tile click listener that can be accessed by name
     * @param name The name of the listener e.g. "next-piece" for the next piece grid
     * @param tcl The listener
     */
    public void addTileClickListener(String name, TileClickListener tcl) {
        this.listeners.put(name, tcl);
    }

    /**
     * Gets the lives component
     * @return the lives component
     */
    public Lives getLives() {
        return this.lives;
    }

    /**
     * Gets the toggle switch component
     * @return the toggle switch component
     */
    @Deprecated
    public ToggleSwitch getToggle() {
        return this.toggle;
    }
}

package uk.ac.soton.comp1206.Scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.instructions.KeyBindingDisplay;
import uk.ac.soton.comp1206.Components.instructions.PieceDisplay;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The instruction scene will tell the users how to play
 *  the game. Including gamepieces they can play and the
 *  key bindings
 */
public class InstructionScene extends BaseScene {
    //Piece display

    //Key bindings + changeable
    
    public InstructionScene(GameWindow gw) {
        super(gw);
        this.build();
    }

    public void build() {
        this.getStylesheets().add(Utility.getStyle("Instructions.css"));
        this.root.getStyleClass().add("instruction-bg");

        this.window.setSize(1075, 700);

        var pieces = new PieceDisplay();
        pieces.setPadding(new Insets(20, 20, 20, 20));


        this.root.setLeft(
            pieces
        );

        var instructions = new ImageView(Utility.getImage("instructions.png"));
        instructions.setPreserveRatio(true);
        instructions.setFitWidth(this.window.getWidth()*0.85);

        var keyBindings = new KeyBindingDisplay();

        var body = new VBox(instructions, keyBindings);
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(20, 20, 20, 20));
        body.setSpacing(25);

        this.root.setCenter(body);

        //Key bindings + game rules
    }


}

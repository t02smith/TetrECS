package uk.ac.soton.comp1206.Components.instructions;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import uk.ac.soton.comp1206.Event.KeyBinding;

public class KeyBindingDisplay extends StackPane {
    private final KeyBinding[] actions = KeyBinding.values();

    public void build() {
        var scroll = new ScrollPane();

        var actionGrid = new GridPane();

        for (int i = 0; i < actions.length; i++) {
            var action = actions[i];

            
        }
    }


}

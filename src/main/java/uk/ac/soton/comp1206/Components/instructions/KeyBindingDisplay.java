package uk.ac.soton.comp1206.Components.instructions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import uk.ac.soton.comp1206.Event.KeyBinding;

/**
 * Displays all the key bindings with their associated actions
 *  Allows key bindings to be changed at runtime
 * @author tcs1g20
 */
public class KeyBindingDisplay extends ScrollPane {
    //The set of actions that can be done by the user
    private final KeyBinding[] actions = KeyBinding.values();

    //The key binding the user is currently changing -> null if not being changed
    private KeyIcon changing;

    public KeyBindingDisplay() {
        this.build();
    }

    /**
     * Builds the component
     */
    public void build() {
        this.getStyleClass().add("key-binding-display");

        //actionGrid is the grid that stores all the info neatly
        var actionGrid = new GridPane();
        actionGrid.setHgap(10);
        actionGrid.setVgap(10);

        //Loops through every action
        for (int i = 0; i < actions.length; i++) {
            KeyBinding action = actions[i];

            //Each action will have its own row
            var keySet = new HBox();
            keySet.setSpacing(10);
            keySet.setAlignment(Pos.CENTER);
            keySet.setPadding(new Insets(10, 10, 10, 10));

            //Loops through the action's key bindings
            action.getBindings().forEach(key -> {
                var icon = new KeyIcon(key, action);

                //You can change a binding by clicking on it
                icon.setOnMouseClicked(event -> {
                    this.changing = icon;
                    icon.hideValue();                    
                });

                //Add each icon to be displayed next to each other
                keySet.getChildren().add(icon);
            });
        
            //Adds the bindings to the grid
            actionGrid.add(keySet, 0, i);

            //Name of the action
            var name = new Label(action.toString().replace("_", " "));
            name.getStyleClass().add("action-name");
            actionGrid.add(name, 1, i);

            //What the action does in game
            var description = new Label(action.getDescription());
            description.setMaxWidth(200);
            description.setWrapText(true);
            description.getStyleClass().add("action-description");
            actionGrid.add(description, 2, i);
            
        }

        //If the user is changing a binding listen for what key they press next
        this.setOnKeyReleased(event -> {
            if (this.changing != null) {
                this.changing.setKey(event.getCode());
                this.changing = null;
            }
        });

        this.setContent(actionGrid);
    }

}

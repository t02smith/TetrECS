package uk.ac.soton.comp1206.Components.instructions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import uk.ac.soton.comp1206.Event.KeyBinding;
import uk.ac.soton.comp1206.Utility.Utility;

public class KeyBindingDisplay extends ScrollPane {
    private final KeyBinding[] actions = KeyBinding.values();

    private KeyIcon changing;

    public KeyBindingDisplay() {
        this.build();
    }

    public void build() {
        this.getStyleClass().add("key-binding-display");

        var actionGrid = new GridPane();
        actionGrid.setHgap(10);
        actionGrid.setVgap(10);

        Utility.getImage("key.png");
        for (int i = 0; i < actions.length; i++) {
            KeyBinding action = actions[i];

            var keySet = new HBox();
            keySet.setSpacing(10);
            keySet.setAlignment(Pos.CENTER);
            keySet.setPadding(new Insets(10, 10, 10, 10));
            action.getBindings().forEach(key -> {
                var icon = new KeyIcon(key);
                icon.setOnMouseClicked(event -> {
                    this.changing = icon;
                    icon.hideValue();                    
                });

                keySet.getChildren().add(
                    icon
                );
            });

            this.setOnKeyReleased(event -> {
                if (this.changing != null) {
                    this.changing.setKey(event.getCode());
                    this.changing = null;
                }
            });
        
            actionGrid.add(keySet, 0, i);

            var name = new Label(action.toString().replace("_", " "));
            name.getStyleClass().add("action-name");
            actionGrid.add(name, 1, i);

            var description = new Label(action.getDescription());
            description.setMaxWidth(200);
            description.setWrapText(true);
            description.getStyleClass().add("action-description");
            actionGrid.add(description, 2, i);
            
        }


        this.setContent(actionGrid);
    }


}

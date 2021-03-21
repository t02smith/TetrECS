package uk.ac.soton.comp1206.Scenes;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Components.multiplayer.ChatPane;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Components.multiplayer.MultiplayerGrid;
import uk.ac.soton.comp1206.Event.SendMessageListener;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerScene extends ChallengeScene {
    private ChatPane chatpane;
    private SendMessageListener sml;

    public MultiplayerScene(GameWindow window, SendMessageListener sml) {
        super(window);
        this.sml = sml;
    }

    @Override
    public void build() {
        super.build();

        this.window.setSize(1150, 700);

        this.grid = new MultiplayerGrid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));

        var center = new VBox(this.grid, this.timer);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(25);

        this.root.setCenter(center);


        this.chatpane = new ChatPane(this.sml);
        this.chatpane.setMaxWidth(this.getWidth()*0.4);
        this.chatpane.setMinWidth(this.getWidth()*0.4);

        this.root.setLeft(this.chatpane);

        //TODO add a thing on the bottom that shows other players grids
    }

    public void addMessage(Message msg) {
        Platform.runLater(() -> {
            this.chatpane.addMessage(msg);
        });
    }
}

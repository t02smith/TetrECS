package uk.ac.soton.comp1206.Scenes;

import java.util.Collection;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Scoreboard;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Components.multiplayer.ChatPane;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Components.multiplayer.MultiplayerGrid;
import uk.ac.soton.comp1206.Components.multiplayer.OnlinePanel;
import uk.ac.soton.comp1206.Components.multiplayer.User;
import uk.ac.soton.comp1206.Event.SendMessageListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Multiplayer scene shown during a multiplayer match
 *  Identical to challenge scene with some additions
 */
public class MultiplayerScene extends ChallengeScene {
    private ChatPane chatpane;
    private SendMessageListener sml;

    private OnlinePanel onlinePanel;

    private VBox users;
    private Collection<User> userList;

    public MultiplayerScene(GameWindow window, SendMessageListener sml) {
        super(window);
        this.sml = sml;
    }

    @Override
    public void build() {
        super.build();
        this.getStylesheets().add(Utility.getStyle("Multiplayer.css"));

        this.window.setSize(1100, 700);

        this.grid = new MultiplayerGrid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));

        var gridComponents = new VBox(this.grid, this.timer);
        gridComponents.setAlignment(Pos.CENTER);
        gridComponents.setSpacing(25);

        this.chatpane = new ChatPane(this.sml);
        //this.chatpane.setMaxWidth(this.getWidth()*0.25);
        this.chatpane.setMinWidth(this.getWidth()*0.25);
        this.chatpane.setOpacity(0);

        this.users = new VBox(10);
        var userScroll = new ScrollPane(this.users);
        userScroll.setFitToWidth(true);
        userScroll.setMaxWidth(this.getWidth()*0.25);
        userScroll.setMinWidth(this.getWidth()*0.25);

        this.users.setAlignment(Pos.TOP_CENTER);
        this.users.setPadding(new Insets(12, 12, 12, 20));
        this.userList.forEach(user -> this.users.getChildren().add(user));

        var scoreboard = new Scoreboard("Local Scores", Utility.readFromFile("/scores/localScores.txt"));

        this.onlinePanel = new OnlinePanel(this.chatpane, this.users, scoreboard);

        this.root.setCenter(gridComponents);
        this.root.setLeft(this.onlinePanel);

    }

    public void addMessage(Message msg) {
        Platform.runLater(() -> {
            this.chatpane.addMessage(msg);
        });
    }

    public void setUserList(Collection<User> userList) {
        this.userList = userList;
    }

    public void toggleOnlinePanel() {
        this.onlinePanel.toggleOnlinePanel();
    }


}

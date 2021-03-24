package uk.ac.soton.comp1206.Scenes;

import java.util.Collection;
import java.util.Iterator;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Components.multiplayer.ChatPane;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Components.multiplayer.MultiplayerGrid;
import uk.ac.soton.comp1206.Components.multiplayer.User;
import uk.ac.soton.comp1206.Event.SendMessageListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerScene extends ChallengeScene {
    private ChatPane chatpane;
    private SendMessageListener sml;

    private StackPane onlinePanel;

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
        this.chatpane.setMaxWidth(this.getWidth()*0.25);
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

        this.onlinePanel = new StackPane(this.chatpane, this.users);

        var center = new HBox(this.onlinePanel, gridComponents);
        this.root.setCenter(center);

        /* If you want a 2xn grid of users
        HBox userRow = null;
        Iterator<User> userIterator = this.userList.iterator();

        for (int i = 0; i < this.userList.size(); i++) {
            if (i%3 == 0) {
                userRow = new HBox(10);
                this.users.getChildren().add(userRow);
            }

            userRow.getChildren().add(userIterator.next());

        }*/

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
        var top = this.onlinePanel.getChildren().get(
            this.onlinePanel.getChildren().size()-1
        );

        top.setOpacity(0);

        this.onlinePanel.getChildren().remove(top);
        this.onlinePanel.getChildren().add(0, top);

        var newTop = this.onlinePanel.getChildren().get(
            this.onlinePanel.getChildren().size()-1
        );

        newTop.setOpacity(1);
        newTop.requestFocus();
    }
}

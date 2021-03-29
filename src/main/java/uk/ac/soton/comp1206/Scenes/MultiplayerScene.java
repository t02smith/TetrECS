package uk.ac.soton.comp1206.Scenes;

import java.util.Collection;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import uk.ac.soton.comp1206.Components.Game.Scoreboard;
import uk.ac.soton.comp1206.Components.Game.Grid.GridSize;
import uk.ac.soton.comp1206.Components.multiplayer.ChatPane;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Components.multiplayer.MultiplayerGrid;
import uk.ac.soton.comp1206.Components.multiplayer.OnlinePanel;
import uk.ac.soton.comp1206.Components.multiplayer.User;
import uk.ac.soton.comp1206.Components.multiplayer.TextToolbar.SubmitListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Multiplayer scene shown during a multiplayer match
 *  Identical to challenge scene with some additions
 */
public class MultiplayerScene extends ChallengeScene {
    //Message list
    private ChatPane chatpane;
    private SubmitListener sml;

    //List of users
    private VBox users;
    private Collection<User> userList;

    //Scoreboard
    private Scoreboard scoreboard;

    //Side panel for online content
    private OnlinePanel onlinePanel;

    public MultiplayerScene(GameWindow window, SubmitListener sml) {
        super(window);
        this.sml = sml;
    }

    @Override
    public void build() {
        super.build();
        this.getStylesheets().add(Utility.getStyle("Multiplayer.css"));

        this.window.setSize(1100, 700);

        //Uses the multiplayer grid which compiles a message to send after every placement
        this.grid = new MultiplayerGrid(this.width, this.height, GridSize.LARGE, this.listeners.get("game-grid"));

        var gridComponents = new VBox(this.grid, this.timer);
        gridComponents.setAlignment(Pos.CENTER);
        gridComponents.setSpacing(25);

        //ONLINE PANEL//

        //Chat window
        this.chatpane = new ChatPane(this.sml);
        //this.chatpane.setMaxWidth(this.getWidth()*0.25);
        this.chatpane.setMinWidth(this.getWidth()*0.25);
        this.chatpane.setOpacity(0);

        //Other user's grid
        this.users = new VBox(10);
        var userScroll = new ScrollPane(this.users);
        userScroll.setFitToWidth(true);
        userScroll.setMinWidth(this.getWidth()*0.25);

        this.users.setAlignment(Pos.TOP_CENTER);
        this.users.setPadding(new Insets(12, 12, 12, 20));
        this.userList.forEach(user -> this.users.getChildren().add(user));

        //Creates an array of all the users that are being put on the scoreboard
        //During runtime the SCORE message will be passed directly to the scoreboard class
        var initialUsers = new HashMap<String, Integer>();
        this.userList.forEach(user -> {
            initialUsers.put(user.getName(), 0);
        });

        this.scoreboard = new Scoreboard("Leaderboard", initialUsers);
        this.scoreboard.setMinWidth(this.getWidth()*0.35);

        this.onlinePanel = new OnlinePanel(this.chatpane, scoreboard, userScroll);
        this.onlinePanel.setMaxWidth(this.getWidth()*0.35);

        this.root.setCenter(gridComponents);
        this.root.setLeft(this.onlinePanel);

    }

    /**
     * Adds a message to the chat window
     * @param msg The message to be added
     */
    public void addMessage(Message msg) {
        Platform.runLater(() -> {
            this.chatpane.addMessage(msg);
        });
    }

    /**
     * Sets the list of users in a game to be displayed
     * @param userList List of users
     */
    public void setUserList(Collection<User> userList) {
        this.userList = userList;
    }

    /**
     * Toggles between panes in the online panel
     */
    public void toggleOnlinePanel() {
        this.onlinePanel.toggleOnlinePanel();
    }

    /**
     * Updates a user's score on the scoreboard
     * @param name The user's name 
     * @param score The user's new score
     */
    public void updateUserScore(String name, int score) {
        this.scoreboard.updateScore(name, score);
    }


}

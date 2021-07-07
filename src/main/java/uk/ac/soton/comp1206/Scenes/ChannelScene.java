package uk.ac.soton.comp1206.Scenes;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.misc.ExpandingTextField;
import uk.ac.soton.comp1206.Components.multiplayer.ChatPane;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Components.multiplayer.TextToolbar.SubmitListener;
import uk.ac.soton.comp1206.Event.GameStartListener;
import uk.ac.soton.comp1206.Event.Action;
import uk.ac.soton.comp1206.Event.ActionTag;
import uk.ac.soton.comp1206.Event.OnClickListener;
import uk.ac.soton.comp1206.Utility.MultiMedia;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.Multiplayer.Channel;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scene for when you are inside a channel
 * Will show users, chat, nickname etc.
 * @author tcs1g20
 */
public class ChannelScene extends BaseScene {
    //The channel the user is in
    private Channel channel;

    //Message window for channel
    private ChatPane chatPane;

    //Component to change your nickname
    private ExpandingTextField nickname;

    //The list of users in the channel
    private GridPane users;

    //Called when the host clicks the start button
    private GameStartListener startListener;

    /**
     * Creates a new Channel scene when in a channel
     * @param gw The window the scene is on
     * @param channel The channel that we have joined
     */
    public ChannelScene(GameWindow gw, Channel channel) {
        super(gw, ActionTag.TYPING);
        this.channel = channel;
    }

    @Override
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Channel.css"));
        this.root.getStyleClass().add("channel-shell");

        //CHATPANE//

        this.chatPane = new ChatPane();
        this.chatPane.setMaxWidth(this.getWidth()*0.4);
        this.chatPane.setMinWidth(this.getWidth()*0.4);

        //Chatpane stretches with the window
        this.widthProperty().addListener(event -> {
            this.chatPane.setMaxWidth(this.getWidth()*0.4);
            this.chatPane.setMinWidth(this.getWidth()*0.4);
        });

        this.root.setLeft(this.chatPane);

        //CHANNEL INFO//

        var channelInfo = new VBox(25);
        channelInfo.setAlignment(Pos.TOP_CENTER);

        var channelName = new Label(channel.getName());
        channelName.getStyleClass().add("channel-name");

        this.nickname = new ExpandingTextField("Nickname");
        this.nickname.setPromptText("Set nickname");

        //USER LIST//
        this.users = new GridPane();

        this.users.setAlignment(Pos.CENTER);
        this.users.getStyleClass().add("user-list");
        this.users.setHgap(25);
        this.users.setVgap(5);

        channelInfo.getChildren().addAll(channelName, nickname, this.users);

        this.root.setCenter(channelInfo);
    }

    @Override
    public void setKeyBindings() {
        this.setOnKeyReleased(event -> {
            Action.executeEvent(event.getCode());
        });
    }

    /**
     * Displays a given message on screen
     * @param message the message to be displayed
     */
    public void addMessage(Message message) {
        Platform.runLater(() -> {
            this.chatPane.addMessage(message);
        });
    }

    /**
     * Updates the list of users show in the menu
     * @param channel The channel the user is in
     */
    public void updateUserList(Channel channel) {
        this.users.getChildren().clear();

        String[] userList = channel.getUsernames();

        for (int i = 0 ; i < userList.length; i++) {
            var userLbl = new Label(userList[i]);
            userLbl.getStyleClass().add("channel-user");

            this.users.add(userLbl, i%2, i/2);
        }
    }

    /**
     * Displays the components unique to the host
     */
    public void showHostComponents() {
        var start = new Button("START");
        start.getStyleClass().add("start-multiplayer");
        start.setOnAction(event -> this.startListener.start());

        ((VBox)this.root.getCenter()).getChildren().add(start);
    }

    /**
     * Changes the user's nickname that's on display
     * @param nickname the new nickname
     */
    public void setNickname(String nickname) {
        this.nickname.setTitle(nickname);
    }

    /**
     * Changes the user's nickname by sending a message to the server
     * @param listener changes the nickname
     */
    public void setChangeNicknameListener(OnClickListener listener) {
        this.nickname.setOnClickListener(listener);
    }

    /**
     * Called when the user sends a message
     * @param listener
     */
    public void setSendMessageListener(SubmitListener listener) {
        this.chatPane.setSendMessageListener(listener);
    }

    /**
     * sets the listener for when the host starts a game
     * @param listener the start game listener
     */
    public void setGameStartListener(GameStartListener listener) {
        this.startListener = listener;
    }

    @Override
    public void playBackgroundMusic() {
        MultiMedia.playMusic("menu.mp3");
    }
}

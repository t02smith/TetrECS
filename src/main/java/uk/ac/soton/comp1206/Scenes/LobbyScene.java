package uk.ac.soton.comp1206.Scenes;

import java.util.Collection;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.misc.ExpandingTextField;
import uk.ac.soton.comp1206.Components.multiplayer.ChatPane;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Event.GameStartListener;
import uk.ac.soton.comp1206.Event.NetworkListener;
import uk.ac.soton.comp1206.Event.OnClickListener;
import uk.ac.soton.comp1206.Event.SendMessageListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.Multiplayer.Channel;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LobbyScene extends BaseScene {
    private ChatPane chatpane;
    private GridPane users = new GridPane();
    private ExpandingTextField nickname;

    private VBox channelList;
    private OnClickListener createChannel;
    private OnClickListener changeNickname;

    private GameStartListener startListener;
    
    private SimpleBooleanProperty inChannel = new SimpleBooleanProperty();

    public LobbyScene(GameWindow gw) {
        super(gw);
    }

    @Override
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Lobby.css"));

        //Initially the user will be in the lobby
        this.buildInLobby();

        this.inChannel.addListener(event -> {
            if (!this.inChannel.get()) {
                this.root.setLeft(null);
            }
        });

    }

    /**
     * Builds the UI for when a user is in a channel
     */
    public void buildInLobby() {
        this.inChannel.set(false);
        this.root.getStyleClass().add("lobby-shell");

        //Channel List

        //Holds the title and the list of available channels
        var channelShell = new VBox();

        //The list of channels that can change
        this.channelList = new VBox();

        var title = new Label("Channels");
        title.getStyleClass().add("channel-title");
        channelShell.getChildren().addAll(title, this.channelList);

        this.root.setCenter(channelShell);

        //Chat window

        var createServer = new ExpandingTextField("create server", this.createChannel);
        createServer.setPromptText("Enter server name");
        this.root.setBottom(createServer);
    }

    /**
     * Builds the UI for when a user is in a channel
     */
    public void buildInChannel(Channel channel, SendMessageListener sml) {
        this.inChannel.set(true);
        this.chatpane = new ChatPane(sml);
        this.chatpane.setMaxWidth(this.getWidth()*0.4);
        this.chatpane.setMinWidth(this.getWidth()*0.4);

        this.widthProperty().addListener(event -> {
            this.chatpane.setMaxWidth(this.getWidth()*0.4);
            this.chatpane.setMinWidth(this.getWidth()*0.4);
        });

        this.root.setCenter(null);

        this.root.setLeft(this.chatpane);
        this.root.setBottom(null);

        var channelInfo = new VBox(25);
        channelInfo.setAlignment(Pos.TOP_CENTER);

        var channelName = new Label(channel.getName());
        channelName.getStyleClass().add("channel-name");

        this.nickname = new ExpandingTextField("Nickname", this.changeNickname);
        this.nickname.setPromptText("Set nickname");

        this.users.setAlignment(Pos.CENTER);
        this.users.getStyleClass().add("user-list");
        this.users.setHgap(25);
        this.users.setVgap(5);

        channelInfo.getChildren().addAll(channelName, nickname, this.users);

        this.root.setCenter(channelInfo);

    }

    /**
     * Updates the visible list of channels
     * @param channels The list of available channels
     * @param joinChannel Join a selected channel
     */
    public void updateChannels(Collection<Channel> channels, NetworkListener joinChannel) {
        //this.channelList = new VBox();

        this.channelList.getChildren().clear();

        if (channels.size() == 0) { //If there are no open channels
            var noChannels = new Label("No channels are currently active :(");
            noChannels.getStyleClass().add("no-channels");

            this.channelList.getChildren().add(noChannels);
        } else {    //If there is at least one open channel
            //Create a button for each channel
            channels.forEach(channel -> {
                var name = new Button(channel.getName());
                name.getStyleClass().addAll("channel-name");

                name.setOnAction(event -> {
                    joinChannel.receive(channel.getName());
                });
                
                this.channelList.getChildren().add(name);
            });
        }
    }

    /**
     * Displays a given message on screen
     * @param message the message to be displayed
     */
    public void addMessage(Message message) {
        Platform.runLater(() -> {
            this.chatpane.addMessage(message);
        });
    }

    /**
     * Updates the list of users show in the menu
     * @param channel The channel the user is in
     */
    public void updateUserList(Channel channel) {
        this.users.getChildren().clear();

        String[] userList = channel.getUsers();

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

    public void setNickname(String nickname) {
        this.nickname.setTitle(nickname);
    }

    public void setChangeNicknameListener(OnClickListener listener) {
        this.changeNickname = listener;
    }

    /**
     * Sets the listener for when a user creates a channel
     * @param listener
     */
    public void setCreateChannelListener(OnClickListener listener) {
        this.createChannel = listener;
    }

    public void setGameStartListener(GameStartListener listener) {
        this.startListener = listener;
    }

    public SimpleBooleanProperty inChannelProperty() {
        return this.inChannel;
    }

    public boolean getInChannel() {
        return this.inChannel.get();
    }

}

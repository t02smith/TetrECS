package uk.ac.soton.comp1206.Scenes;

import java.util.Collection;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.misc.ExpandingTextField;
import uk.ac.soton.comp1206.Event.OnClickListener;
import uk.ac.soton.comp1206.Network.Communicator.NetworkListener;
import uk.ac.soton.comp1206.Utility.MultiMedia;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.Multiplayer.Channel;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Scene for the multiplayer lobby where a user can join/create a server
 * @author tcs1g20
 */
public class LobbyScene extends BaseScene {
    //The list of channels
    private VBox channelList;

    //Called when the user creates a channel
    private OnClickListener createChannel;

    /**
     * Creates a new Lobby scene
     * @param gw The window to place the lobby scene on
     */
    public LobbyScene(GameWindow gw) {
        super(gw);
    }

    @Override
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Lobby.css"));

        //Initially the user will be in the lobby
        this.buildInLobby();

    }

    /**
     * Builds the UI for when a user is in a channel
     */
    public void buildInLobby() {
        this.root.getStyleClass().add("lobby-shell");

        //Channel List

        //Holds the title and the list of available channels
        var channelShell = new VBox(12);
        channelShell.setPadding(new Insets(12));

        //The list of channels that can change
        this.channelList = new VBox();

        var title = new Label("Channels");
        title.getStyleClass().add("channel-title");
        channelShell.getChildren().addAll(title, this.channelList);

        this.root.setCenter(channelShell);

        //Chat window

        var createServer = new ExpandingTextField("create server");
        createServer.setOnClickListener(this.createChannel);
        createServer.setPromptText("Enter server name");
        this.root.setBottom(createServer);
    }


    /**
     * Updates the visible list of channels
     * @param channels The list of available channels
     * @param joinChannel Join a selected channel
     */
    public void updateChannels(Collection<Channel> channels, NetworkListener joinChannel) {
        this.channelList.getChildren().clear();

        if (channels.size() == 0) { //If there are no open channels
            var noChannels = new Label("No channels are currently active :(");
            noChannels.getStyleClass().add("no-channels");

            this.channelList.getChildren().add(noChannels);
        } else {//If there is at least one open channel
            //Create a button for each channel
            channels.forEach(channel -> {
                var name = new Button(channel.getName());
                name.getStyleClass().addAll("channel-title");

                name.setOnAction(event -> {
                    joinChannel.receive(channel.getName());
                });
                
                this.channelList.getChildren().add(name);
            });
        }
    }

    /**
     * Sets the listener for when a user creates a channel
     * @param listener
     */
    public void setCreateChannelListener(OnClickListener listener) {
        this.createChannel = listener;
    }

    @Override
    public void playBackgroundMusic() {
        MultiMedia.playMusic("menu.mp3");
    }

}

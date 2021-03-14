package uk.ac.soton.comp1206.Scenes;

import java.util.Collection;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.multiplayer.ChatPane;
import uk.ac.soton.comp1206.Event.NetworkListener;
import uk.ac.soton.comp1206.Utility.Utility;
import uk.ac.soton.comp1206.game.Multiplayer.Channel;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LobbyScene extends BaseScene {
    private ChatPane chatpane;

    private VBox channelList;

    public LobbyScene(GameWindow gw) {
        super(gw);
    }
    
    public void build() {
        this.getStylesheets().add(Utility.getStyle("Lobby.css"));
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

        this.chatpane = new ChatPane(message -> {
            logger.error(message);
        });
        this.chatpane.build();

        this.root.setLeft(this.chatpane);

    }

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

}

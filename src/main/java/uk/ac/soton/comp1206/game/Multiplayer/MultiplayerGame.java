package uk.ac.soton.comp1206.game.Multiplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Network.NetworkProtocol;
import uk.ac.soton.comp1206.Scenes.LobbyScene;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerGame extends Game {
    private HashMap<String, Channel> channels = new HashMap<>();
    private Channel currentChannel;

    private LobbyScene lobby;

    public MultiplayerGame(GameWindow gw, Communicator communicator) {
        super(gw, communicator);

        logger.info("Starting multiplayer");
    }

    @Override
    public void buildGame() {
        this.lobby = new LobbyScene(this.gameWindow);
        this.gameWindow.setLobbyScene(this.lobby);

        this.setupCommunicator();

    }

    @Override
    protected void setupCommunicator() {
        //List of channels
        NetworkProtocol.LIST.addListener(message -> this.fillServerList(message));

        //Join a channel
        NetworkProtocol.JOIN.addListener(message -> {
            var name = message.split("\\s+")[1];
            this.currentChannel = this.channels.get(name);
            logger.info("Channel changed to {}", name);
        });

        //Set a nickname
        NetworkProtocol.CHANGE_NICK.addListener(message -> {
            String[] msg = message.split("\\s+")[1].split(":");
            this.currentChannel.updateNickname(msg[0], msg[1]);
        });

        NetworkProtocol.HOST.addListener(message -> logger.info("You are the host"));

        this.communicator.send("LIST");
        //Updates the channel list at regular intervals
        var getChannelList = new Timeline(
            new KeyFrame(Duration.millis(5000), e -> {
                logger.info("Updating channel list");
                this.communicator.send("LIST");
            })
        );

        getChannelList.setCycleCount(Animation.INDEFINITE);
        getChannelList.play();
    }

    /**
     * Creates an arraylist of servers
     * @param servers the server list received from the communicator
     */
    private void fillServerList(String servers) {
        var channels = new ArrayList<String>(Arrays.asList(servers.split("\\s+")));

        //Removes 'CHANNELS'
        channels.remove(0);

        //Adds any new channels to the list
        channels.forEach(channel -> {
            if (!this.channels.containsKey(channel)) {
                var newChannel = new Channel(channel);
                this.channels.put(channel, newChannel);
            }
        });

        //Updates the visible list of servers
        Platform.runLater(
            () -> this.lobby.updateChannels(this.channels.values(), channelName -> {
                this.communicator.send("JOIN " + channelName);
            })
        );

        logger.info("Channel list updated");
    }
}

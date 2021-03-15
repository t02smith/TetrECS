package uk.ac.soton.comp1206.game.Multiplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Network.NetworkProtocol;
import uk.ac.soton.comp1206.Scenes.LobbyScene;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerGame extends Game {
    private HashMap<String, Channel> channels = new HashMap<>();
    private Channel currentChannel;

    private String name;

    private Timeline updateChannelList;

    private LobbyScene lobby;

    public MultiplayerGame(GameWindow gw, Communicator communicator) {
        super(gw, communicator);

        logger.info("Starting multiplayer");
    }

    @Override
    public void buildGame() {
        this.lobby = new LobbyScene(this.gameWindow);
        this.lobby.setCreateChannelListener(channelName -> {
            this.communicator.send("CREATE " + channelName);
        });

        //When you leave a channel
        this.lobby.inChannelProperty().addListener(event -> {
            if (!this.lobby.getInChannel()) {
                this.leaveChannel();
            }
        });


        this.gameWindow.setLobbyScene(this.lobby);

        this.setupCommunicator();

    }

    /**
     * Sets up any network protocols that will be used
     */
    @Override
    protected void setupCommunicator() {
        //List of channels
        NetworkProtocol.LIST.addListener(message -> this.fillServerList(message));

        //Join a channel
        NetworkProtocol.JOIN.addListener(message -> {
            var name = message.split("\\s+")[1];
            if (this.currentChannel == null) {
                if (!this.channels.containsKey(name)) {
                    var channel = new Channel(name);
                    this.channels.put(name, channel);
                }

                this.currentChannel = this.channels.get(name);
                this.displayChannel();

            } else logger.error("You are already in a channel");
        });

        //Updates the list of users in the channel
        NetworkProtocol.USERS.addListener(message -> {
            this.currentChannel.updateUsers(message);
            Platform.runLater(() -> this.lobby.updateUserList(this.currentChannel));
        });

        NetworkProtocol.MSG.addListener(message -> {  
            String[] msg = message.substring(4).split(":");
            this.lobby.addMessage(
                new Message(msg[0], msg[1], msg[0].equals(this.name))
            );
        });

        //Sets nickname
        NetworkProtocol.NICK.addListener(message -> {
            this.name = message.split("\\s+")[1];
        });

        //Set a nickname
        NetworkProtocol.CHANGE_NICK.addListener(message -> {
            String[] msg = message.split("\\s+")[1].split(":");
            this.currentChannel.updateNickname(msg[0], msg[1]);
        });

        NetworkProtocol.HOST.addListener(message -> logger.info("You are the host"));

        //Leave the current channel
        NetworkProtocol.PART.addListener(message -> {
            this.currentChannel = null;
        });


        this.communicator.send("LIST");
        //Updates the channel list at regular intervals
        this.updateChannelList = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                logger.info("Updating channel list");
                this.communicator.send("LIST");
            }),
            new KeyFrame(Duration.millis(5000))
        );

        this.updateChannelList.setCycleCount(Animation.INDEFINITE);
        this.updateChannelList.play();
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

    /**
     * Called when joining a channel
     * Displays the chat window and list of users
     */
    private void displayChannel() {
        this.updateChannelList.stop();
        Platform.runLater(() -> {
            this.lobby.buildInChannel(this.currentChannel, message -> {
                this.communicator.send("MSG " + message);

            });
        });

    }

    /**
     * Called when a user leaves a channel
     */
    private void leaveChannel() {
        this.communicator.send("PART");
        this.currentChannel = null;
        this.updateChannelList.playFromStart();

        //some ui updates
    }

}

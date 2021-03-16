package uk.ac.soton.comp1206.game.Multiplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Components.multiplayer.Message;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Network.NetworkProtocol;
import uk.ac.soton.comp1206.Scenes.LobbyScene;
import uk.ac.soton.comp1206.Scenes.MultiplayerScene;
import uk.ac.soton.comp1206.Utility.Media;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerGame extends Game {
    private HashMap<String, Channel> channels = new HashMap<>();
    private Channel currentChannel;

    private String name;
    private boolean host = false;
    private boolean inGame = false;

    private Timeline updateChannelList;

    private LobbyScene lobby;

    public MultiplayerGame(GameWindow gw, Communicator communicator) {
        super(gw, communicator);

        logger.info("Starting multiplayer");
    }

    @Override
    public void buildGame() {
        this.gameScene = new MultiplayerScene(this.gameWindow, message -> this.communicator.send("MSG " + message));
        this.gameWindow.setGameScene(this.gameScene);

        //Lobby//
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

        this.lobby.setOnKeyReleased(event -> {
            switch(event.getCode()) {
                default:
                    break;
                case ESCAPE:
                if (this.lobby.getInChannel()) {
                    this.lobby.buildInLobby();
                    this.leaveChannel();

                } else this.gameWindow.loadMenu();
            }
        });

        //Game setup//

        this.gameWindow.addGameStartListener(() -> {
            this.communicator.send("PIECE");
            this.communicator.send("PIECE");

            this.gameOver = false;

            Media.playMusic("game.wav");

            this.gameLoop();
        });


        this.setKeyBindings();
        this.setTileClickListeners();
        this.setUserPropertyListeners();
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
            var msgComponent = new Message(msg[0], msg[1], msg[0].equals(this.name));
            if (this.inGame) ((MultiplayerScene)this.gameScene).addMessage(msgComponent);
            else this.lobby.addMessage(msgComponent);
        });

        //Sets nickname
        NetworkProtocol.NICK.addListener(message -> {
            this.name = message.split("\\s+")[1];
            Platform.runLater(() -> this.lobby.setNickname(this.name));
        });

        this.lobby.setChangeNicknameListener(nickname -> {
            this.communicator.send("NICK " + nickname);
            this.communicator.send("MSG **" + this.name + "** has changed their name to **" + nickname + "**");
        });

        //Set a nickname
        NetworkProtocol.CHANGE_NICK.addListener(message -> {
            String[] msg = message.split("\\s+")[1].split(":");
            this.currentChannel.updateNickname(msg[0], msg[1]);

        });

        NetworkProtocol.HOST.addListener(message -> {
            logger.info("You are the host");
            this.host = true;
            Platform.runLater(() -> this.lobby.showHostComponents());
        });

        //Called when the host starts the game
        this.lobby.setGameStartListener(() -> this.communicator.send("START"));

        NetworkProtocol.START.addListener(message -> {
            Platform.runLater(() -> this.gameWindow.loadMultiplayer());
            this.inGame = true;
        });

        //Leave the current channel
        NetworkProtocol.PART.addListener(message -> {
            this.currentChannel = null;
        });

        //Get next piece
        NetworkProtocol.PIECE.addListener(message -> {
            this.nextPiece();
            int value = Integer.parseInt(message.split("\\s+")[1]);
            GamePiece reserve = GamePiece.getByValue(value);

            logger.info("Adding piece {}", reserve);

            this.reservePiece = reserve;
            this.gameScene.setReservePiece(this.reservePiece);
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


    @Override
    protected void setUserPropertyListeners() {
        super.setUserPropertyListeners();

        this.lives.addListener(event -> {
            if (this.lives.get() == -1) this.communicator.send("DIE");
            else this.communicator.send("LIVES " + this.lives.get());  
        });
    }

    @Override
    public void resetGame() {
        super.resetGame();
        this.leaveChannel();
    }

    @Override
    public void insertPiece(int x, int y) {
        if (this.placePiece(x, y)) {
            this.afterPiece();

            this.communicator.send("PIECE");

            this.timeline.playFromStart();
        }
    }

    @Override
    protected void fillTiles(ArrayList<int[]> buffer) {
        super.fillTiles(buffer);

        //TODO Send the board thing
    }

    @Override
    protected void nextPiece() {
        this.currentPiece = this.reservePiece;
        this.gameScene.setNextPiece(this.currentPiece);
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

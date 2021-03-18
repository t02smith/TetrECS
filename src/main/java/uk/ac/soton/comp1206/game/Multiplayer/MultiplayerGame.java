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
import uk.ac.soton.comp1206.Event.KeyBinding;
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

    //Stores the history of the board to send off to the server
    private ArrayList<int[][]> boardHistory = new ArrayList<>();

    //Will always aim to contain 5 pieces
    private ArrayList<GamePiece> pieces = new ArrayList<>();

    private Timeline updateChannelList;

    private LobbyScene lobby;

    public MultiplayerGame(GameWindow gw, Communicator communicator) {
        super(gw, communicator);

        logger.info("Starting multiplayer");
    }

    @Override
    public void buildGame() {
        this.challengeScene = new MultiplayerScene(this.gameWindow, message -> this.communicator.send("MSG " + message));
        this.gameWindow.setGameScene(this.challengeScene);

        this.challengeScene.setHighScore(this.gameWindow.getScoresScene().getHighScore());

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
            this.updateChannelList.stop();
            //empty board to start off with
            this.boardHistory.add(new int[5][5]);

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

    @Override
    protected void setKeyBindings() {
        super.setKeyBindings();

        KeyBinding.CHAT.setEvent(() -> {
            KeyBinding.setKeysDisabled(true);
        });

        this.communicator.addListener(message -> {
            if (message.startsWith("MSG " + this.name)) {
                KeyBinding.setKeysDisabled(false);
            }
        });
    }

    /**
     * Sets up any network protocols that will be used
     * @override Adds all the multiplayer protocols as well
     */
    @Override
    protected void setupCommunicator() {
        super.setupCommunicator();

        //List of channels
        NetworkProtocol.LIST.addListener(message -> {
            this.fillServerList(message);
            logger.error("yes");
        });

        //Join a channel
        NetworkProtocol.JOIN.addListener(message -> {
            logger.error("Hello");
            var name = message.split("\\s+")[1];
            if (this.currentChannel == null) {

                if (!this.channels.containsKey(name)) {
                    var channel = new Channel(name);
                    this.channels.put(name, channel);
                }

                this.currentChannel = this.channels.get(name);
                logger.info("test");
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
            if (this.inGame) ((MultiplayerScene)this.challengeScene).addMessage(msgComponent);
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
            this.challengeScene.setReservePiece(this.reservePiece);
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
     * Sets the listeners for the user's properties
     * @override Sends a DIE message when the user is out of live
     *           send a message saying how many lives they have
     */
    @Override
    protected void setUserPropertyListeners() {
        super.setUserPropertyListeners();

        //Sends a die message when the user runs out of lives
        this.lives.addListener(event -> {
            if (this.lives.get() == -1) this.communicator.send("DIE");
            else this.communicator.send("LIVES " + this.lives.get());  
        });
    }

    /**
     * Resets the game
     * @override Leaves the channel once reset
     */
    @Override
    public void resetGame() {
        super.resetGame();

        //Leaves channel when game is reset
        this.leaveChannel();
    }

    @Override
    public void insertPiece(int x, int y) {
        if (this.placePiece(x, y)) {
            this.afterPiece();

            //Requests next piece
            this.communicator.send("PIECE");

            this.timeline.playFromStart();
        }
    }

    /**
     * Fills the tiles on the board
     * @override Adds the tiles to the board array to submit to the server
     */
    @Override
    protected void fillTiles(ArrayList<int[]> buffer) {
        super.fillTiles(buffer);

        int playedPiece = this.currentPiece.getValue()+1;
        int[][] next = this.boardHistory.get(this.boardHistory.size()-1).clone();

        buffer.forEach(pos -> {
            next[pos[1]][pos[0]] = playedPiece;
        });

        this.boardHistory.add(next);
    }

    /**
     * Called after a piece is played
     * Mostly the same as Game
     * @override Creates a numerical version of the board to send off to the server
     */
    @Override
    protected void afterPiece() {
        var board = this.challengeScene.getBoard();

        //Buffers for which rows/columns are full
        //rows and columns share tiles so we can't remove any tiles before checking both
        var rowBuffer = new ArrayList<Integer>();
        var columnBuffer = new ArrayList<Integer>();

        for (int i = 0; i < this.challengeScene.getGridHeight(); i++) {
            if (board.checkRow(i)) rowBuffer.add(i);
            if (board.checkColumn(i)) columnBuffer.add(i);
        }

        //Gets the current board for transmission
        int[][] currentBoard = this.boardHistory.get(this.boardHistory.size()-1).clone();

        //Clears the rows and columns that are full
        rowBuffer.forEach(row -> {
            board.clearRow(row);
            currentBoard[row] = new int[this.challengeScene.getGridWidth()]; //removes any deleted rows
        });

        columnBuffer.forEach(column -> {
            board.clearColumn(column);
            for (int i = 0; i < this.challengeScene.getGridHeight(); i++) { //removes deleted columns
                currentBoard[i][column] = 0;
            }
        });

        //Sets next board
        this.boardHistory.set(this.boardHistory.size()-1, currentBoard);

        //Builds the BOARD command
        StringBuilder send = new StringBuilder("BOARD");
        for (int y = 0; y < currentBoard.length; y++) {
            for (int x = 0; x < currentBoard[0].length; x++) {
                send.append(" " + currentBoard[x][y]);
            }
        }

        this.communicator.send(send.toString());

        //Values needed for the score
        int linesCleared = rowBuffer.size()+columnBuffer.size();
        int blocks = columnBuffer.size()*board.getGridHeight() + rowBuffer.size() * (board.getGridWidth() - columnBuffer.size());

        //Update the score
        this.score(linesCleared, blocks); 

        //Updates the multiplier
        if (columnBuffer.size() == 0 && rowBuffer.size() == 0) this.multiplier.set(1);
        else this.multiplier.set(this.multiplier.get() + 1);

        logger.info("Multiplier set to {}", this.multiplier.get());

    }

    /**
     * Gets the next piece
     * @override The next piece is retrieved through a server message
     *           This will only move the reserve piece to the main slot
     */
    @Override
    protected void nextPiece() {
        this.currentPiece = this.reservePiece;
        this.challengeScene.setNextPiece(this.currentPiece);
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

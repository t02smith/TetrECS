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
import uk.ac.soton.comp1206.Components.multiplayer.MultiplayerGrid;
import uk.ac.soton.comp1206.Event.KeyBinding;
import uk.ac.soton.comp1206.Network.Communicator;
import uk.ac.soton.comp1206.Network.NetworkProtocol;
import uk.ac.soton.comp1206.Scenes.ChannelScene;
import uk.ac.soton.comp1206.Scenes.LobbyScene;
import uk.ac.soton.comp1206.Scenes.MultiplayerScene;
import uk.ac.soton.comp1206.Utility.MultiMedia;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The multiplayer version of the game
 * @author tcs1g20
 */
public class MultiplayerGame extends Game {
    //List of all channels currently available
    private HashMap<String, Channel> channels = new HashMap<>();

    //The channel the user is currently in
    private Channel currentChannel;

    //The users name
    private String name;

    //Whether the user is in a game
    private boolean inGame = false;

    //The history of the user's grid -> time travel
    private ArrayList<int[][]> gridHistory;

    //queue of pieces - Will always aim to contain 5 pieces
    private ArrayList<GamePiece> pieces = new ArrayList<>();

    //What to do when the channel list needs updating
    private Timeline updateChannelList;

    //The lobby the user goes into to join/create channels
    private LobbyScene lobby;

    //The channel lobby before the game starts
    private ChannelScene channelScene;

    /**
     * Creates a new multiplayer game
     * @param gw the window the game will be on
     * @param communicator the communicator connected to a server
     */
    public MultiplayerGame(GameWindow gw, Communicator communicator) {
        super(gw, communicator);

        logger.info("Starting multiplayer");
    }

    @Override
    public void buildGame() {
        logger.info("Building multiplayer game");
        this.challengeScene = new MultiplayerScene(
            this.gameWindow, 
            message -> this.communicator.send("MSG " + message)
        );

        this.gameWindow.setGameScene(this.challengeScene);

        this.challengeScene.setHighScore(this.gameWindow.getScoresScene().getHighScore());

        //Lobby//
        this.lobby = new LobbyScene(this.gameWindow);
        this.lobby.setCreateChannelListener(channelName -> {
            this.communicator.send("CREATE " + channelName);
        });

        //Game setup//

        this.setGameStartListener();

        this.setKeyBindings();
        this.setTileClickListeners();
        this.setUserPropertyListeners();
        this.gameWindow.setLobbyScene(this.lobby);
        this.setupCommunicator();

    }

    @Override
    protected void setKeyBindings() {
        super.setKeyBindings();

        KeyBinding.TOGGLE_PANEL.setEvent(() -> {
            ((MultiplayerScene)this.challengeScene).toggleOnlinePanel();
        });

    }

    @Override
    protected void setGameStartListener() {
        this.gameWindow.addGameStartListener(() -> {
            logger.info("Multiplayer game starting");

            this.resetGame();
            this.gridHistory = ((MultiplayerGrid)this.challengeScene.getBoard()).getGridHistory();

            this.updateChannelList.stop();
            //empty board to start off with
            this.gridHistory.add(new int[5][5]);

            for (int i = 0; i < 5; i++) 
                this.communicator.send("PIECE");

            this.gameOver = false;

            MultiMedia.playMusic("game.wav");
            KeyBinding.setKeysDisabled(false);

            this.gameLoop();
        });
    }

    /**
     * Sets up any network protocols that will be used
     * @override Adds all the multiplayer protocols as well
     */
    @Override
    protected void setupCommunicator() {
        super.setupCommunicator();

        //LOBBY//

        //List of channels
        NetworkProtocol.LIST.addListener(message -> {
            this.fillServerList(message);
        });


        //Updates the channel list at regular intervals
        this.updateChannelList = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                logger.info("Updating channel list");
                this.communicator.send("LIST");
            }),
            new KeyFrame(Duration.millis(3000))
        );

        this.updateChannelList.setCycleCount(Animation.INDEFINITE);
        this.updateChannelList.play();

        //Join a channel
        NetworkProtocol.JOIN.addListener(message -> {
            var name = message.split("\\s+")[1];
            if (this.currentChannel == null) { //if the user is not already in a channel

                //If the new channel is in our list of available channels
                if (!this.channels.containsKey(name)) {
                    var channel = new Channel(name);
                    this.channels.put(name, channel);
                }

                logger.info("Joining channel {}", name);

                this.currentChannel = this.channels.get(name);
                this.displayChannel();

                //Change this keybinding to leave the channel
                KeyBinding.ESCAPE.setEvent(() -> {
                    logger.info("Leaving channel");
                    this.leaveChannel();
                    if (this.inGame) this.stopGame();
        
                    this.gameWindow.revertScene();
                    KeyBinding.setKeysDisabled(true);
                });

            } else logger.error("You are already in a channel");
        });

        //IN CHANNEL//

        //Updates the list of users in the channel
        NetworkProtocol.USERS.addListener(message -> {
            this.currentChannel.updateUsers(message);
            Platform.runLater(() -> this.channelScene.updateUserList(this.currentChannel));
        });

        //If the user is the host
        NetworkProtocol.HOST.addListener(message -> {
            logger.info("You are the host");
            Platform.runLater(() -> this.channelScene.showHostComponents());
        });

        
        //For when the game starts
        NetworkProtocol.START.addListener(message -> {
            Platform.runLater(() -> this.gameWindow.loadGame());
            this.inGame = true;
        });

        //Leave the current channel
        NetworkProtocol.PART.addListener(message -> {
            this.leaveChannel();
        });


        //SOCIAL//

        //When receiving a message
        NetworkProtocol.MSG.addListener(message -> {  
            String[] msg = message.substring(4).split(":");
            var msgComponent = new Message(msg[0], msg[1], msg[0].equals(this.name));
            if (this.inGame) ((MultiplayerScene)this.challengeScene).addMessage(msgComponent);
            else this.channelScene.addMessage(msgComponent);
        });

        //Sets initial nickname from server
        NetworkProtocol.NICK.addListener(message -> {
            this.name = message.split("\\s+")[1];
        });

        //Change a nickname
        NetworkProtocol.CHANGE_NICK.addListener(message -> {
            String[] msg = message.split("\\s+")[1].split(":");
            try {this.currentChannel.updateNickname(msg[0], msg[1]);}
            catch (NullPointerException e) {
                //If the game hasn't started this will throw an error :(
            }

        });

        //IN GAME//

        //Get next piece
        NetworkProtocol.PIECE.addListener(message -> {
            int value = Integer.parseInt(message.split("\\s+")[1]);
            
            GamePiece next = GamePiece.getByValue(value);
            this.pieces.add(next);
            logger.info("Adding piece {}", next);

            if (this.currentPiece == null) this.nextPiece();
        });

        //When receiving details about a user's game board
        NetworkProtocol.BOARD.addListener(message -> {
            message = message.substring(6); //Removes BOARD
            String name = message.split(":")[0];
            String[] tiles = message.split(":")[1].split("\\s+");

            logger.info("Updating {}'s board ", name);

            int[][] grid = new int[5][5];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    var nextTile = tiles[i*5 + j];

                    grid[i][j] = Integer.parseInt(nextTile);
                }
            }

            this.currentChannel.updateUserGrid(name, grid);
        });

        //Updates a user's properties when received from server
        NetworkProtocol.SCORES.addListener(message -> {
            message = message.substring(7);
            logger.info("Updating user properties");

            String[] users = message.split("\\s+");
            for (String user: users) {
                String[] userProps= user.split(":");

                this.currentChannel.updateUserProperties(
                    userProps[0], 
                    Integer.parseInt(userProps[1]), 
                    userProps[2].equals("DEAD") ? 0: Integer.parseInt(userProps[2])
                );
            }
        });

        //Update a user's score
        NetworkProtocol.SCORE.addListener(message -> {
            var nameScore = message.substring(6).split(":");
            logger.info("Updating {}'s score to {}", nameScore[0], nameScore[1]);

            ((MultiplayerScene)this.challengeScene).updateUserScore(
                nameScore[0], Integer.parseInt(nameScore[1])
            );
        });

        //Kill a user
        NetworkProtocol.DIE.addListener(message -> {
            var name = message.split("\\s+")[1];
            this.currentChannel.killUser(name);
        });

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
            if (this.lives.get() == -1) {
                this.communicator.send("DIE");
            } else this.communicator.send("LIVES " + this.lives.get());  
        });

        this.score.addListener(event -> {
            logger.info("Updating score");
            this.communicator.send("SCORE " + this.score.get());
        });
    }

    @Override
    public void stopGame() {
        logger.info("GAME OVER");
        if (this.timeline != null) this.timeline.stop();
        this.gameOver = true;
        
    }

    @Override
    public void insertPiece(int x, int y) {
        var grid = this.challengeScene.getBoard();

        if (!this.gameOver && grid.placePiece(this.currentPiece, x, y)) {
            this.afterPiece();

            MultiMedia.playSFX("SFX/place.wav");

            //Requests next piece
            this.communicator.send("PIECE");
            this.nextPiece();

            this.timeline.playFromStart();
        } else MultiMedia.playSFX("SFX/fail.wav");
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
        int[][] currentBoard = this.gridHistory.get(this.gridHistory.size()-1).clone();

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

        if (rowBuffer.size() > 0 || columnBuffer.size() > 0) MultiMedia.playSFX("SFX/explode.wav");

        //Sets next board
        this.gridHistory.set(this.gridHistory.size()-1, currentBoard);

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

        this.reservePiece = this.pieces.get(0);
        this.challengeScene.setReservePiece(this.reservePiece);
        this.pieces.remove(0);
    }

    /**
     * Creates an arraylist of servers
     * @param servers the server list received from the communicator
     */
    private void fillServerList(String servers) {
        var channelNames = new ArrayList<String>(Arrays.asList(servers.split("\\s+")));
        
        //Removes all existing channels incase they don't exist anymore
        this.channels.clear();

        //Removes 'CHANNELS'
        channelNames.remove(0);

        //Adds any new channels to the list
        channelNames.forEach(channel -> {
            var newChannel = new Channel(channel);
            this.channels.put(channel, newChannel);
        });

        //Updates the visible list of servers
        Platform.runLater(
            () -> this.lobby.updateChannels(this.channels.values(), channelName -> {
                this.communicator.send("JOIN " + channelName);
                logger.info("Joining channel {}", channelName);
            })
        );

        logger.info("Channel list updated");
    }

    /**
     * Called when joining a channel
     * Displays the chat window and list of users
     */
    private void displayChannel() {
        logger.info("Displaying channel: {}", this.currentChannel.getName());
        this.updateChannelList.stop();
        ((MultiplayerScene)this.challengeScene).setUserList(this.currentChannel.getUsers());
        
        Platform.runLater(() -> {
            this.gameWindow.loadChannel(this.currentChannel);

            this.channelScene = this.gameWindow.getChannelScene();

            this.channelScene.setSendMessageListener(message -> {
                this.communicator.send("MSG " + message);
            });
    
            //Called when the host starts the game
            this.channelScene.setGameStartListener(() -> this.communicator.send("START"));
    
            //When a nickname is changed
            this.channelScene.setChangeNicknameListener(nickname -> {
                this.communicator.send("NICK " + nickname);
            });

            this.channelScene.setNickname(this.name);

        });

    }

    /**
     * Called when a user leaves a channel
     */
    private void leaveChannel() {
        this.communicator.send("PART");
        this.currentChannel = null;
        this.updateChannelList.playFromStart();

        logger.info("Leaving channel");
    }

}

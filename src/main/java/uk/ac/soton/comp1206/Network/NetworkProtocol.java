package uk.ac.soton.comp1206.Network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Network.Communicator.NetworkListener;


/**
 * Network protocol for how can communicate with the server
 * Contains the regex format of any arguments you need as well as
 * the expected result.
 * 
 * Blank send/receive fields imply you wouldn't send/receive anything
 * @author tcs1g20
 */
public enum NetworkProtocol {
    HISCORES    ("HISCORES (\\w*:\\d+\\s*)+"),                      //List of high scores
    LIST        ("CHANNELS ((\\w+\\s*)+\\n*)*"),                    //List of open channels
    CREATE      (""),                                               //Create a channel
    JOIN        ("JOIN (\\w+\\s*)+"),                               //Join a channel by name
    QUIT        (""),                                               //Disconnect from server
    MSG         ("MSG \\w+:((\\W+)*\\w+(\\W+)*\\s*)+"),             //Send/receive a message in a channel
    NICK        ("NICK \\w+"),                                      //change of nickname -> yours or someones elses
    CHANGE_NICK ("NICK (\\w+):(\\w+)"),                             //Change another player's nickname
    START       ("START"),                                          //Request start or notification to say game is starting
    PART        ("PARTED"),                                         //Disconnect from current channel
    USERS       ("USERS (\\w+\\n?)+"),                              //Gets a list of users in channel
    HOST        ("HOST"),                                           //Received if user is the channel's host
    ERROR       ("ERROR (\\w+\\s*)+"),                              //If any network errors occur
    SCORES      ("SCORES (\\w+:\\d+(:(\\d|DEAD)?)\\s*)+"),          //Update a user's properties
    SCORE       ("SCORE \\w+:\\d+"),                                //Update a user's score
    PIECE       ("PIECE \\d+"),                                     //Get the next piece to play
    BOARD       ("BOARD \\w+:(\\d+\\s?){25}");                      //Get a user's game board



    //Regex to match the expected message from the server
    private final String receive;

    //Does this when receiving a message
    private NetworkListener action;

    protected static final Logger logger = LogManager.getLogger(NetworkProtocol.class);

    /**
     * Constructor
     * @param argument
     * @param receive
     */
    private NetworkProtocol(String receive) {
        this.receive = receive;
    }

    /**
     * Adds the listener to be performed when a message is received
     * of the corresponding format
     * @param action the method to be executed
     */
    public void addListener(NetworkListener action) {
        logger.info("Network protocol {} setup", this);
        this.action = action;
    }

    /**
     * Performs the given action on a received message
     * @param message the message being received
     */
    public void doAction(String message) {
        if (this.action != null) this.action.receive(message);
        else logger.error("No action setup for protocol {}", this);
    }

    //Getters

    /**
     * Gets the expected result format
     */
    public String getResult() {
        return this.receive;
    }



}
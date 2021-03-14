package uk.ac.soton.comp1206.Network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Event.NetworkListener;

/**
 * Network protocol for how can communicate with the server
 * Contains the regex format of any arguments you need as well as
 * the expected result.
 * 
 * Blank send/receive fields imply you wouldn't send/receive anything
 * 
 * @TODO boolean for whether you would expect a result after sending?
 */
public enum NetworkProtocol {
    HISCORES    ("HISCORES", "HISCORES (\\w+:\\d+\\s*)+"),  //List of high scores
    LIST        ("LIST", "CHANNELS ((\\w+\\s*)+\\n*)*"),    //List of open channels
    CREATE      ("CREATE (\\w+\\s*)+", ""), //Create a channel
    JOIN        ("JOIN (\\w+\\s*)+", "JOIN (\\w+\\s*)+"),   //Join a channel by name
    QUIT        ("QUIT", ""),                               //Disconnect from server
    MSG         ("MSG (\\w+\\s*)+", "MSG (\\w+\\s*)+"),     //Send/receive a message in a channel
    NICK        ("NICK \\w+", "NICK \\w+"),                 //change of nickname -> yours or someones elses
    CHANGE_NICK ("", "NICK (\\w+):(\\w+)"),
    START       ("START", "START"),                         //Request start or notification to say game is starting
    PART        ("", "PARTED"),                             //Disconnect from current channel
    USERS       ("USERS", "USERS (\\w+\\n?)+"),             //Gets a list of users in channel
    HOST        ("", "HOST"),                               //Received if user is the channel's host
    ERROR       ("", "ERROR (\\w+\\s*)+");                  //If any network errors occur


    //Any arguments passed through when sending message
    private final String send;

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
    private NetworkProtocol(String argument, String receive) {
        this.send = argument;
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
     * Gets the send format
     */
    public String getSend() {
        return this.send;
    }

    /**
     * Gets the expected result format
     */
    public String getResult() {
        return this.receive;
    }



}
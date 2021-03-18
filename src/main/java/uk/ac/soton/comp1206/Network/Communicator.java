package uk.ac.soton.comp1206.Network;

import java.util.ArrayList;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.Alert;

public class Communicator {
    private static final Logger logger = LogManager.getLogger(Communicator.class);

    private final ArrayList<NetworkListener> listeners = new ArrayList<>();

    private WebSocket ws;

    public Communicator(String server) {
        try {
            var socketFactory = new WebSocketFactory();

            this.ws = socketFactory.createSocket(server);
            this.ws.connect();
            logger.info("Connected to '{}'", server);

            this.ws.addListener(new WebSocketAdapter() {
                @Override
                //When a message is received
                public void onTextMessage(WebSocket ws, String message) throws Exception {
                    if (message.startsWith("ERROR")) logger.error(message);
                    else Communicator.this.receive(ws, message);
                }

                @Override
                //When the socket is pinged
                public void onPingFrame(WebSocket ws, WebSocketFrame wsFrame) throws Exception {
                    logger.info("Ping");
                }

                //Error handling

                @Override
                public void handleCallbackError(WebSocket ws, Throwable throwable) throws Exception {
                    logger.error("Callback error: '{}'", throwable.getMessage());
                }

                @Override
                public void onError(WebSocket ws, WebSocketException error) throws Exception {
                    logger.error("Error: '{}'", error.getMessage());
                }
            });

        } catch (Exception e) {
            logger.error("Socket error: '{}'", e.getMessage());

            var alert = new Alert(Alert.AlertType.ERROR, "Unable to communicate with the TetrECS server.");
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Sends a message to the server
     * @param message message to send
     */
    public void send(String message) {
        logger.info("Sending message: '{}'", message);
        this.ws.sendText(message);
    }

    /**
     * Called when a message is received from the server
     * @param ws the socket it came from
     * @param message the content of the message
     */
    private void receive(WebSocket ws, String message) {
        logger.info("Received: '{}'", message);

        this.listeners.forEach(listener -> {
            listener.receive(message);
        });
    }

    /**
     * Adds a given listener
     * @param listener
     */
    public void addListener(NetworkListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes all currently in use listeners
     */
    public void clearListeners() {
        this.listeners.clear();
    }

    public interface NetworkListener {
        public void receive(String message); 
    }
}

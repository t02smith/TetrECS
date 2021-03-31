package uk.ac.soton.comp1206.Components.multiplayer;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Components.multiplayer.TextToolbar.SubmitListener;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * Multiplayer component
 *  Used to send and receive messages
 * @author tcs1g20
 */
public class ChatPane extends BorderPane {
    //The window where messages are displayed
    private ScrollPane msgWindow;

    //The list of displayed messages
    private VBox msgList;

    //The toolbar with the text input and send btn
    private TextToolbar toolbar;

    //What to do when sending a message
    private SubmitListener sendMsg;

    public ChatPane() {
        this.build();
    }

    public ChatPane(SubmitListener sendMsg) {
        this.build();
        this.setSendMessageListener(sendMsg);
    }

    /**
     * Builds chat component
     */
    public void build() {
        this.getStylesheets().add(Utility.getStyle("chatpane.css"));
        this.getStyleClass().add("chat-pane");
        this.msgList = new VBox(5);

        //Messages to scroll through
        this.msgWindow = new ScrollPane(this.msgList);
        this.msgWindow.setFitToWidth(true);
        this.msgWindow.setId("msg-window");

        this.setCenter(this.msgWindow);

        this.toolbar = new TextToolbar(this.sendMsg);
        this.setBottom(this.toolbar);
    }

    /**
     * Adds a message on screen
     * @param msg The message to add
     */
    public void addMessage(Message msg) {
        this.msgList.getChildren().add(msg);
        this.msgWindow.setVvalue(0.0);
    }

    /**
     * Set the action for when sending a message
     * @param listener
     */
    public void setSendMessageListener(SubmitListener listener) {
        this.toolbar.setSubmitListener(listener);
    }

}

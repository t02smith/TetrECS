package uk.ac.soton.comp1206.Components.multiplayer;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Event.SendMessageListener;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * Multiplayer component
 *  Used to send and receive messages
 */
public class ChatPane extends BorderPane {
    //The window where messages are displayed
    private ScrollPane msgWindow;

    //The list of displayed messages
    private VBox msgList;

    //The toolbar with the text input and send btn
    private MsgToolbar toolbar;

    //What to do when sending a message
    private SendMessageListener sendMsg;

    public ChatPane(SendMessageListener sml) {
        this.sendMsg = sml;
        this.build();
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

        this.toolbar = new MsgToolbar(this.sendMsg);
        this.setBottom(this.toolbar);
    }

    public void addMessage(Message msg) {
        this.msgList.getChildren().add(msg);
        this.msgWindow.setVvalue(0.0);
    }

}

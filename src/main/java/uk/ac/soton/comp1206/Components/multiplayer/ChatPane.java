package uk.ac.soton.comp1206.Components.multiplayer;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp1206.Event.SendMessageListener;

public class ChatPane extends BorderPane {
    
    private ScrollPane msgWindow;
    private VBox msgList;

    private MsgToolbar toolbar;
    private SendMessageListener sendMsg;

    public ChatPane(SendMessageListener sml) {
        this.sendMsg = sml;
    }

    public void build() {
        this.msgList = new VBox(5);

        this.msgWindow = new ScrollPane(this.msgList);
        this.msgWindow.setFitToWidth(true);
        this.msgWindow.setId("msg-window");

        this.setCenter(this.msgWindow);

        this.toolbar = new MsgToolbar(this.sendMsg);
        this.setBottom(this.toolbar);
    }

    public void addMessage(Message msg) {
        this.msgList.getChildren().add(msg);
        this.msgWindow.setVvalue(1.0);
    }

}

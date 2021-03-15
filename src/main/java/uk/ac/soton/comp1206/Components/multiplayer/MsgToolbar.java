package uk.ac.soton.comp1206.Components.multiplayer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import uk.ac.soton.comp1206.Event.SendMessageListener;
import uk.ac.soton.comp1206.Utility.Utility;

public class MsgToolbar extends HBox {
    private TextField msg;
    private Button send;

    private SendMessageListener sml;
    
    public MsgToolbar(SendMessageListener sendMessageListener) {
        this.sml = sendMessageListener;
        this.build();
    }

    public void build() {
        this.msg = new TextField();
        this.msg.setPromptText("Enter message");
        this.msg.setMaxHeight(20);
        this.msg.getStyleClass().add("msg-input");
        this.msg.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(this.msg, Priority.ALWAYS);

        ImageView icon = new ImageView(Utility.getImage("send.png"));
        icon.setPreserveRatio(true);
        icon.setFitHeight(20);

        this.send = new Button();
        this.send.setGraphic(icon);
        this.send.getStyleClass().add("msg-send");
        this.send.setOnAction(event -> {
            this.sml.sendMsg(this.msg.getText());
            this.msg.setText("");
        });

        this.msg.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.sml.sendMsg(this.msg.getText());
                this.msg.setText("");
            }
        });

        this.getChildren().addAll(this.msg, this.send);
        this.getStyleClass().add("msg-toolbar");
    }
}

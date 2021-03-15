package uk.ac.soton.comp1206.Components.multiplayer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Message extends HBox {
    private VBox msgShell;
    private Label msgAuthor;
    private TextFlow msgContent;
    private Label msgTime;

    private boolean isUser;
    private boolean styleEnd = false;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    
    public Message(String author, String content, boolean isUser) {
        this.isUser = isUser;

        this.msgShell = new VBox();
        msgShell.setSpacing(1);

        this.msgAuthor = new Label(author + ":");
        msgAuthor.getStyleClass().add("msg-author");

        this.msgContent = this.createMsgContent(content);
        this.msgTime = this.createMsgTime();

        this.msgShell.getChildren().addAll(this.msgAuthor, this.msgContent);

        //The user's own messages will be on a different side to the rest
        if (isUser) {
            this.msgShell.getStyleClass().add("message-content-user");
            this.setAlignment(Pos.TOP_RIGHT);
            this.getChildren().addAll(this.msgShell, this.msgTime);
        } else {
            this.msgShell.getStyleClass().add("message-content");
            this.setAlignment(Pos.TOP_LEFT);
            this.getChildren().addAll(this.msgTime, this.msgShell);
        }
    }

    /**
     * Stores different stylings for a message
     * To add a new one:
     *  -> add a name and a unique key to the enum
     *  -> add a corresponding style to the message.css file
     */
    public enum MsgStyle {
        BOLD("**"),
        ITALIC("++"),
        UNDERLINE("__"),
        STRIKE("--");

        private static ArrayList<String> keyset = MsgStyle.createKeySet();

        private boolean active = false; //Whether the style is being used on the current word
        private String key; //The string prefix/suffix 

        private MsgStyle(String key) {
            this.key = key;
        }

        /**
         * @return the prefix/suffix
         */
        public String getKey() {
            return this.key;
        }

        /**
         * Changes whether the style is active or not
         */
        public void swapActive() {
            this.active = !this.active;
        }

        /**
         * Finds the corresponding style to the key
         * @param key The key 
         * @return the correct style
         */
        private static MsgStyle getStyle(String key) {
            for (MsgStyle style: MsgStyle.values()) {
                if (style.getKey().equals(key)) return style;
            }

            return null;
        }

        /**
         * Creates an arraylist of the keys
         * @return arraylist of keys
         */
        private static ArrayList<String> createKeySet() {
            var keys = new ArrayList<String>();

            for (MsgStyle style: MsgStyle.values()) {
                keys.add(style.getKey());
            }

            return keys;
        }
   
        /**
         * Resets all the stylings
         * Used after a message is sent
         */
        private static void resetActive() {
            for (MsgStyle style: MsgStyle.values()) {
                style.active = false;
            }
        }
    }

    /**
     * Creates and styles messages sent/received
     * @param content the content of the message
     * @return the styled TextFlow
     */
    private TextFlow createMsgContent(String content) {
        var msgContent = new TextFlow();
        var words = content.split(" ");

        for (String word: words) {
            //Styles each word
            var text = this.formatWord(new Text(), word);

            //If the style has ended it won't apply to the space
            //Needs work
            if (this.styleEnd) {
                var space = new Text(" ");
                msgContent.getChildren().addAll(text, space);
            } else {
                msgContent.getChildren().add(text);
            }
        }

        //resets all styling so they can't carry over to the next msg
        MsgStyle.resetActive();
        return msgContent;
    }

    /**
     * Used to format a specific word
     * @param text The text object storing the word 
     * @param word The word to be styled
     * @return The styled word component
     */
    private Text formatWord(Text text, String word) {
        //Looks for a prefix
        for (String key: MsgStyle.keyset) {
            if (word.startsWith(key)) {
                MsgStyle.getStyle(key).swapActive();
                return this.formatWord(text, word.substring(2));
            }
        }

        //Applies any stylings
        for (String key: MsgStyle.keyset) {
            var style = MsgStyle.getStyle(key);
            if (style.active) text.getStyleClass().add(style.toString());
        }

        //Looks for a suffix
        for (String key: MsgStyle.keyset) {
            if (word.endsWith(key)) {
                this.styleEnd = true;
                MsgStyle.getStyle(key).swapActive();
                return this.formatWord(text, word.substring(0, word.length()-2));
            }
        }

        text.setText(this.styleEnd ? word: word + " ");
        return text;
    }

    /**
     * Creates a label for the current time
     * @return The time label
     */
    private Label createMsgTime() {
        var time = new Label(formatter.format(LocalDateTime.now()));
        time.setMinWidth(40);
        time.getStyleClass().add("time");

        return time;
    }
}

package uk.ac.soton.comp1206.game.Multiplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A multiplayer channel that the user can joing
 */
public class Channel {
    //The channels name
    private String name;

    //The message history by user <username, User>
    private HashMap<String, ArrayList<String>> messages = new HashMap<>();

    public Channel(String name) {
        this.name = name;
    }

    /**
     * Updates the list of users 
     * @param users new list of users
     */
    public void updateUsers(String users) {
        var userArr = new ArrayList<>(Arrays.asList(users.split("\\s+")));
        userArr.remove(0);

        userArr.forEach(user -> {
            if (!this.messages.containsKey(user)) {
                this.messages.put(user, new ArrayList<String>());
            }
        });
    }

    /**
     * Adds a message to the list
     * @param user Who sent the message
     * @param message The content of the message
     */
    public void addMessage(String user, String message) {
        this.messages.get(user).add(message);
    }

    /**
     * Updates an existing user's nickname
     * @param oldName 
     * @param newName
     */
    public void updateNickname(String oldName, String newName) {
        if (!this.messages.containsKey(oldName)) return;

        var msgs = this.messages.get(oldName);
        this.messages.remove(oldName);
        this.messages.put(newName, msgs);
    }

    public String getName() {
        return this.name;
    }

    public String[] getUsers() {
        return this.messages.keySet().toArray(String[]::new);
    }
}

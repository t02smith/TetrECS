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

    //The list of users
    private HashMap<String, User> users = new HashMap<>();

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
            if (!this.users.containsKey(user)) {
                this.users.put(user, new User(user));
            }
        });
    }

    /**
     * Adds a message to the list
     * @param user Who sent the message
     * @param message The content of the message
     */
    public void addMessage(String user, String message) {
        this.users.get(user).addMessage(message);

    }

    /**
     * Updates an existing user's nickname
     * @param oldName 
     * @param newName
     */
    public void updateNickname(String oldName, String newName) {
        if (!this.users.containsKey(oldName)) return;

        var user = this.users.get(oldName);
        user.setName(newName);

        this.users.remove(oldName);
        this.users.put(newName, user);
    }

    public void updateUserScore(String name, int score) {
        var user = this.users.get(name);
        user.setScore(score);
    }

    public void updateUserLives(String name, int lives) {
        var user = this.users.get(name);
        user.setLives(lives);
    }

    public String getName() {
        return this.name;
    }

    public String[] getUsernames() {
        return this.users.keySet().toArray(String[]::new);
    }


}

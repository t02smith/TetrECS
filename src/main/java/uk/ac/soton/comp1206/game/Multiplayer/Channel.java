package uk.ac.soton.comp1206.game.Multiplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import uk.ac.soton.comp1206.Components.multiplayer.User;

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

    public void updateUserProperties(String name, int score, int lives) {
        this.updateUserScore(name, score);
        this.updateUserLives(name, lives);
    }

    /**
     * Updates a user's score
     * @param name the user's name
     * @param score the user's new score
     */
    public void updateUserScore(String name, int score) {
        var user = this.users.get(name);
        user.setScore(score);
    }

    /**
     * Updates the user's lives
     * @param name The user's name
     * @param lives The user's lives left
     */
    public void updateUserLives(String name, int lives) {
        var user = this.users.get(name);
        user.setLives(lives);
    }

    /**
     * Updates the user's grid
     * @param username The user's name
     * @param newGrid The user's new grid
     */
    public void updateUserGrid(String username, int[][] newGrid) {
        var user = this.users.get(username);

        user.applyGridChanges(newGrid);
    }

    public String getName() {
        return this.name;
    }

    public String[] getUsernames() {
        return this.users.keySet().toArray(String[]::new);
    }

    public Collection<User> getUsers() {
        return this.users.values();
    }


}

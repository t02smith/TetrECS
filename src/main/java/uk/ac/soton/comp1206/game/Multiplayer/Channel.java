package uk.ac.soton.comp1206.game.Multiplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.Components.multiplayer.User;

/**
 * A multiplayer channel that the user can joing
 * @author tcs1g20
 */
public class Channel {
    private static final Logger logger = LogManager.getLogger(Channel.class);

    //The channels name
    private String name;

    //The list of users
    private HashMap<String, User> users = new HashMap<>();

    /**
     * Creates a new channel
     * @param name the name of the channel
     */
    public Channel(String name) {
        logger.info("Channel {} created", name);
        this.name = name;
    }

    /**
     * Updates the list of users 
     * @param users new list of users
     */
    public void updateUsers(String users) {
        logger.info("Updating users in {}", this.name);
        var userArr = new ArrayList<>(Arrays.asList(users.split("\\s+")));
        userArr.remove(0);

        //Add a user if they are not currently being tracked
        userArr.forEach(user -> {
            if (!this.users.containsKey(user)) {
                this.users.put(user, new User(user));
            }
        });

        //Remove the user if they are no longer in the channel
        this.users.keySet().removeIf(user -> !userArr.contains(user));
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

        logger.info("Updating {} to {}", oldName, newName);
    }

    /**
     * Updates a user's properties
     * @param name the user who is being updated
     * @param score the user's new score
     * @param lives the user's new live count
     */
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

    /**
     * Displays the user as dead
     * @param name The user who died
     */
    public void killUser(String name) {
        logger.info("Killing {} :/", name);
        var user = this.users.get(name);
        user.displayDead();
    }

    /**
     * @return the name of the channel
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return list of user's names in the channel
     */
    public String[] getUsernames() {
        return this.users.keySet().toArray(String[]::new);
    }

    /**
     * @return list of the users
     */
    public Collection<User> getUsers() {
        return this.users.values();
    }


}

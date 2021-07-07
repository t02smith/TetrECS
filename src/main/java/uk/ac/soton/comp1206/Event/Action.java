package uk.ac.soton.comp1206.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.input.KeyCode;

/**
 * Each event can be assigned at most 2 keys
 * This means we can reassign key bindings during runtime as
 *  each event has a set of key bindings rather than a key
 *  being binded to an event
 * 
 * @author tcs1g20
 */
public enum Action {
    ROTATE_RIGHT    ("Rotate the next piece 90 degrees right",                           ActionTag.GAME,        KeyCode.CLOSE_BRACKET),
    ROTATE_LEFT     ("Rotate the next piece 90 degrees left",                            ActionTag.GAME,        KeyCode.OPEN_BRACKET),
    PLACE           ("Place the next piece on the board at the pointer if possible",     ActionTag.GAME,        KeyCode.ENTER, KeyCode.X),
    MOVE_UP         ("Move the pointer up by one square",                                ActionTag.GAME,        KeyCode.UP, KeyCode.W),
    MOVE_DOWN       ("Move the pointer down by one square",                              ActionTag.GAME,        KeyCode.DOWN, KeyCode.S),
    MOVE_LEFT       ("Move the pointer left by one square",                              ActionTag.GAME,        KeyCode.LEFT, KeyCode.A),
    MOVE_RIGHT      ("Move the pointer right by one square",                             ActionTag.GAME,        KeyCode.RIGHT, KeyCode.D),
    SWAP            ("Swap between the next and reserve piece",                          ActionTag.GAME,        KeyCode.SPACE, KeyCode.R),
    ESCAPE          ("Move back to the previous screen",                                 ActionTag.UTILITY,     KeyCode.ESCAPE),
    TOGGLE_PANEL     ("Toggles the online side panel.",                                  ActionTag.MULTIPLAYER, KeyCode.TAB, KeyCode.C);      

    //Hashmap of all the bindings
    private static HashMap<KeyCode, Action> bindings;

    //The keys currently assigned to an event
    private HashSet<KeyCode> keys = new HashSet<>();

    //The default set of keys for this event
    private final KeyCode[] defaultKeys;

    //The description of the action
    private final String description;

    private final ActionTag tag;

    //The event for the key
    private KeyListener event;

    private static final Logger logger = LogManager.getLogger(Action.class);

    /**
     * A new keybinding
     * @param description A textual description of the action
     * @param defaultKeys The default set of keys bound to it
     */
    private Action(String description, ActionTag tag, KeyCode... defaultKeys) {
        this.defaultKeys = defaultKeys;
        this.description = description;
        this.tag = tag;

        for (KeyCode key: defaultKeys) this.assignKey(null, key);
    }

    /**
     * Assigns a new key to an event
     * @param newKey the key being added
     */
    public boolean assignKey(KeyCode oldKey, KeyCode newKey) {
        //Static fields are initialized after enum so this was the easiest fix
        if (Action.bindings == null) bindings = new HashMap<>();

        //If the key is already assigned
        if (Action.bindings.containsKey(newKey) && newKey != null) return false;

        Action.bindings.put(newKey, this);

        //Remove the old key from any bindings
        if (oldKey != null) {
            Action.bindings.remove(oldKey);
            this.keys.remove(oldKey);
        }

        this.keys.add(newKey);
        try {logger.info(this + ": Binding for key {} changed to {}", oldKey, newKey);}
        catch(NullPointerException e) {}

        return true;
    }

    /**
     * binds a key to an action without replacing an existing key
     * @param newKey the key to be bound
     * @return whether the key was successfully bound
     */
    public boolean assignKey(KeyCode newKey) {
        logger.info("{} assigned to {}", newKey, this);
        return this.assignKey(null, newKey);
    }

    /**
     * Removes a key from an event
     * @param key the key being removed
     */
    public void removeKey(KeyCode key) {
        if (this.keys.contains(key)) {
            logger.info("{}: keybinding {} removed", this, key);
            Action.bindings.remove(key);
            this.keys.remove(key);
        }
    }

    /**
     * Resets a key back to its default key bindings
     */
    public void resetKeys() {
        logger.info("{}: keybindings reset to default", this);
        this.keys = new HashSet<>();
        Action.bindings = new HashMap<>();
        for (KeyCode key: defaultKeys) this.assignKey(null, key);
    }

    /**
     * Sets the the event that a key will call
     * @param event
     */
    public void setEvent(KeyListener event) {
        this.event = event;
    }

    /**
     * Executes the event for the key
     */
    public void execute() {
        this.event.onPress();
    }

    //GETTERS//

    /**
     * Gets an action by the key that calls it
     * @param key The key being pressed
     * @return The corresponding action
     */
    public static Action getAction(KeyCode key) {
        return Action.bindings.get(key);
    }

    /**
     * @return The action's description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets all bindings for this action
     * @return bindings for actions
     */
    public ArrayList<KeyCode> getBindings() {
        var bindings = new ArrayList<KeyCode>();
        this.keys.forEach(key -> bindings.add(key));

        //Each key can have two bindings
        //Adding a null value will result in a blank tile being displayed
        if (bindings.size() < 2) bindings.add(null);
        
        return bindings;
    }

    /**
     * Listener for when a key is pressed
     */
    public interface KeyListener {
        public void onPress();
    }

    //STATIC//

    /**
     * Resets all keys back to their defaults
     */
    public static void setDefaultKeys() {
        for (Action event: Action.values()) {
            event.resetKeys();
        }
    }

    /**
     * Executes a key pressed
     * @param pressed The key pressed
     */
    public static void executeEvent(KeyCode pressed) {

        if (Action.bindings.containsKey(pressed)) {
            var action = Action.bindings.get(pressed);

            if (ActionTag.activeTags.contains(action.tag)) {
                action.execute();
            }
        }
        
    }
}
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
public enum KeyBinding {
    ROTATE_RIGHT    ("Rotate the next piece 90 degrees right",                          KeyCode.CLOSE_BRACKET            ),
    ROTATE_LEFT     ("Rotate the next piece 90 degrees left",                           KeyCode.OPEN_BRACKET             ),
    PLACE           ("Place the next piece on the board at the pointer if possible",    KeyCode.ENTER,          KeyCode.X),
    MOVE_UP         ("Move the pointer up by one square",                               KeyCode.UP,             KeyCode.W),
    MOVE_DOWN       ("Move the pointer down by one square",                             KeyCode.DOWN,           KeyCode.S),
    MOVE_LEFT       ("Move the pointer left by one square",                             KeyCode.LEFT,           KeyCode.A),
    MOVE_RIGHT      ("Move the pointer right by one square",                            KeyCode.RIGHT,          KeyCode.D),
    SWAP            ("Swap between the next and reserve piece",                         KeyCode.SPACE,          KeyCode.R),
    ESCAPE          ("Move back to the previous screen",                                KeyCode.ESCAPE                   ),

    //Multiplayer
    TOGGLE_PANEL     ("Toggles the online side panel.",                                 KeyCode.TAB,            KeyCode.C);      

    //Hashmap of all the bindings
    private static HashMap<KeyCode, KeyBinding> bindings;

    //Disable all key commands e.g. when typing
    private static boolean disableKeys = true;

    //The keys currently assigned to an event
    private HashSet<KeyCode> keys = new HashSet<>();

    //The default set of keys for this event
    private final KeyCode[] defaultKeys;

    //The description of the action
    private final String description;

    //The event for the key
    private KeyListener event;

    private static final Logger logger = LogManager.getLogger(KeyBinding.class);

    private KeyBinding(String description, KeyCode... defaultKeys) {
        this.defaultKeys = defaultKeys;
        this.description = description;

        for (KeyCode key: defaultKeys) this.assignKey(null, key);
    }

    /**
     * Assigns a new key to an event
     * @param newKey the key being added
     */
    public boolean assignKey(KeyCode oldKey, KeyCode newKey) {
        //Static fields are initialized after enum so this was the easiest fix
        if (KeyBinding.bindings == null) bindings = new HashMap<>();

        //If the key is already assigned
        if (KeyBinding.bindings.containsKey(newKey) && newKey != null) return false;

        KeyBinding.bindings.put(newKey, this);

        //Remove the old key from any bindings
        if (oldKey != null) {
            KeyBinding.bindings.remove(oldKey);
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
            KeyBinding.bindings.remove(key);
            this.keys.remove(key);
        }
    }

    /**
     * Resets a key back to its default key bindings
     */
    public void resetKeys() {
        logger.info("{}: keybindings reset to default", this);
        this.keys = new HashSet<>();
        KeyBinding.bindings = new HashMap<>();
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

    /**
     * Used to disable the use of key actions
     *  e.g. when typing a message
     * @param keysDisabled the new state for disableKeys
     */
    public static void setKeysDisabled(boolean keysDisabled) {
        logger.info("Key actions: {}", !keysDisabled);
        KeyBinding.disableKeys = keysDisabled;
    }

    //GETTERS//

    public static boolean getKeysDisabled() {
        return disableKeys;
    }

    /**
     * Gets an action by the key that calls it
     * @param key The key being pressed
     * @return The corresponding action
     */
    public static KeyBinding getAction(KeyCode key) {
        return KeyBinding.bindings.get(key);
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
        for (KeyBinding event: KeyBinding.values()) {
            event.resetKeys();
        }
    }

    /**
     * Executes a key pressed
     * @param pressed The key pressed
     */
    public static void executeEvent(KeyCode pressed) {
        if (KeyBinding.disableKeys && pressed != KeyCode.ESCAPE) return;

        if (KeyBinding.bindings.containsKey(pressed)) {
            KeyBinding.bindings.get(pressed).execute();
        }
        
    }
}
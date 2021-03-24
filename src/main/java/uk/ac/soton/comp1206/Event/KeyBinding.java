package uk.ac.soton.comp1206.Event;

import java.util.HashMap;
import java.util.HashSet;

import javafx.scene.input.KeyCode;

/**
 * Each event can be assigned a key
 * This means we can reassign key bindings during runtime as
 *  each event has a set of key bindings rather than a key
 *  being binded to an event
 */
public enum KeyBinding {
    ROTATE_RIGHT    (new KeyCode[] {KeyCode.CLOSE_BRACKET},     "Rotate the next piece 90 degrees right"),
    ROTATE_LEFT     (new KeyCode[] {KeyCode.OPEN_BRACKET},      "Rotate the next piece 90 degrees left"),
    PLACE           (new KeyCode[] {KeyCode.ENTER, KeyCode.X},  "Place the next piece on the board at the pointer if possible"),
    MOVE_UP         (new KeyCode[] {KeyCode.UP, KeyCode.W},     "Move the pointer up by one square"),
    MOVE_DOWN       (new KeyCode[] {KeyCode.DOWN, KeyCode.S},   "Move the pointer down by one square"),
    MOVE_LEFT       (new KeyCode[] {KeyCode.LEFT, KeyCode.A},   "Move the pointer left by one square"),
    MOVE_RIGHT      (new KeyCode[] {KeyCode.RIGHT, KeyCode.D},  "Move the pointer right by one square"),
    SWAP            (new KeyCode[] {KeyCode.SPACE, KeyCode.R},  "Swap between the next and reserve piece"),
    QUIT            (new KeyCode[] {KeyCode.ESCAPE},            "Quit the game"),

    //Multiplayer
    TOGGLE_PANEL     (new KeyCode[] {KeyCode.TAB, KeyCode.C},                 "Toggles the online side panel.");      

    //Hashmap of all the bindings
    private static HashMap<KeyCode, KeyBinding> bindings;

    //Disable all key commands e.g. when typing
    private static boolean disableKeys = false;

    //The keys currently assigned to an event
    private HashSet<KeyCode> keys = new HashSet<>();

    //The default set of keys for this event
    private final KeyCode[] defaultKeys;

    private final String description;

    //The event for the key
    private KeyListener event;

    private KeyBinding(KeyCode[] defaultKeys, String description) {
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

        if (KeyBinding.bindings.containsKey(newKey)) return false;

        KeyBinding.bindings.put(newKey, this);
        if (oldKey != null) KeyBinding.bindings.remove(oldKey);
        this.keys.add(newKey);
        return true;
    }

    /**
     * Removes a key from an event
     * @param key the key being removed
     */
    public void removeKey(KeyCode key) {
        if (this.keys.contains(key)) {
            this.keys.remove(key);
            KeyBinding.bindings.remove(key);
        }
    }

    /**
     * Resets a key back to its default key bindings
     */
    public void resetKeys() {
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
        if (KeyBinding.disableKeys) return;

        if (KeyBinding.bindings.containsKey(pressed)) {
            KeyBinding.bindings.get(pressed).execute();
        }
        
    }

    /**
     * Used to disable the use of key actions
     *  e.g. when typing a message
     * @param keysDisabled the new state for disableKeys
     */
    public static void setKeysDisabled(boolean keysDisabled) {
        KeyBinding.disableKeys = keysDisabled;
    }

    public static KeyBinding getAction(KeyCode key) {
        return KeyBinding.bindings.get(key);
    }

    public String getDescription() {
        return this.description;
    }

    public HashSet<KeyCode> getBindings() {
        return this.keys;
    }

    /**
     * Listener for when a key is pressed
     */
    public interface KeyListener {
        public void onPress();
    }
}
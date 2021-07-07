package uk.ac.soton.comp1206.Event;

import java.util.Arrays;
import java.util.HashSet;

public enum ActionTag {
    UTILITY,
    GAME,
    TYPING,
    MULTIPLAYER;

    public static HashSet<ActionTag> activeTags = new HashSet<>();

    //Holds any tags to be restored into activeTags when signalled
    private static HashSet<ActionTag> tempStore;

    /**
     * Resets the list of active tags
     */
    public static void resetActiveTags() {
        activeTags = new HashSet<>();

        //Add any default tags
        activeTags.add(ActionTag.UTILITY);
    }

    public static void tempChangeTags(ActionTag... tempTags) {

        tempStore = new HashSet<>(activeTags);
        activeTags.clear();
        activeTags.addAll(Arrays.asList(tempTags));
    }

    public static void restoreTempTags() {
        if (tempStore.isEmpty()) return;

        activeTags.clear();
        activeTags.addAll(tempStore);
        tempStore.clear();
    }
}

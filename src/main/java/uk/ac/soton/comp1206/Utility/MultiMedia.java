package uk.ac.soton.comp1206.Utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Animation;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.MediaPlayer;

/**
 * To play any background music or sound effects
 * @author tcs1g20
 */
public class MultiMedia {
    private static final Logger logger = LogManager.getLogger(Utility.class);

    //For sound effects
    private static MediaPlayer sfx;

    //For background music
    private static MediaPlayer music;

    //Mute the audio
    private static SimpleBooleanProperty audioEnabled = new SimpleBooleanProperty(true);

    //Current volume
    private static double volume = 1;

    /**
     * Plays a given audio file from a given file
     * @param filename The name of the audio file
     */
    public static void playSFX(String filename) {
        if (audioEnabled.get()) {
            if (sfx != null) sfx.stop();

            var toPlay = MultiMedia.class.getResource("/music/" + filename).toExternalForm();
            logger.info("Playing audio {}", toPlay);
    
            sfx = new MediaPlayer(
                new javafx.scene.media.Media(toPlay)
            );
    
            sfx.setVolume(volume);

            sfx.play();
        }

    }

    /**
     * Plays a given audio file on loop
     * @param filename the audio filename
     */
    public static void playMusic(String filename) {
        if (audioEnabled.get()) {
            if (music != null) music.stop();

            var toPlay = MultiMedia.class.getResource("/music/" + filename).toExternalForm();
            logger.info("Playing music {}", toPlay);
    
            music = new MediaPlayer(
                new javafx.scene.media.Media(toPlay)
            );
            music.setCycleCount(Animation.INDEFINITE);
    
            music.setVolume(volume);
            music.play();
        }
    }

    /**
     * Stops currently playing song
     */
    public static void stopMusic() {
        if (music != null) {
            logger.info("Stopping background music");
            music.stop();
        }
    }

    /**
     * Toggles whether the audio is enabled or not
     */
    public static void toggleAudioEnabled() {
        logger.info("audio enabled: {}", !audioEnabled.get());
        audioEnabled.set(!audioEnabled.get());

        if (!audioEnabled.get()) stopMusic();
    }

    /**
     * Sets the audio volume
     * @param volume The new volume
     */
    public static void setVolume(double newVolume) {
        logger.info("Setting volume to {}", newVolume);
        volume = newVolume;

        if (music != null) music.setVolume(newVolume);
        if (sfx != null) sfx.setVolume(newVolume);
    }

    public static double getVolume() {
        return volume*100;
    }
}

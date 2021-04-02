package uk.ac.soton.comp1206.Utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Animation;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.MediaPlayer;

/**
 * To play any background music or sound effects
 */
public class MultiMedia {
    private static final Logger logger = LogManager.getLogger(Utility.class);

    //For sound effects
    private static MediaPlayer audio;

    //For background music
    private static MediaPlayer music;

    //Mute the audio
    private static SimpleBooleanProperty audioEnabled = new SimpleBooleanProperty(false);

    /**
     * Plays a given audio file from a given file
     * @param filename The name of the audio file
     */
    public static void playSFX(String filename) {
        if (audioEnabled.get()) {
            if (audio != null) audio.stop();

            var toPlay = MultiMedia.class.getResource("/music/" + filename).toExternalForm();
            logger.info("Playing audio {}", toPlay);
    
            audio = new MediaPlayer(
                new javafx.scene.media.Media(toPlay)
            );
    
            

            audio.play();
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
    
            music.setVolume(0.5);
            music.play();
        }
    }
}

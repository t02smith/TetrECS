package uk.ac.soton.comp1206.Utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.image.Image;

public class Utility {
    protected static final Logger logger = LogManager.getLogger(Utility.class);
    
    public static String getStyle(String filename) {
        logger.info("Getting stylesheet '{}'", filename);
        return Utility.class.getResource("/style/" + filename).toExternalForm();
    }

    public static Image getImage(String filename) {
        logger.info("Getting image '{}'", filename);
        return new Image(Utility.class.getResource("/images/" + filename).toExternalForm());
    }
}

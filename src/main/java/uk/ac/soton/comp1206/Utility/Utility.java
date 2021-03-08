package uk.ac.soton.comp1206.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

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

    public static ArrayList<String> readFromFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(
                new FileReader(
                    new File(Utility.class.getResource(filename).toURI())
                )
            );

            String line;
            var output = new ArrayList<String>();
            while ((line = br.readLine()) != null) output.add(line);

            return output;
        } catch (FileNotFoundException | URISyntaxException e) {
            logger.error("File {} not found", filename);
        } catch (IOException e) {
            logger.error("Error reading file {}", filename);
        }

        return null;
    }
}

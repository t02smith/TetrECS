package uk.ac.soton.comp1206.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.image.Image;

/**
 * Utility class to handle file IO
 */
public class Utility {
    protected static final Logger logger = LogManager.getLogger(Utility.class);

    //Stores all images to prevent them being loaded multiple times
    protected static HashMap<String, Image> images = new HashMap<>();

    //Stores all needed stylesheets
    protected static HashMap<String, String> stylesheets = new HashMap<>();
    
    /**
     * Gets a stylesheet from the style folder
     * @param filename the name of the stylesheet
     * @return the stylesheet
     */
    public static String getStyle(String filename) {
        if (stylesheets.containsKey(filename)) return stylesheets.get(filename);
        else {
            logger.info("Getting stylesheet '{}'", filename);
            var stylesheet = Utility.class.getResource("/style/" + filename).toExternalForm();
            stylesheets.put(filename, stylesheet);
            return stylesheet;
        }
    }

    /**
     * Gets an image from the images folder
     * @param filename the name of the image
     * @return the image
     */
    public static Image getImage(String filename) {
        if (images.containsKey(filename)) return images.get(filename);
        else {
            logger.info("Getting image '{}'", filename);
            var image =  new Image(Utility.class.getResource("/images/" + filename).toExternalForm());
            images.put(filename, image);
            return image;
        }

    }

    /**
     * Reads the data from a file and stores each line
     * @param filename the name of the file
     * @return an arraylist of the files lines
     */
    public static ArrayList<String> readFromFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(
                new FileReader(
                    new File(Utility.class.getResource(filename).toURI())
                )
            );

            String line;
            var output = new ArrayList<String>();

            //Reads file line by line
            while ((line = br.readLine()) != null) output.add(line);

            return output;
        } catch (FileNotFoundException | URISyntaxException e) {
            logger.error("File {} not found", filename);
        } catch (IOException e) {
            logger.error("Error reading file {}", filename);
        }

        return null;
    }

    /**
     * Writes a given string to a file
     * @param filename the file to write to
     * @param lines the string to write to the file
     */
    public static void writeToFile(String filename, String lines) {
        try {
            var fw = new FileWriter(
                new File(Utility.class.getResource(filename).toURI())
            );
            
            fw.write(lines);
            fw.close();
            logger.info("Writing {} to '{}'", lines, filename);

        } catch (URISyntaxException | IOException e) {
            logger.error("Error writing to file {}", filename);
        }

    }
}

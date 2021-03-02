package uk.ac.soton.comp1206.Utility;

import javafx.scene.image.Image;

public class Utility {
    
    public static String getStyle(String filename) {
        return Utility.class.getResource("/style/" + filename).toExternalForm();
    }

    public static Image getImage(String filename) {
        return new Image(Utility.class.getResource("/images/" + filename).toExternalForm());
    }
}

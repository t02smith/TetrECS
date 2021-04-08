package uk.ac.soton.comp1206.Components.Game;

import java.util.Arrays;
import java.util.Random;

import javafx.scene.image.Image;

import uk.ac.soton.comp1206.Utility.CircularQueue;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * This is a custom class for the colours in my game
 * Each colour is assigned a tile image that it will display
 * @author tcs1g20
 */
public enum Colour {
    CYAN("cyan.png"),
    DARK_BLUE("dark-blue.png"),
    GREEN("green.png"),
    ORANGE("orange.png"),
    PURPLE("purple.png"),
    RED("red.png"),
    YELLOW("yellow.png"),
    PINK("pink.png"),
    GREY("grey.png"),
    LILAC("lilac.png"),
    TURQOISE("turqoise.png"),
    LIME("lime.png"),
    RAINBOW("rainbow.png"),
    LIGHT_RED("light-red.png"),
    WHITE("white.png"),
    TRANSPARENT("transparent.png");

    //The image it will display
    private final Image icon;

    //Ensures that all the colours are used and can be reused if we add more pieces
    private static final CircularQueue<Colour> colourCycle = new CircularQueue<>(Arrays.asList(Colour.values()));

    /**
     * @param filename The location of the icon
     */
    private Colour(String filename) {
        this.icon = Utility.getImage("tiles/" + filename);
    }

    /**
     * Gets the image of the colour
     * @return
     */
    public Image getIcon() {
        return this.icon;
    }

    //STATIC//

    /**
     * Cycles through the list of colours
     * @return
     */
    public static Colour nextColour() {
        if (colourCycle.peek().equals(TRANSPARENT)) {
            colourCycle.dequeue();
        }

        return colourCycle.dequeue();
    }

    /**
     * Gets a random colour from our set
     * @return A random colour
     */
    public static Colour randomColour() {
        var random = new Random();
        var colours = Colour.values();
        Colour colour;

        do { //Get the first colour that isn't transparent
            colour = colours[random.nextInt(colours.length)];
        } while (colour.equals(TRANSPARENT)); 

        return colour;
    }


}
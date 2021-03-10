package uk.ac.soton.comp1206.Components.Game;

import java.util.Arrays;
import java.util.Random;

import javafx.scene.image.Image;

import uk.ac.soton.comp1206.Utility.CircularQueue;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * This class is used to get any of the different colour icons
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
    TRANSPARENT("transparent.png");

    private final Image icon;

    private static final CircularQueue<Colour> colourCycle = new CircularQueue<>(Arrays.asList(Colour.values()));

    /**
     * @param filename The location of the icon
     */
    private Colour(String filename) {
        this.icon = Utility.getImage("tiles/" + filename);
    }

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

    public static Colour randomColour() {
        var random = new Random();
        var colours = Colour.values();
        Colour colour;

        do {
            colour = colours[random.nextInt(colours.length)];
        } while (colour.equals(TRANSPARENT));

        return colour;
    }

    public Image getIcon() {
        return this.icon;
    }
}
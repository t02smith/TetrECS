package uk.ac.soton.comp1206.game.Powerup;

import javafx.scene.image.Image;
import uk.ac.soton.comp1206.Utility.Utility;

/**
 * List of power up and their prices
 */
public enum PowerUp {
    PUSH_DOWN       (100,   "down.png"),
    PUSH_UP         (100,   "up.png"),
    PUSH_LEFT       (100,   "left.png"),
    PUSH_RIGHT      (100,   "right.png"),
    NUKE            (1000,  "nuke.png"),
    RESTORE_LIFE    (2500,  "heart.png"),
    NEW_PIECE       (50,    "red.png"),
    DOUBLE_POINTS   (150,   "two.png");

    //Called when a powerup is used
    private PowerUpAction action;

    //How much the powerup costs
    private int price;

    //The icon to be displayed
    private Image icon;

    /**
     * A new power up
     * @param price its price
     */
    private PowerUp(int price, String image) {
        this.price = price;
        this.icon = Utility.getImage("powerups/" + image);
    }

    /**
     * Set the action of a given powerup
     * @param action The action
     */
    public void setAction(PowerUpAction action) {
        this.action = action;
    }

    /**
     * Use the powerup
     */
    public void execute() {
        this.action.execute();
        this.price *= 2;
    }

    /**
     * @return the price of using the powerup
     */
    public int getPrice() {
        return this.price;
    }

    public Image getIcon() {
        return this.icon;
    }

    /**
     * The interface for executing a powerup
     */
    public interface PowerUpAction {
        public void execute();
    }
}

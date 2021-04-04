package uk.ac.soton.comp1206.game.Powerup;

public enum PowerUp {
    PUSH_DOWN(10),
    PUSH_UP(10),
    PUSH_LEFT(10),
    PUSH_RIGHT(10),
    NUKE(10),
    RESTORE_LIFE(10),
    NEW_PIECE(10),
    DOUBLE_POINTS(10);

    private PowerUpAction action;

    private int price;

    private PowerUp(int price) {
        this.price = price;
    }

    public void setAction(PowerUpAction action) {
        this.action = action;
    }

    public void execute() {
        this.action.execute();
    }

    public int getPrice() {
        return this.price;
    }

    public interface PowerUpAction {
        public void execute();
    }
}

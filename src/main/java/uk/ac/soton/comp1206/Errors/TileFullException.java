package uk.ac.soton.comp1206.Errors;

@SuppressWarnings("serial")
public class TileFullException extends Exception {
    public TileFullException() {
        super("This tile is full");
    }
}

package concentratequest.exception;

/** Thrown when an item (or an equipment slot) the player chose is not there. */
public class ItemNotFoundException extends GameException {
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }
}

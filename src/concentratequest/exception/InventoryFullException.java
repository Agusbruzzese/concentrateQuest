package concentratequest.exception;

/** Thrown when an item does not fit in the backpack. */
public class InventoryFullException extends GameException {
    private static final long serialVersionUID = 1L;

    public InventoryFullException(String message) {
        super(message);
    }
}

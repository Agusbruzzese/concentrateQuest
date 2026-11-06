package concentratequest.exception;

/** Thrown when a quest needs items that the hero does not carry. */
public class MissingItemsException extends GameException {
    private static final long serialVersionUID = 1L;

    public MissingItemsException(String message) {
        super(message);
    }
}

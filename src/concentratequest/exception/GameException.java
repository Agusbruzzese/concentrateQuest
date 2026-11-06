package concentratequest.exception;

/**
 * Root of the checked exceptions of the game: things that can go wrong while
 * playing (a full backpack, a missing item...) and that the caller must handle.
 */
public class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}

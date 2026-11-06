package concentratequest.exception;

/**
 * Unchecked on purpose: completing a mission that never started is a bug in
 * the calling code, not a situation the player can cause or recover from.
 */
public class InvalidMissionStateException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public InvalidMissionStateException(String message) {
        super(message);
    }
}

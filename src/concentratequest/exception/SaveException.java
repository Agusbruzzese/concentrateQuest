package concentratequest.exception;

/** Saving or loading failed. The original IOException is kept as the cause. */
public class SaveException extends GameException {
    private static final long serialVersionUID = 1L;

    public SaveException(String message, Throwable cause) {
        super(message + " (" + cause.getMessage() + ")", cause);
    }
}

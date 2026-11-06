package concentratequest.exception;

/** A line of a data file (e.g. custom items) could not be understood. */
public class InvalidDataException extends GameException {
    private static final long serialVersionUID = 1L;

    private final int lineNumber;

    public InvalidDataException(int lineNumber, String message) {
        super("Line " + lineNumber + ": " + message);
        this.lineNumber = lineNumber;
    }

    public InvalidDataException(int lineNumber, String message, Throwable cause) {
        super("Line " + lineNumber + ": " + message, cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}

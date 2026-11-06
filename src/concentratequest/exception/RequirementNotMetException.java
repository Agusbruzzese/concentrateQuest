package concentratequest.exception;

/** Thrown when a hero tries to use an item without the level, class or stats it needs. */
public class RequirementNotMetException extends GameException {
    private static final long serialVersionUID = 1L;

    public RequirementNotMetException(String message) {
        super(message);
    }
}

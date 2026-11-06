package concentratequest.exception;

/** Thrown when the hero tries to buy something that costs more than their gold. */
public class NotEnoughGoldException extends GameException {
    private static final long serialVersionUID = 1L;

    public NotEnoughGoldException(String message) {
        super(message);
    }
}

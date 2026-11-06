package concentratequest.rules;

import java.io.Serializable;

/**
 * A game rule the player can pick in the settings. Serializable so the
 * chosen rules are saved with the game.
 */
public interface Rule extends Serializable {

    String getName();

    String getDescription();
}

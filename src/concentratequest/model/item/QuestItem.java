package concentratequest.model.item;

import concentratequest.exception.GameException;
import concentratequest.model.hero.Hero;

/**
 * An item that only matters for quests: materials found on short quests and
 * the keys made from them on long quests. It cannot be used directly; the
 * quest that needs it takes it from the backpack when it starts.
 */
public class QuestItem extends Item {
    private static final long serialVersionUID = 1L;

    private final String purpose;

    public QuestItem(String name, String purpose) {
        super(name, 1);
        this.purpose = purpose;
    }

    @Override
    public String getKind() {
        return "Quest item";
    }

    @Override
    protected String getDetails() {
        return purpose;
    }

    @Override
    public String getUseVerb() {
        return "Inspect";
    }

    @Override
    public void use(Hero hero) throws GameException {
        throw new GameException(getName() + ": " + purpose + ". It is used automatically when that quest starts.");
    }
}

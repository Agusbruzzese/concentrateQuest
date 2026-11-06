package concentratequest.model.item;

import concentratequest.exception.GameException;
import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.Hero;
import concentratequest.model.hero.HeroType;

/**
 * An Item that is also Equippable. It holds the behaviour every piece of gear
 * shares, so Weapon, Shield... only say which slot and which bonus.
 */
public abstract class Gear extends Item implements Equippable {
    private static final long serialVersionUID = 1L;

    protected Gear(String name, int minLevel,
                   CharacterStats requiredStats, HeroType... allowedClasses) {
        super(name, minLevel, requiredStats, allowedClasses);
    }

    @Override
    public String getKind() {
        return getSlot().getDisplayName();
    }

    /** Gear is worth twice as much as other items of the same level. */
    @Override
    public int getValue() {
        return 2 * super.getValue();
    }

    @Override
    public String getUseVerb() {
        return "Equip";
    }

    @Override
    public void use(Hero hero) throws GameException {
        hero.equip(this);
    }
}

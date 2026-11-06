package concentratequest.model.item;

import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.HeroType;

public class Ring extends Gear {
    private static final long serialVersionUID = 1L;

    private final CharacterStats bonus;

    public Ring(String name, int minLevel, CharacterStats bonus,
                  CharacterStats requiredStats, HeroType... allowedClasses) {
        super(name, minLevel, requiredStats, allowedClasses);
        this.bonus = bonus;
    }

    @Override
    public EquipSlot getSlot() {
        return EquipSlot.RING;
    }

    @Override
    public CharacterStats getStatBonus() {
        return bonus;
    }

    @Override
    protected String getDetails() {
        return "Bonus " + bonus.toShortString();
    }
}

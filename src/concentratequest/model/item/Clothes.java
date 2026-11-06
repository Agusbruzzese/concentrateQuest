package concentratequest.model.item;

import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.HeroType;

public class Clothes extends Gear {
    private static final long serialVersionUID = 1L;

    private final int defense;

    public Clothes(String name, int minLevel, int defense,
                   CharacterStats requiredStats, HeroType... allowedClasses) {
        super(name, minLevel, requiredStats, allowedClasses);
        this.defense = defense;
    }

    @Override
    public EquipSlot getSlot() {
        return EquipSlot.CLOTHES;
    }

    @Override
    public int getDefenseBonus() {
        return defense;
    }

    @Override
    protected String getDetails() {
        return "Defense +" + defense;
    }
}

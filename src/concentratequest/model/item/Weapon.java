package concentratequest.model.item;

import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.HeroType;

public class Weapon extends Gear {
    private static final long serialVersionUID = 1L;

    private final int damage;
    //constructor chain
    public Weapon(String name, int minLevel, int damage,
                  CharacterStats requiredStats, HeroType... allowedClasses) {
        super(name, minLevel, requiredStats, allowedClasses);
        this.damage = damage;
    }

    @Override
    public EquipSlot getSlot() {
        return EquipSlot.WEAPON;
    }

    @Override
    public int getDamageBonus() {
        return damage;
    }

    @Override
    protected String getDetails() {
        return "Damage +" + damage;
    }
}

package concentratequest.model.item;

import concentratequest.model.hero.CharacterStats;

/**
 * Something that can be worn in an equipment slot. The default methods mean
 * each piece only overrides the bonus it actually gives.
 */
public interface Equippable {

    EquipSlot getSlot();

    default int getDamageBonus() {
        return 0;
    }

    default int getDefenseBonus() {
        return 0;
    }

    default CharacterStats getStatBonus() {
        return CharacterStats.ZERO;
    }
}

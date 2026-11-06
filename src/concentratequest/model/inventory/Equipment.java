package concentratequest.model.inventory;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Optional;

import concentratequest.model.hero.CharacterStats;
import concentratequest.model.item.EquipSlot;
import concentratequest.model.item.Equippable;
import concentratequest.model.item.Gear;

/**
 * What the hero is wearing: at most one piece of gear per slot. The totals
 * only use the Equippable methods, so new kinds of gear need no change here.
 */
public class Equipment implements Serializable {
    private static final long serialVersionUID = 1L;

    private final EnumMap<EquipSlot, Gear> slots = new EnumMap<>(EquipSlot.class);

    /** Puts the gear in its own slot and returns what was there before. */
    public Optional<Gear> put(Gear gear) {
        return Optional.ofNullable(slots.put(gear.getSlot(), gear));
    }

    public Optional<Gear> get(EquipSlot slot) {
        return Optional.ofNullable(slots.get(slot));
    }

    public Optional<Gear> remove(EquipSlot slot) {
        return Optional.ofNullable(slots.remove(slot));
    }

    public int getDamage() {
        int total = 0;
        for (Equippable piece : slots.values()) {
            total += piece.getDamageBonus();
        }
        return total;
    }

    public int getDefense() {
        int total = 0;
        for (Equippable piece : slots.values()) {
            total += piece.getDefenseBonus();
        }
        return total;
    }

    public CharacterStats getStatBonus() {
        CharacterStats total = CharacterStats.ZERO;
        for (Equippable piece : slots.values()) {
            total = total.plus(piece.getStatBonus());
        }
        return total;
    }
}

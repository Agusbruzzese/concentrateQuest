package concentratequest.model.hero;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** The four base attributes. Immutable: every change returns a new object. */
public final class CharacterStats implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final CharacterStats ZERO = new CharacterStats(0, 0, 0, 0);

    private final int strength;
    private final int agility;
    private final int intelligence;
    private final int stamina;

    public CharacterStats(int strength, int agility, int intelligence, int stamina) {
        this.strength = strength;
        this.agility = agility;
        this.intelligence = intelligence;
        this.stamina = stamina;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getStamina() {
        return stamina;
    }

    public int get(StatType type) {
        return switch (type) {
            case STRENGTH -> strength;
            case AGILITY -> agility;
            case INTELLIGENCE -> intelligence;
            case STAMINA -> stamina;
        };
    }

    public CharacterStats plus(CharacterStats other) {
        return new CharacterStats(strength + other.strength,
                agility + other.agility,
                intelligence + other.intelligence,
                stamina + other.stamina);
    }

    /** Overload of plus: adds points to a single stat. */
    public CharacterStats plus(StatType type, int amount) {
        return switch (type) {
            case STRENGTH -> new CharacterStats(strength + amount, agility, intelligence, stamina);
            case AGILITY -> new CharacterStats(strength, agility + amount, intelligence, stamina);
            case INTELLIGENCE -> new CharacterStats(strength, agility, intelligence + amount, stamina);
            case STAMINA -> new CharacterStats(strength, agility, intelligence, stamina + amount);
        };
    }

    /** True if every stat is at least the required one. */
    public boolean meets(CharacterStats required) {
        return strength >= required.strength
                && agility >= required.agility
                && intelligence >= required.intelligence
                && stamina >= required.stamina;
    }

    /** Only the stats that are not zero, e.g. "STR 40 INT 10". */
    public String toShortString() {
        List<String> parts = new ArrayList<>();
        for (StatType type : StatType.values()) {
            if (get(type) != 0) {
                parts.add(type.getShortName() + " " + get(type));
            }
        }
        return parts.isEmpty() ? "-" : String.join(" ", parts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CharacterStats other)) {
            return false;
        }
        return strength == other.strength && agility == other.agility
                && intelligence == other.intelligence && stamina == other.stamina;
    }

    @Override
    public int hashCode() {
        return Objects.hash(strength, agility, intelligence, stamina);
    }

    @Override
    public String toString() {
        return "STR " + strength + " / AGI " + agility
                + " / INT " + intelligence + " / STA " + stamina;
    }
}

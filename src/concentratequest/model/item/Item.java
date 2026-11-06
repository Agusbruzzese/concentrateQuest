package concentratequest.model.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import concentratequest.exception.GameException;
import concentratequest.exception.RequirementNotMetException;
import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.Hero;
import concentratequest.model.hero.HeroType;

/**
 * Base class of everything a hero can carry. It knows who may use it
 * (level, class, stats); each subclass decides what "using" it means.
 */
public abstract class Item implements Serializable, Comparable<Item> {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int minLevel;
    private final CharacterStats requiredStats;
    private final EnumSet<HeroType> allowedClasses; // empty = every class

    protected Item(String name, int minLevel,
                   CharacterStats requiredStats, HeroType... allowedClasses) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("An item needs a name");
        }
        if (minLevel < 1) {
            throw new IllegalArgumentException("The level of " + name + " must be at least 1");
        }
        this.name = name.trim();
        this.minLevel = minLevel;
        this.requiredStats = requiredStats == null ? CharacterStats.ZERO : requiredStats;
        this.allowedClasses = EnumSet.noneOf(HeroType.class);
        this.allowedClasses.addAll(Arrays.asList(allowedClasses));
    }

    /** Overload for items without stat requirements. */
    protected Item(String name, int minLevel, HeroType... allowedClasses) {
        this(name, minLevel, CharacterStats.ZERO, allowedClasses);
    }

    // ---------- what each kind of item defines ----------

    /** Short label for the kind of item: "Weapon", "Potion"... */
    public abstract String getKind();

    /** Extra text specific to each item kind (damage, defense, healing...). */
    protected abstract String getDetails();

    /** The word shown in menus for the use action: "Equip", "Drink"... */
    public abstract String getUseVerb();

    /** What happens when the hero uses this item. */
    public abstract void use(Hero hero) throws GameException;

    // ---------- requirements ----------

    public String getName() {
        return name;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public CharacterStats getRequiredStats() {
        return requiredStats;
    }

    /** Gold paid by the shop for this item. Higher-level items are worth more. */
    public int getValue() {
        return 10 * minLevel;
    }

    public boolean isAllowedFor(HeroType type) {
        return allowedClasses.isEmpty() || allowedClasses.contains(type);
    }

    /** Checks level, class and base stats. */
    public boolean canBeUsedBy(Hero hero) {
        return findProblem(hero).isEmpty();
    }

    /** Same check as canBeUsedBy, but explains what is missing. */
    public void checkUsableBy(Hero hero) throws RequirementNotMetException {
        Optional<String> problem = findProblem(hero);
        if (problem.isPresent()) {
            throw new RequirementNotMetException(
                    hero.getName() + " cannot use " + name + ": " + problem.get());
        }
    }

    private Optional<String> findProblem(Hero hero) {
        if (hero.getLevel() < minLevel) {
            return Optional.of("it requires level " + minLevel);
        }
        if (!isAllowedFor(hero.getType())) {
            return Optional.of("it is only for " + allowedClassesText());
        }
        if (!hero.getBaseStats().meets(requiredStats)) {
            return Optional.of("it requires " + requiredStats.toShortString());
        }
        return Optional.empty();
    }

    private String allowedClassesText() {
        if (allowedClasses.isEmpty()) {
            return "All classes";
        }
        List<String> names = new ArrayList<>();
        for (HeroType type : allowedClasses) {
            names.add(type.getDisplayName());
        }
        return String.join("/", names);
    }

    // ---------- display and comparison ----------

    public String describe() {
        String requirement = requiredStats.equals(CharacterStats.ZERO)
                ? "" : " | needs " + requiredStats.toShortString();
        return name + " [" + getKind() + "] " + getDetails()
                + " | Lv " + minLevel + requirement + " | " + allowedClassesText();
    }

    /** Natural order: by level, then by name. */
    @Override
    public int compareTo(Item other) {
        int byLevel = Integer.compare(minLevel, other.minLevel);
        return byLevel != 0 ? byLevel : name.compareTo(other.name);
    }

    /**
     * Same kind and same name means same item. Needed because loading a save
     * file creates new objects, so == would not work anymore.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item other)) {
            return false;
        }
        return name.equals(other.name) && getKind().equals(other.getKind());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, getKind());
    }

    @Override
    public String toString() {
        return name;
    }
}

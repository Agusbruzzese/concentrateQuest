package concentratequest.model.hero;

/**
 * The playable classes. Adding a new class means writing the subclass,
 * adding one constant here and one case in create(): menus and the item loader pick it up by themselves.
 */
public enum HeroType {
    WIZARD("Wizard", "Smart but fragile, grows in intelligence."),
    CHAMPION("Champion", "Strong and tough, grows in strength."),
    ELF("Elf", "Fast and creative, grows in agility.");

    private final String displayName;
    private final String description;

    HeroType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /** Factory: creates a new hero of this class. */
    public Hero create(String heroName) {
        switch (this) {
            case WIZARD:
                return new Wizard(heroName);
            case CHAMPION:
                return new Champion(heroName);
            case ELF:
                return new Elf(heroName);
            default:
                throw new IllegalStateException("No hero subclass for " + this);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /** Case-insensitive lookup by display name, used when reading data files. */
    public static HeroType fromName(String name) {
        for (HeroType type : values()) {
            if (type.displayName.equalsIgnoreCase(name.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown hero class '" + name + "'");
    }

    @Override
    public String toString() {
        return displayName;
    }
}

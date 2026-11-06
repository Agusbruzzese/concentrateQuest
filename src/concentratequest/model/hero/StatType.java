package concentratequest.model.hero;

/** The four base attributes, used to pick a single stat out of CharacterStats. */
public enum StatType {
    STRENGTH("Strength", "STR"),
    AGILITY("Agility", "AGI"),
    INTELLIGENCE("Intelligence", "INT"),
    STAMINA("Stamina", "STA");

    private final String displayName;
    private final String shortName;

    StatType(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }
}

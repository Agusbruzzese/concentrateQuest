package concentratequest.model.mission;

import java.util.List;

import concentratequest.model.hero.StatType;
import concentratequest.model.item.QuestItem;

/**
 * The kind of real-life task the player commits to. Each one trains a stat
 * and has its own quests: studying sends the hero to libraries, exercising
 * to mountains and arenas, and so on. Quests are grouped by length, and each
 * category has a chain of quest items: short quests find a material, long
 * quests turn two materials into a key, epic quests need that key.
 */
public enum TaskCategory {
    STUDY("Study", StatType.INTELLIGENCE,
            "came back with a notebook full of discoveries.",
            List.of("Find a lost book in the Old Library",
                    "Copy the scrolls of the Scriptorium",
                    "Read the star maps at the Observatory"),
            List.of("Research trip to the Sunken Archive",
                    "Decipher the runes of the Stone Circle"),
            List.of("Recover the Grimoire of the First Mage",
                    "Solve the riddle of the Sphinx"),
            "Ancient Page", "Scholar's Map"),
    WORK("Work", StatType.STAMINA,
            "came back tired but proud of the work done.",
            List.of("Help the blacksmith at the forge",
                    "Deliver the merchant's letters",
                    "Repair the village fences"),
            List.of("Escort the merchant caravan",
                    "Build the bridge over the river"),
            List.of("Rebuild the castle walls before winter",
                    "Lead the royal caravan across the desert"),
            "Iron Ingot", "Guild Seal"),
    EXERCISE("Exercise", StatType.STRENGTH,
            "came back stronger, covered in dust and glory.",
            List.of("Train at the arena",
                    "Run around the city walls",
                    "Spar with the town guards"),
            List.of("Climb the Eagle Mountain",
                    "Hunt the wolves of the Black Forest"),
            List.of("Defeat the Ogre of the Iron Pass",
                    "Reach the summit of the Dragon Peak"),
            "Climbing Rope", "Mountain Pass"),
    CREATIVE("Creative", StatType.AGILITY,
            "came back inspired, with something beautiful to show.",
            List.of("Paint the mural of the town square",
                    "Write a ballad at the tavern",
                    "Carve a statue in the workshop"),
            List.of("Seek inspiration in the Enchanted Valley",
                    "Compose the song of the Elven festival"),
            List.of("Craft a masterpiece for the King",
                    "Restore the legendary Crystal Harp"),
            "Rare Pigment", "Master's Sketch");

    private final String displayName;
    private final StatType trainedStat;
    private final String returnStory;
    private final List<String> shortQuests;
    private final List<String> longQuests;
    private final List<String> epicQuests;
    private final QuestItem material;
    private final QuestItem key;

    TaskCategory(String displayName, StatType trainedStat, String returnStory,
                 List<String> shortQuests, List<String> longQuests, List<String> epicQuests,
                 String materialName, String keyName) {
        this.displayName = displayName;
        this.trainedStat = trainedStat;
        this.returnStory = returnStory;
        this.shortQuests = shortQuests;
        this.longQuests = longQuests;
        this.epicQuests = epicQuests;
        this.material = new QuestItem(materialName,
                "Material for long " + displayName + " quests");
        this.key = new QuestItem(keyName,
                "Key for epic " + displayName + " quests");
    }

    public String getDisplayName() {
        return displayName;
    }

    public StatType getTrainedStat() {
        return trainedStat;
    }

    /** End of the sentence told when the hero comes back, e.g. "came back stronger...". */
    public String getReturnStory() {
        return returnStory;
    }

    public List<String> getShortQuests() {
        return shortQuests;
    }

    public List<String> getLongQuests() {
        return longQuests;
    }

    public List<String> getEpicQuests() {
        return epicQuests;
    }

    /** Found on short quests; long quests need two of them. */
    public QuestItem getMaterial() {
        return material;
    }

    /** Made on long quests; epic quests need it. */
    public QuestItem getKey() {
        return key;
    }
}

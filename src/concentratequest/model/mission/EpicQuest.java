package concentratequest.model.mission;

import java.time.Duration;
import java.util.List;

import concentratequest.model.item.Item;

/**
 * Needs 50 minutes or more and the key of its category, made on a long quest:
 * the most experience and loot above the hero's level.
 */
public class EpicQuest extends Mission {
    private static final long serialVersionUID = 1L;

    public EpicQuest(String title, String task, TaskCategory category,
                     int minutes, Duration realDuration) {
        super(title, task, category, minutes, realDuration);
    }

    @Override
    public String getKind() {
        return "Epic quest";
    }

    @Override
    public String getReturnStory(String heroName) {
        return heroName + " " + getCategory().getReturnStory() + " An epic deed: the treasures are rare.";
    }

    @Override
    protected double getExperienceMultiplier() {
        return 1.3;
    }

    @Override
    protected int getBonusLootRolls() {
        return 1;
    }

    /** Epic loot can be one level above the hero. */
    @Override
    protected int getLootLevelBonus() {
        return 1;
    }

    @Override
    public List<Item> getRequiredItems() {
        return List.of(getCategory().getKey());
    }
}

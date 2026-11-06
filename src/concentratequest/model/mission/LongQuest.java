package concentratequest.model.mission;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import concentratequest.model.item.Item;

/**
 * Needs 25 minutes or more and two materials of its category, found on short
 * quests: more experience, one extra item, and the key that epic quests need.
 */
public class LongQuest extends Mission {
    private static final long serialVersionUID = 1L;

    public static final int MATERIALS_NEEDED = 2;

    public LongQuest(String title, String task, TaskCategory category,
                     int minutes, Duration realDuration) {
        super(title, task, category, minutes, realDuration);
    }

    @Override
    public String getKind() {
        return "Long quest";
    }

    @Override
    public String getReturnStory(String heroName) {
        return heroName + " " + getCategory().getReturnStory() + " The journey was long, and the bag is heavier.";
    }

    @Override
    protected double getExperienceMultiplier() {
        return 1.1;
    }

    @Override
    protected int getBonusLootRolls() {
        return 1;
    }

    @Override
    public List<Item> getRequiredItems() {
        return Collections.nCopies(MATERIALS_NEEDED, getCategory().getMaterial());
    }

    @Override
    protected List<Item> getGuaranteedItems() {
        return List.of(getCategory().getKey());
    }
}

package concentratequest.model.mission;

import java.time.Duration;
import java.util.List;

import concentratequest.model.item.Item;

/** Available for any duration: plain rewards, and always one material of its category. */
public class ShortQuest extends Mission {
    private static final long serialVersionUID = 1L;

    public ShortQuest(String title, String task, TaskCategory category,
                      int minutes, Duration realDuration) {
        super(title, task, category, minutes, realDuration);
    }

    @Override
    public String getKind() {
        return "Short quest";
    }

    @Override
    public String getReturnStory(String heroName) {
        return heroName + " " + getCategory().getReturnStory();
    }

    @Override
    protected double getExperienceMultiplier() {
        return 1.0;
    }

    @Override
    protected int getBonusLootRolls() {
        return 0;
    }

    @Override
    protected List<Item> getGuaranteedItems() {
        return List.of(getCategory().getMaterial());
    }
}

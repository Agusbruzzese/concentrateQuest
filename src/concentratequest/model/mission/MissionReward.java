package concentratequest.model.mission;

import java.util.List;

import concentratequest.model.hero.StatType;
import concentratequest.model.item.Item;

/** What a hero brings back from a completed mission. Immutable. */
public final class MissionReward {
    private final int experience;
    private final List<Item> items;
    private final StatType trainedStat;
    private final int statGain;
    private final int gold;
    private final int healing;

    public MissionReward(int experience, List<Item> items, StatType trainedStat,
                         int statGain, int gold, int healing) {
        this.experience = experience;
        this.items = List.copyOf(items);
        this.trainedStat = trainedStat;
        this.statGain = statGain;
        this.gold = gold;
        this.healing = healing;
    }

    public int getExperience() {
        return experience;
    }

    public List<Item> getItems() {
        return items;
    }

    public StatType getTrainedStat() {
        return trainedStat;
    }

    public int getStatGain() {
        return statGain;
    }

    public int getGold() {
        return gold;
    }

    public int getHealing() {
        return healing;
    }
}

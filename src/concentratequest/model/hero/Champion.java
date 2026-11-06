package concentratequest.model.hero;

import java.util.List;

import concentratequest.model.item.ItemCatalog;
import concentratequest.model.mission.TaskCategory;

/** Gets the most out of EXERCISE sessions. */
public class Champion extends Hero {
    private static final long serialVersionUID = 1L;

    public Champion(String name) {
        super(name, new CharacterStats(90, 55, 35, 85),
                List.of(ItemCatalog.IRON_SWORD, ItemCatalog.WOODEN_SHIELD, ItemCatalog.PADDED_ARMOR),
                List.of(ItemCatalog.SMALL_POTION));
    }

    @Override
    public HeroType getType() {
        return HeroType.CHAMPION;
    }

    @Override
    public TaskCategory getAffinity() {
        return TaskCategory.EXERCISE;
    }

    @Override
    protected CharacterStats getLevelUpGrowth() {
        return new CharacterStats(4, 1, 0, 3);
    }
}

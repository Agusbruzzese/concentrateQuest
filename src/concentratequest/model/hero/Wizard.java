package concentratequest.model.hero;

import java.util.List;

import concentratequest.model.item.ItemCatalog;
import concentratequest.model.mission.TaskCategory;

/** Gets the most out of STUDY sessions. */
public class Wizard extends Hero {
    private static final long serialVersionUID = 1L;

    public Wizard(String name) {
        super(name, new CharacterStats(20, 40, 90, 35),
                List.of(ItemCatalog.WOODEN_STAFF, ItemCatalog.APPRENTICE_ROBE),
                List.of(ItemCatalog.SMALL_POTION));
    }

    @Override
    public HeroType getType() {
        return HeroType.WIZARD;
    }

    @Override
    public TaskCategory getAffinity() {
        return TaskCategory.STUDY;
    }

    @Override
    protected CharacterStats getLevelUpGrowth() {
        return new CharacterStats(1, 1, 4, 2);
    }
}

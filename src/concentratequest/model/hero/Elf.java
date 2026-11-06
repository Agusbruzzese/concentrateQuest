package concentratequest.model.hero;

import java.util.List;

import concentratequest.model.item.ItemCatalog;
import concentratequest.model.mission.TaskCategory;

/** Gets the most out of CREATIVE sessions. */
public class Elf extends Hero {
    private static final long serialVersionUID = 1L;

    public Elf(String name) {
        super(name, new CharacterStats(45, 90, 65, 50),
                List.of(ItemCatalog.SHORT_BOW, ItemCatalog.WOODEN_SHIELD, ItemCatalog.LEATHER_TUNIC),
                List.of(ItemCatalog.SMALL_POTION));
    }

    @Override
    public HeroType getType() {
        return HeroType.ELF;
    }

    @Override
    public TaskCategory getAffinity() {
        return TaskCategory.CREATIVE;
    }

    @Override
    protected CharacterStats getLevelUpGrowth() {
        return new CharacterStats(1, 4, 2, 1);
    }
}

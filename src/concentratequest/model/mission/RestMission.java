package concentratequest.model.mission;

import java.time.Duration;

import concentratequest.model.hero.Hero;

/**
 * The hero stays home and recovers while the player focuses. It is the only
 * choice for a knocked-out hero (0 HP): full healing, half experience, no loot.
 */
public class RestMission extends Mission {
    private static final long serialVersionUID = 1L;

    public RestMission(String task, TaskCategory category, int minutes, Duration realDuration) {
        super("Rest at home", task, category, minutes, realDuration);
    }

    @Override
    public String getKind() {
        return "Rest";
    }

    @Override
    public String getReturnStory(String heroName) {
        return heroName + " rested at home and is fully healed.";
    }

    @Override
    protected double getExperienceMultiplier() {
        return 0.5;
    }

    @Override
    protected int getBonusLootRolls() {
        return 0;
    }

    @Override
    protected boolean findsLoot() {
        return false;
    }

    @Override
    protected int getHealing(Hero hero) {
        return hero.getMaxHealth();
    }
}

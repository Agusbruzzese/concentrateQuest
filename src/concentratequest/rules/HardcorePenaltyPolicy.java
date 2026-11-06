package concentratequest.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import concentratequest.model.hero.Hero;
import concentratequest.model.item.Item;
import concentratequest.model.mission.Mission;

/** Like the Forest app: giving up really hurts. */
public class HardcorePenaltyPolicy implements PenaltyPolicy {
    private static final long serialVersionUID = 1L;

    private static final int HEALTH_LOSS = 30;
    private static final int XP_LOST_PER_MINUTE = 5;

    @Override
    public String getName() {
        return "Hardcore";
    }

    @Override
    public String getDescription() {
        return "Giving up costs " + HEALTH_LOSS + " HP, experience, a random item "
                + "from the backpack and the streak.";
    }

    @Override
    public List<String> apply(Hero hero, Mission mission, Random random) {
        List<String> consequences = new ArrayList<>();

        hero.takeDamage(HEALTH_LOSS);
        consequences.add(hero.getName() + " was wounded while running away (-" + HEALTH_LOSS + " HP).");

        int xpLost = mission.getMinutes() * XP_LOST_PER_MINUTE;
        hero.loseExperience(xpLost);
        consequences.add("Lost up to " + xpLost + " XP.");

        Optional<Item> lost = hero.loseRandomItem(random);
        if (lost.isPresent()) {
            consequences.add(lost.get().getName() + " was lost on the way back.");
        } else {
            consequences.add("The backpack was empty, nothing else was lost.");
        }
        return consequences;
    }
}

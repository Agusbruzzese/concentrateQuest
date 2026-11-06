package concentratequest.rules;

import java.util.List;
import java.util.Random;

import concentratequest.model.hero.Hero;
import concentratequest.model.mission.Mission;

/** The hero only comes back a bit hurt. Good for getting started. */
public class GentlePenaltyPolicy implements PenaltyPolicy {
    private static final long serialVersionUID = 1L;

    private static final int HEALTH_LOSS = 10;

    @Override
    public String getName() {
        return "Gentle";
    }

    @Override
    public String getDescription() {
        return "Giving up costs " + HEALTH_LOSS + " HP and the streak.";
    }

    @Override
    public List<String> apply(Hero hero, Mission mission, Random random) {
        hero.takeDamage(HEALTH_LOSS);
        return List.of(hero.getName() + " came back empty-handed and hurt (-" + HEALTH_LOSS + " HP).");
    }
}

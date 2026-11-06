package concentratequest.rules;

import java.util.List;
import java.util.Random;

import concentratequest.model.hero.Hero;
import concentratequest.model.mission.Mission;

/** Decides what the hero loses when the player gives up a focus session. */
public interface PenaltyPolicy extends Rule {

    /** Applies the penalty and returns a line of text for each consequence. */
    List<String> apply(Hero hero, Mission mission, Random random);
}

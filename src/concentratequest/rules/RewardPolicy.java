package concentratequest.rules;

/** Decides how generous a completed focus session is. */
public interface RewardPolicy extends Rule {

    /** Experience for a session, before the mission and affinity multipliers. */
    int experienceFor(int minutes, int streak);

    /** How many random items the hero brings back. */
    int lootRollsFor(int minutes, int streak);
}

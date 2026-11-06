package concentratequest.rules;

/** Rewards grow linearly with the time spent focusing. */
public class StandardRewardPolicy implements RewardPolicy {
    private static final long serialVersionUID = 1L;

    public static final int XP_PER_MINUTE = 10;
    public static final int MINUTES_PER_LOOT_ROLL = 30;

    @Override
    public String getName() {
        return "Standard";
    }

    @Override
    public String getDescription() {
        return XP_PER_MINUTE + " XP per minute, one more item every "
                + MINUTES_PER_LOOT_ROLL + " minutes.";
    }

    @Override
    public int experienceFor(int minutes, int streak) {
        return minutes * XP_PER_MINUTE;
    }

    @Override
    public int lootRollsFor(int minutes, int streak) {
        return 1 + minutes / MINUTES_PER_LOOT_ROLL;
    }
}

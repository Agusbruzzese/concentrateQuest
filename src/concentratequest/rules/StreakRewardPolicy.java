package concentratequest.rules;

/** Like the standard rule, but rewards being consistent day after day. */
public class StreakRewardPolicy extends StandardRewardPolicy {
    private static final long serialVersionUID = 1L;

    private static final double BONUS_PER_MISSION = 0.10;
    private static final double MAX_MULTIPLIER = 2.0;
    private static final int STREAK_FOR_EXTRA_ITEM = 5;

    @Override
    public String getName() {
        return "Streak";
    }

    @Override
    public String getDescription() {
        return "Standard, plus 10% XP for every mission in a row without giving up "
                + "(up to x2) and one more item from a streak of " + STREAK_FOR_EXTRA_ITEM + ".";
    }

    /** The int streak is widened to double in the multiplication (coercion). */
    @Override
    public int experienceFor(int minutes, int streak) {
        double multiplier = Math.min(MAX_MULTIPLIER, 1 + streak * BONUS_PER_MISSION);
        return (int) Math.round(super.experienceFor(minutes, streak) * multiplier);
    }

    @Override
    public int lootRollsFor(int minutes, int streak) {
        int extra = streak >= STREAK_FOR_EXTRA_ITEM ? 1 : 0;
        return super.lootRollsFor(minutes, streak) + extra;
    }
}

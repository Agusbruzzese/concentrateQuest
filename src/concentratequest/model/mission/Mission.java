package concentratequest.model.mission;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import concentratequest.exception.InvalidMissionStateException;
import concentratequest.model.hero.Hero;
import concentratequest.model.item.Item;
import concentratequest.model.item.ItemCatalog;
import concentratequest.rules.RewardPolicy;

/**
 * The hero goes away for as long as the player committed to focus.
 * The state can only change through start, complete and fail, and only in
 * the right order (see MissionState).
 */
public abstract class Mission implements Serializable {
    private static final long serialVersionUID = 1L;

    /** XP multiplier when the task matches the hero's affinity (plus one extra item). */
    public static final double AFFINITY_XP_BONUS = 1.25;
    /** Minutes of focus needed for one permanent stat point. */
    public static final int MINUTES_PER_STAT_POINT = 25;
    /** Coins found for every minute of focus. */
    public static final int GOLD_PER_MINUTE = 1;

    private final String title;
    private final String task;
    private final TaskCategory category;
    private final int minutes;
    private final Duration realDuration;
    private MissionState state = MissionState.PLANNED;
    private Instant startedAt;

    /**
     * @param minutes      the focus time the player committed to (rewards use this)
     * @param realDuration how long the mission really lasts: the same as
     *                     minutes, except in demo mode where it is shorter
     */
    protected Mission(String title, String task, TaskCategory category,
                      int minutes, Duration realDuration) {
        if (minutes < 1) {
            throw new IllegalArgumentException("A mission lasts at least one minute");
        }
        if (realDuration.isZero() || realDuration.isNegative()) {
            throw new IllegalArgumentException("The duration must be positive");
        }
        this.title = title;
        this.task = task;
        this.category = category;
        this.minutes = minutes;
        this.realDuration = realDuration;
    }

    /** Overload for normal play: the mission lasts exactly the committed minutes. */
    protected Mission(String title, String task, TaskCategory category, int minutes) {
        this(title, task, category, minutes, Duration.ofMinutes(minutes));
    }

    // ---------- what each kind of mission defines ----------

    public abstract String getKind();

    /** The little story told when the hero comes back. */
    public abstract String getReturnStory(String heroName);

    protected abstract double getExperienceMultiplier();

    protected abstract int getBonusLootRolls();

    /** Hook with a default: how many levels above the hero the loot can be. */
    protected int getLootLevelBonus() {
        return 0;
    }

    /** Hook with a default: false for missions that bring back no items and no gold. */
    protected boolean findsLoot() {
        return true;
    }

    /** Hook with a default: health recovered on return, one point per minute. */
    protected int getHealing(Hero hero) {
        return minutes;
    }

    /** Hook with a default: items the hero must carry; the quest uses them up when it starts. */
    public List<Item> getRequiredItems() {
        return List.of();
    }

    /** Hook with a default: items always brought back, besides the random loot. */
    protected List<Item> getGuaranteedItems() {
        return List.of();
    }

    /** The requirement as text, e.g. "2 x Ancient Page"; empty if nothing is needed. */
    public String describeRequiredItems() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Item item : getRequiredItems()) {
            counts.put(item.getName(), counts.getOrDefault(item.getName(), 0) + 1);
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            parts.add(entry.getValue() + " x " + entry.getKey());
        }
        return String.join(", ", parts);
    }

    // ---------- life cycle ----------

    public void start(Instant now) {
        requireState(MissionState.PLANNED, "start");
        startedAt = now;
        state = MissionState.IN_PROGRESS;
    }

    public void complete(Instant now) {
        requireState(MissionState.IN_PROGRESS, "complete");
        if (!isTimeUp(now)) {
            throw new InvalidMissionStateException("The mission is not over yet");
        }
        state = MissionState.COMPLETED;
    }

    /** The player gave up (or closed the app) before the time was over. */
    public void fail() {
        requireState(MissionState.IN_PROGRESS, "fail");
        state = MissionState.FAILED;
    }

    private void requireState(MissionState expected, String action) {
        if (state != expected) {
            throw new InvalidMissionStateException(
                    "Cannot " + action + " a mission that is " + state);
        }
    }

    // ---------- time ----------

    public Duration getRemaining(Instant now) {
        if (startedAt == null) {
            return realDuration;
        }
        Duration left = realDuration.minus(Duration.between(startedAt, now));
        return left.isNegative() ? Duration.ZERO : left;
    }

    public boolean isTimeUp(Instant now) {
        return startedAt != null && getRemaining(now).isZero();
    }

    /** From 0.0 (just left) to 1.0 (time over). */
    public double getProgress(Instant now) {
        if (startedAt == null) {
            return 0.0;
        }
        double elapsed = Duration.between(startedAt, now).toMillis(); // long widened to double
        return Math.min(1.0, elapsed / realDuration.toMillis());
    }

    // ---------- reward ----------

    /**
     * Template method: the steps are always the same and cannot be overridden
     * (final), while the numbers come from the subclass hooks and the policy.
     */
    public final MissionReward computeReward(Hero hero, RewardPolicy policy, Random random) {
        requireState(MissionState.COMPLETED, "reward");
        boolean affinity = hero.getAffinity() == category;

        int streak = hero.getRecord().getStreak();
        double experience = policy.experienceFor(minutes, streak) * getExperienceMultiplier();
        int lootRolls = findsLoot() ? policy.lootRollsFor(minutes, streak) + getBonusLootRolls() : 0;
        if (affinity) {
            experience *= AFFINITY_XP_BONUS;
            if (findsLoot()) {
                lootRolls++;
            }
        }

        List<Item> loot = new ArrayList<>();
        if (findsLoot()) {
            loot.addAll(getGuaranteedItems());
        }
        for (int i = 0; i < lootRolls; i++) {
            loot.add(ItemCatalog.randomLoot(hero.getLevel() + getLootLevelBonus(), random));
        }
        int statGain = Math.max(1, minutes / MINUTES_PER_STAT_POINT);
        int gold = findsLoot() ? minutes * GOLD_PER_MINUTE : 0;
        return new MissionReward((int) Math.round(experience), loot,
                category.getTrainedStat(), statGain, gold, getHealing(hero));
    }

    // ---------- getters ----------

    public String getTitle() {
        return title;
    }

    public String getTask() {
        return task;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public int getMinutes() {
        return minutes;
    }

    public Duration getRealDuration() {
        return realDuration;
    }

    public MissionState getState() {
        return state;
    }

    @Override
    public String toString() {
        return title + " [" + getKind() + ", " + minutes + " min]";
    }
}

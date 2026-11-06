package concentratequest.game;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import concentratequest.rules.GentlePenaltyPolicy;
import concentratequest.rules.HardcorePenaltyPolicy;
import concentratequest.rules.PenaltyPolicy;
import concentratequest.rules.RewardPolicy;
import concentratequest.rules.StandardRewardPolicy;
import concentratequest.rules.StreakRewardPolicy;

/** The rules chosen by the player. A new rule only has to be added to a list here. */
public class GameSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final List<RewardPolicy> REWARD_POLICIES =
            List.of(new StandardRewardPolicy(), new StreakRewardPolicy());
    public static final List<PenaltyPolicy> PENALTY_POLICIES =
            List.of(new GentlePenaltyPolicy(), new HardcorePenaltyPolicy());

    private RewardPolicy rewardPolicy = REWARD_POLICIES.get(0);
    private PenaltyPolicy penaltyPolicy = PENALTY_POLICIES.get(0);
    private boolean demoMode;
    private boolean brownNoise = true;

    public RewardPolicy getRewardPolicy() {
        return rewardPolicy;
    }

    public void setRewardPolicy(RewardPolicy rewardPolicy) {
        this.rewardPolicy = Objects.requireNonNull(rewardPolicy);
    }

    public PenaltyPolicy getPenaltyPolicy() {
        return penaltyPolicy;
    }

    public void setPenaltyPolicy(PenaltyPolicy penaltyPolicy) {
        this.penaltyPolicy = Objects.requireNonNull(penaltyPolicy);
    }

    /** In demo mode one minute of mission lasts one second, to show the game quickly. */
    public boolean isDemoMode() {
        return demoMode;
    }

    public void setDemoMode(boolean demoMode) {
        this.demoMode = demoMode;
    }

    /** Brown noise played by the focus window during a quest. */
    public boolean isBrownNoise() {
        return brownNoise;
    }

    public void setBrownNoise(boolean brownNoise) {
        this.brownNoise = brownNoise;
    }

    public Duration realDurationFor(int minutes) {
        return demoMode ? Duration.ofSeconds(minutes) : Duration.ofMinutes(minutes);
    }
}

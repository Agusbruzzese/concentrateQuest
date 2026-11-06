package concentratequest.model.hero;

import java.io.Serializable;

/**
 * The real-life focus history of the player. Only the hero (same package)
 * can change it; everybody else gets the read-only getters.
 */
public class FocusRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int completedMissions;
    private int failedMissions;
    private int totalFocusMinutes;
    private int streak;
    private int bestStreak;

    void registerSuccess(int minutes) {
        completedMissions++;
        totalFocusMinutes += minutes;
        streak++;
        bestStreak = Math.max(bestStreak, streak);
    }

    void registerFailure() {
        failedMissions++;
        streak = 0;
    }

    public int getCompletedMissions() {
        return completedMissions;
    }

    public int getFailedMissions() {
        return failedMissions;
    }

    public int getTotalFocusMinutes() {
        return totalFocusMinutes;
    }

    /** Missions completed in a row without giving up. */
    public int getStreak() {
        return streak;
    }

    public int getBestStreak() {
        return bestStreak;
    }
}

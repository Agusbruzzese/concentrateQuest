package concentratequest.game;

import java.time.Duration;
import java.util.List;

import concentratequest.model.mission.Mission;
import concentratequest.model.mission.MissionReward;

/**
 * Observer of the missions. The controller does not know who is listening
 * (console, GUI, achievements...): it just notifies every listener.
 */
public interface MissionListener {

    void missionStarted(Mission mission);

    /** Called regularly while the hero is away. Optional, so it has a default. */
    default void missionProgress(Mission mission, double progress, Duration remaining) {
    }

    void missionCompleted(Mission mission, MissionReward reward, List<String> events);

    void missionFailed(Mission mission, List<String> consequences);
}

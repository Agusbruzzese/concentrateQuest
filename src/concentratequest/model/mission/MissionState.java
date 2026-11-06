package concentratequest.model.mission;

/**
 * Life cycle of a mission:
 * PLANNED -> IN_PROGRESS -> COMPLETED (time over) or FAILED (player gave up).
 */
public enum MissionState {
    PLANNED, IN_PROGRESS, COMPLETED, FAILED;

    public boolean isFinished() {
        return this == COMPLETED || this == FAILED;
    }
}

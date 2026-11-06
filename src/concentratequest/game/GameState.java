package concentratequest.game;

import java.io.Serializable;
import java.util.Optional;

import concentratequest.model.hero.Hero;
import concentratequest.model.mission.Mission;

/** Everything that is saved to disk: the hero, the settings and the running mission. */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Hero hero;
    private final GameSettings settings = new GameSettings();
    // volatile: the timer thread reads it while the main thread may change it
    private volatile Mission activeMission;

    public GameState(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public Optional<Mission> getActiveMission() {
        return Optional.ofNullable(activeMission);
    }

    // Only the MissionController (same package) starts and ends missions
    void setActiveMission(Mission mission) {
        this.activeMission = mission;
    }

    void clearActiveMission() {
        this.activeMission = null;
    }
}

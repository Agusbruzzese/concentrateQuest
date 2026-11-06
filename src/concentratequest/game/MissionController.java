package concentratequest.game;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import concentratequest.exception.InvalidMissionStateException;
import concentratequest.exception.InventoryFullException;
import concentratequest.exception.MissingItemsException;
import concentratequest.exception.SaveException;
import concentratequest.model.hero.Hero;
import concentratequest.model.item.Item;
import concentratequest.model.mission.Mission;
import concentratequest.model.mission.MissionReward;
import concentratequest.persistence.SaveManager;

/**
 * Runs the focus sessions: starts a mission, ends it with a reward when the
 * time is over or with a penalty when the player gives up, saves the game
 * and tells the listeners. Contains no input/output, so any UI can use it.
 */
public class MissionController {
    private final GameState state;
    private final SaveManager saveManager;
    private final Clock clock;
    private final Random random;
    // Thread-safe list: the timer thread notifies while the main thread may add listeners
    private final List<MissionListener> listeners = new CopyOnWriteArrayList<>();

    /** The clock and the random generator are passed in, so tests can control them. */
    public MissionController(GameState state, SaveManager saveManager, Clock clock, Random random) {
        this.state = state;
        this.saveManager = saveManager;
        this.clock = clock;
        this.random = random;
    }

    /** Overload for normal play: real clock and real randomness. */
    public MissionController(GameState state, SaveManager saveManager) {
        this(state, saveManager, Clock.systemUTC(), new Random());
    }

    public void addListener(MissionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MissionListener listener) {
        listeners.remove(listener);
    }

    public Instant now() {
        return clock.instant();
    }

    public Optional<Mission> getActiveMission() {
        return state.getActiveMission();
    }

    /** The required items are used up here: if the player gives up, they are lost. */
    public void start(Mission mission) throws SaveException, MissingItemsException {
        if (state.getActiveMission().isPresent()) {
            throw new InvalidMissionStateException("The hero is already on a mission");
        }
        state.getHero().useUpItems(mission.getRequiredItems());
        mission.start(now());
        state.setActiveMission(mission);
        saveManager.save(state); // from now on, closing the app counts as leaving early
        for (MissionListener listener : listeners) {
            listener.missionStarted(mission);
        }
    }

    /** Sends the current progress to the listeners. Called by the timer. */
    public void tick() {
        Optional<Mission> active = state.getActiveMission();
        if (active.isEmpty()) {
            return;
        }
        Mission mission = active.get();
        Instant now = now();
        for (MissionListener listener : listeners) {
            listener.missionProgress(mission, mission.getProgress(now), mission.getRemaining(now));
        }
    }

    public boolean isTimeUp() {
        return activeOrThrow().isTimeUp(now());
    }

    /** The time is over: the hero comes back with the reward. */
    public MissionReward finish() throws SaveException {
        Mission mission = activeOrThrow();
        mission.complete(now());
        Hero hero = state.getHero();

        MissionReward reward = mission.computeReward(hero, state.getSettings().getRewardPolicy(), random);
        List<String> events = applyReward(hero, mission, reward);
        hero.registerCompletedMission(mission.getMinutes());
        endMission();

        for (MissionListener listener : listeners) {
            listener.missionCompleted(mission, reward, events);
        }
        return reward;
    }

    /** The player gave up before the time was over. */
    public void giveUp() throws SaveException {
        giveUp(null);
    }

    /** Overload that also explains why the mission failed. */
    public void giveUp(String reason) throws SaveException {
        Mission mission = activeOrThrow();
        mission.fail();
        Hero hero = state.getHero();

        List<String> consequences = new ArrayList<>();
        if (reason != null) {
            consequences.add(reason);
        }
        consequences.addAll(state.getSettings().getPenaltyPolicy().apply(hero, mission, random));
        hero.registerFailedMission();
        consequences.add("The focus streak is back to 0.");
        endMission();

        for (MissionListener listener : listeners) {
            listener.missionFailed(mission, consequences);
        }
    }

    /**
     * Called at startup. A mission still running in the save file means the
     * app was closed: it counts as giving up, unless the time was already over.
     */
    public void resolveInterruptedMission() throws SaveException {
        Optional<Mission> active = state.getActiveMission();
        if (active.isEmpty()) {
            return;
        }
        if (active.get().isTimeUp(now())) {
            finish();
        } else {
            giveUp("The app was closed before the mission was over.");
        }
    }

    private List<String> applyReward(Hero hero, Mission mission, MissionReward reward) {
        List<String> events = new ArrayList<>();

        int levels = hero.gainExperience(reward.getExperience());
        if (levels > 0) {
            events.add(hero.getName() + " reached level " + hero.getLevel() + "! +"
                    + levels * Hero.STAT_POINTS_PER_LEVEL + " free stat points.");
        }
        hero.train(reward.getTrainedStat(), reward.getStatGain());
        hero.heal(reward.getHealing());
        hero.earnGold(reward.getGold());

        for (Item item : reward.getItems()) {
            try {
                hero.addItem(item);
            } catch (InventoryFullException e) {
                events.add(e.getMessage() + " It was left behind.");
            }
        }
        return events;
    }

    private void endMission() throws SaveException {
        state.clearActiveMission();
        saveManager.save(state);
    }

    private Mission activeOrThrow() {
        Optional<Mission> active = state.getActiveMission();
        if (!active.isPresent()) {
            throw new InvalidMissionStateException("The hero is not on a mission");
        }
        return active.get();
    }
}

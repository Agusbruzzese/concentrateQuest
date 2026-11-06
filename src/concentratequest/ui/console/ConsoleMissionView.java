package concentratequest.ui.console;

import java.time.Duration;
import java.util.List;

import concentratequest.game.MissionListener;
import concentratequest.model.item.Item;
import concentratequest.model.mission.Mission;
import concentratequest.model.mission.MissionReward;

/** Shows the missions on the console. It only reacts to what the controller tells it. */
public class ConsoleMissionView implements MissionListener {
    private static final int BAR_WIDTH = 30;

    private final String heroName;
    // written by the timer thread, read by the main thread
    private volatile boolean returnAnnounced;

    public ConsoleMissionView(String heroName) {
        this.heroName = heroName;
    }

    @Override
    public void missionStarted(Mission mission) {
        returnAnnounced = false;
        System.out.println();
        System.out.println("=== " + mission.getTitle() + " ===");
        System.out.println(heroName + " has left. Now focus on: " + mission.getTask());
        System.out.println("The hero will be back in " + ConsoleFormat.time(mission.getRealDuration()) + ".");
    }

    @Override
    public void missionProgress(Mission mission, double progress, Duration remaining) {
        if (remaining.isZero()) {
            if (!returnAnnounced) {
                returnAnnounced = true;
                System.out.println();
                System.out.println("*** " + heroName + " is back! Press ENTER to open the bag. ***");
            }
            return;
        }
        System.out.println("  " + ConsoleFormat.bar(progress, BAR_WIDTH) + " "
                + Math.round(progress * 100) + "%  " + ConsoleFormat.time(remaining) + " left");
    }

    @Override
    public void missionCompleted(Mission mission, MissionReward reward, List<String> events) {
        System.out.println();
        System.out.println("=== Mission complete: " + mission.getTitle() + " ===");
        System.out.println(mission.getReturnStory(heroName));
        System.out.println("  +" + reward.getExperience() + " XP");
        System.out.println("  +" + reward.getStatGain() + " " + reward.getTrainedStat().getDisplayName()
                + " (" + mission.getMinutes() + " minutes of " + mission.getCategory().getDisplayName() + ")");
        System.out.println("  +" + reward.getHealing() + " HP");
        if (reward.getGold() > 0) {
            System.out.println("  +" + reward.getGold() + " gold");
        }
        for (Item item : reward.getItems()) {
            System.out.println("  Found: " + item.describe());
        }
        for (String event : events) {
            System.out.println("  " + event);
        }
    }

    @Override
    public void missionFailed(Mission mission, List<String> consequences) {
        System.out.println();
        System.out.println("=== Mission failed: " + mission.getTitle() + " ===");
        System.out.println(heroName + " was called back before the end.");
        for (String consequence : consequences) {
            System.out.println("  " + consequence);
        }
    }
}

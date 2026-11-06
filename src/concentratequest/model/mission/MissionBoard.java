package concentratequest.model.mission;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import concentratequest.model.hero.Hero;

/**
 * Offers the missions available for a focus session. The kind of task decides
 * the theme (library, mountain, forge...), the time committed decides how
 * epic the quest is. A hurt hero can also rest; a knocked-out one can only rest.
 */
public class MissionBoard {
    public static final int LONG_QUEST_MIN_MINUTES = 25;
    public static final int EPIC_QUEST_MIN_MINUTES = 50;

    private final Random random;

    public MissionBoard(Random random) {
        this.random = random;
    }

    public List<Mission> offers(Hero hero, String task, TaskCategory category,
                                int minutes, Duration realDuration) {
        List<Mission> offers = new ArrayList<>();
        if (!hero.isKnockedOut()) {
            offers.add(new ShortQuest(pick(category.getShortQuests()),
                    task, category, minutes, realDuration));
            if (minutes >= LONG_QUEST_MIN_MINUTES) {
                offers.add(new LongQuest(pick(category.getLongQuests()),
                        task, category, minutes, realDuration));
            }
            if (minutes >= EPIC_QUEST_MIN_MINUTES) {
                offers.add(new EpicQuest(pick(category.getEpicQuests()),
                        task, category, minutes, realDuration));
            }
        }
        if (hero.getHealth() < hero.getMaxHealth()) {
            offers.add(new RestMission(task, category, minutes, realDuration));
        }
        return offers;
    }

    private String pick(List<String> options) {
        return options.get(random.nextInt(options.size()));
    }
}

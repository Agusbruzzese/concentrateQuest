package concentratequest.ui.swing;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import concentratequest.game.MissionController;
import concentratequest.game.MissionListener;
import concentratequest.model.item.Item;
import concentratequest.model.mission.Mission;
import concentratequest.model.mission.MissionReward;
import concentratequest.ui.console.ConsoleFormat;

/**
 * The quest window, built only with JOptionPane dialogs as in the textbook's
 * first GUI examples (Deitel and Deitel, section 3.6): during the quest a
 * dialog shows the time left and a "Give up" button; at the end a message
 * dialog shows the result.
 */
public class QuestDialog implements MissionListener {
    private static final String GIVE_UP = "Give up";

    private final MissionController controller;

    public QuestDialog(MissionController controller) {
        this.controller = controller;
    }

    /**
     * Shows the dialog until the time is over or the player gives up.
     * Returns true if the quest was completed, false if the player gave up.
     */
    public boolean waitForQuest(Mission mission) {
        JLabel counter = new JLabel(counterText(mission));
        counter.setFont(counter.getFont().deriveFont(Font.BOLD, 28f));

        // A JOptionPane object instead of the static method, so the timer can close it
        Object[] message = {mission.getTitle(), "Focus on: " + mission.getTask(), counter};
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, new Object[] {GIVE_UP});
        JDialog dialog = pane.createDialog("Concentrate Quest");
        dialog.setAlwaysOnTop(true);

        // Every second: update the counter, and close the dialog when the time is over
        // (anonymous inner class: an ActionListener written on the spot)
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                counter.setText(counterText(mission));
                if (controller.isTimeUp()) {
                    dialog.dispose();
                }
            }
        });

        while (true) {
            pane.setValue(JOptionPane.UNINITIALIZED_VALUE); // so "Give up" works again after a "No"
            timer.start();
            dialog.setVisible(true); // waits here until the button, the close box or the timer closes it
            timer.stop();
            if (controller.isTimeUp()) {
                return true;
            }
            int answer = JOptionPane.showConfirmDialog(null,
                    "Closing this window ends the quest and you get the penalty.\nGive up?",
                    "Give up the quest?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                return controller.isTimeUp(); // the time may have run out while the question was open
            }
        }
    }

    private String counterText(Mission mission) {
        Instant now = controller.now();
        return "Time left: " + ConsoleFormat.time(mission.getRemaining(now))
                + "   (" + Math.round(mission.getProgress(now) * 100) + "%)";
    }

    // ---------- MissionListener: the result in a message dialog ----------

    @Override
    public void missionStarted(Mission mission) {
        // the dialog is opened by waitForQuest
    }

    @Override
    public void missionCompleted(Mission mission, MissionReward reward, List<String> events) {
        List<String> lines = new ArrayList<>();
        lines.add("Quest complete: " + mission.getTitle());
        lines.add("+" + reward.getExperience() + " XP, +" + reward.getGold() + " gold, +"
                + reward.getStatGain() + " " + reward.getTrainedStat().getDisplayName());
        for (Item item : reward.getItems()) {
            lines.add("Found: " + item.getName());
        }
        lines.addAll(events);
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, String.join("\n", lines),
                "The hero is back!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void missionFailed(Mission mission, List<String> consequences) {
        JOptionPane.showMessageDialog(null, "Quest failed: " + mission.getTitle() + "\n"
                + String.join("\n", consequences), "Quest failed", JOptionPane.WARNING_MESSAGE);
    }
}

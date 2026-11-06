package concentratequest;

import java.util.Arrays;

import concentratequest.ui.console.ConsoleGame;

public class Main {

    /** Run with --demo to make one minute of mission last one second. */
    public static void main(String[] args) {
        boolean demoMode = Arrays.asList(args).contains("--demo");
        new ConsoleGame(demoMode).run();
        System.exit(0); // also stops the Swing thread if the focus window was used
    }
}

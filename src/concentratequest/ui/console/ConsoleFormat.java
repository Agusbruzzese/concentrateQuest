package concentratequest.ui.console;

import java.time.Duration;

/** Small text helpers: status bars and times (the time format is also used by the focus window). */
public final class ConsoleFormat {

    private ConsoleFormat() {
    }

    /** A bar like [#####-----] for a fraction between 0 and 1. */
    static String bar(double fraction, int width) {
        int filled = (int) Math.round(Math.max(0, Math.min(1, fraction)) * width);
        return "[" + "#".repeat(filled) + "-".repeat(width - filled) + "]";
    }

    /** Overload for whole numbers: the cast makes the division happen in double. */
    static String bar(int value, int max, int width) {
        return bar(max == 0 ? 0 : (double) value / max, width);
    }

    /** 1:05:09 or 05:09 */
    public static String time(Duration duration) {
        long seconds = (duration.toMillis() + 999) / 1000; // round up: never show 00:00 too early
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return hours > 0
                ? String.format("%d:%02d:%02d", hours, minutes, secs)
                : String.format("%02d:%02d", minutes, secs);
    }
}

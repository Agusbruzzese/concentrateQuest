package concentratequest.ui.console;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Reads and validates what the player types. Keeps asking until the input is valid. */
public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(InputStream in) {
        this.scanner = new Scanner(in);
    }

    /** Throws NoSuchElementException if the input is closed (Ctrl+D). */
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Please write something.");
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please choose a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("'" + line + "' is not a number.");
            }
        }
    }

    public boolean confirm(String question) {
        String answer = readLine(question + " (y/n) ");
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }

    /**
     * Numbered menu with a "Back" option: empty if the player goes back.
     * labels.get(i) is the text shown for options.get(i).
     */
    public <T> Optional<T> choose(String title, List<T> options, List<String> labels) {
        printOptions(title, labels);
        System.out.println("  0. Back");
        int choice = readInt("> ", 0, options.size());
        return choice == 0 ? Optional.empty() : Optional.of(options.get(choice - 1));
    }

    /** Overload that shows each option with its toString(). */
    public <T> Optional<T> choose(String title, List<T> options) {
        List<String> labels = new ArrayList<>();
        for (T option : options) {
            labels.add(option.toString());
        }
        return choose(title, options, labels);
    }

    /** Numbered menu where the player must pick something. */
    public <T> T chooseOne(String title, List<T> options, List<String> labels) {
        printOptions(title, labels);
        return options.get(readInt("> ", 1, options.size()) - 1);
    }

    private void printOptions(String title, List<String> labels) {
        System.out.println(title);
        for (int i = 0; i < labels.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + labels.get(i));
        }
    }
}

package concentratequest.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import concentratequest.exception.InvalidDataException;
import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.HeroType;
import concentratequest.model.hero.StatType;
import concentratequest.model.item.Clothes;
import concentratequest.model.item.Elixir;
import concentratequest.model.item.Item;
import concentratequest.model.item.Potion;
import concentratequest.model.item.Ring;
import concentratequest.model.item.Scroll;
import concentratequest.model.item.Shield;
import concentratequest.model.item.Weapon;

/**
 * Reads extra items from a text file, so new items can be added without
 * touching the code. One item per line, fields separated by ';':
 *   KIND;Name;minLevel;value;requiredSTR,AGI,INT,STA;Class,Class
 * The value depends on the kind: damage (WEAPON), defense (SHIELD, CLOTHES),
 * health (POTION), experience (SCROLL), a bonus STR,AGI,INT,STA (RING) or
 * STAT:amount (ELIXIR). The last two fields are optional; # starts a comment.
 */
public class CustomItemLoader {

    /** Builds the item of the given kind from the already-parsed common fields. */
    private static Item createItem(String kind, String name, int level, String value,
                                   CharacterStats required, HeroType[] classes) {
        switch (kind) {
            case "WEAPON":
                return new Weapon(name, level, Integer.parseInt(value), required, classes);
            case "SHIELD":
                return new Shield(name, level, Integer.parseInt(value), required, classes);
            case "CLOTHES":
                return new Clothes(name, level, Integer.parseInt(value), required, classes);
            case "RING":
                return new Ring(name, level, parseStats(value), required, classes);
            case "POTION":
                return new Potion(name, level, Integer.parseInt(value));
            case "SCROLL":
                return new Scroll(name, level, Integer.parseInt(value));
            case "ELIXIR":
                String[] parts = value.split(":");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("an elixir value looks like STRENGTH:2");
                }
                return new Elixir(name, level, StatType.valueOf(parts[0].trim().toUpperCase()),
                        Integer.parseInt(parts[1].trim()));
            default:
                return null; // unknown kind, checked by the caller
        }
    }

    /** Returns no items if the file does not exist: custom items are optional. */
    public List<Item> load(Path file) throws IOException, InvalidDataException {
        List<Item> items = new ArrayList<>();
        if (!Files.exists(file)) {
            return items;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    items.add(parseLine(line, lineNumber));
                }
            }
        }
        return items;
    }

    private Item parseLine(String line, int lineNumber) throws InvalidDataException {
        String[] fields = line.split(";", -1);
        if (fields.length < 4) {
            throw new InvalidDataException(lineNumber,
                    "expected at least 4 fields separated by ';', found " + fields.length);
        }
        String kind = fields[0].trim().toUpperCase();
        try {
            String name = fields[1].trim();
            int level = Integer.parseInt(fields[2].trim());
            CharacterStats required = fields.length > 4 && !fields[4].isBlank()
                    ? parseStats(fields[4]) : CharacterStats.ZERO;
            HeroType[] classes = fields.length > 5 && !fields[5].isBlank()
                    ? parseClasses(fields[5]) : new HeroType[0];
            Item item = createItem(kind, name, level, fields[3].trim(), required, classes);
            if (item == null) {
                throw new InvalidDataException(lineNumber, "unknown item kind '" + fields[0].trim() + "'");
            }
            return item;
        } catch (IllegalArgumentException e) { // NumberFormatException is a subclass of it
            throw new InvalidDataException(lineNumber, e.getMessage(), e);
        }
    }

    private static CharacterStats parseStats(String text) {
        String[] parts = text.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("stats need 4 numbers: STR,AGI,INT,STA");
        }
        return new CharacterStats(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim()));
    }

    private static HeroType[] parseClasses(String text) {
        String[] names = text.split(",");
        HeroType[] classes = new HeroType[names.length];
        for (int i = 0; i < names.length; i++) {
            classes[i] = HeroType.fromName(names[i]);
        }
        return classes;
    }
}

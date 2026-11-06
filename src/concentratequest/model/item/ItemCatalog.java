package concentratequest.model.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.Hero;
import concentratequest.model.hero.StatType;

import static concentratequest.model.hero.HeroType.CHAMPION;
import static concentratequest.model.hero.HeroType.ELF;
import static concentratequest.model.hero.HeroType.WIZARD;

/**
 * Every item in the game, grouped by the level that unlocks it.
 * Used for starter gear and mission loot. More items can be registered at
 * startup from a data file (see CustomItemLoader).
 */
public final class ItemCatalog {

    // Helper to keep the requirements short: str, agi, int, sta
    private static CharacterStats req(int str, int agi, int intel, int sta) {
        return new CharacterStats(str, agi, intel, sta);
    }

    private static final CharacterStats NONE = CharacterStats.ZERO;

    // ---------- LEVEL 1 ----------
    public static final Weapon RUSTY_DAGGER   = new Weapon("Rusty Dagger", 1, 3, NONE);
    public static final Weapon WOODEN_STAFF   = new Weapon("Wooden Staff", 1, 5, req(0, 0, 40, 0), WIZARD);
    public static final Weapon IRON_SWORD     = new Weapon("Iron Sword", 1, 8, req(40, 0, 0, 0), CHAMPION);
    public static final Weapon SHORT_BOW      = new Weapon("Short Bow", 1, 6, req(0, 40, 0, 0), ELF);
    public static final Shield WOODEN_SHIELD  = new Shield("Wooden Shield", 1, 3, req(30, 0, 0, 0), CHAMPION, ELF);
    public static final Clothes APPRENTICE_ROBE = new Clothes("Apprentice Robe", 1, 2, req(0, 0, 30, 0), WIZARD);
    public static final Clothes LEATHER_TUNIC = new Clothes("Leather Tunic", 1, 3, req(0, 30, 0, 0), ELF);
    public static final Clothes PADDED_ARMOR  = new Clothes("Padded Armor", 1, 4, req(30, 0, 0, 0), CHAMPION);
    public static final Potion SMALL_POTION   = new Potion("Small Health Potion", 1, 30);
    public static final Ring COPPER_RING      = new Ring("Copper Ring", 1, req(2, 2, 2, 2), NONE);
    public static final Scroll TRAVEL_NOTES   = new Scroll("Traveller's Notes", 1, 40);

    // ---------- LEVEL 2 ----------
    public static final Weapon OAK_STAFF      = new Weapon("Oak Staff", 2, 12, req(0, 0, 50, 0), WIZARD);
    public static final Weapon STEEL_SWORD    = new Weapon("Steel Sword", 2, 16, req(60, 0, 0, 0), CHAMPION);
    public static final Weapon HUNTING_BOW    = new Weapon("Hunting Bow", 2, 13, req(0, 60, 0, 0), ELF);
    public static final Shield IRON_SHIELD    = new Shield("Iron Shield", 2, 8, req(70, 0, 0, 0), CHAMPION);
    public static final Shield ELVEN_BUCKLER  = new Shield("Elven Buckler", 2, 5, req(0, 70, 0, 0), ELF);
    public static final Clothes MAGE_ROBE     = new Clothes("Mage Robe", 2, 4, req(0, 0, 60, 0), WIZARD);
    public static final Clothes CHAINMAIL     = new Clothes("Chainmail", 2, 9, req(60, 0, 0, 0), CHAMPION);
    public static final Clothes RANGER_CLOAK  = new Clothes("Ranger Cloak", 2, 6, req(0, 60, 0, 0), ELF);
    public static final Potion HEALTH_POTION  = new Potion("Health Potion", 2, 60);
    public static final Ring RING_OF_WISDOM   = new Ring("Ring of Wisdom", 2, req(0, 0, 10, 0), NONE);
    public static final Scroll SCHOLAR_SCROLL = new Scroll("Scholar's Scroll", 2, 120);
    public static final Elixir FOX_ELIXIR     = new Elixir("Elixir of the Fox", 2, StatType.AGILITY, 2);

    // ---------- LEVEL 3 ----------
    public static final Weapon ARCHMAGE_STAFF = new Weapon("Archmage Staff", 3, 25, req(0, 0, 85, 0), WIZARD);
    public static final Weapon GREAT_SWORD    = new Weapon("Great Sword", 3, 30, req(85, 0, 0, 0), CHAMPION);
    public static final Weapon LONGBOW_WINDS  = new Weapon("Longbow of Winds", 3, 26, req(0, 85, 0, 0), ELF);
    public static final Shield TOWER_SHIELD   = new Shield("Tower Shield", 3, 15, req(85, 0, 0, 0), CHAMPION);
    public static final Clothes STAR_ROBE     = new Clothes("Robe of the Stars", 3, 8, req(0, 0, 85, 0), WIZARD);
    public static final Clothes PLATE_ARMOR   = new Clothes("Plate Armor", 3, 16, req(85, 0, 0, 0), CHAMPION);
    public static final Clothes SHADOW_LEATHER = new Clothes("Shadow Leather", 3, 11, req(0, 85, 0, 0), ELF);
    public static final Potion GREATER_POTION = new Potion("Greater Health Potion", 3, 100);
    public static final Ring RING_OF_TITAN    = new Ring("Ring of the Titan", 3, req(10, 0, 0, 5), NONE);
    public static final Elixir OWL_ELIXIR     = new Elixir("Elixir of the Owl", 3, StatType.INTELLIGENCE, 3);
    public static final Elixir BEAR_ELIXIR    = new Elixir("Elixir of the Bear", 3, StatType.STRENGTH, 3);

    private static final List<Item> ALL = new ArrayList<>(List.of(
            // Level 1
            RUSTY_DAGGER, WOODEN_STAFF, IRON_SWORD, SHORT_BOW, WOODEN_SHIELD,
            APPRENTICE_ROBE, LEATHER_TUNIC, PADDED_ARMOR,
            SMALL_POTION, COPPER_RING, TRAVEL_NOTES,
            // Level 2
            OAK_STAFF, STEEL_SWORD, HUNTING_BOW, IRON_SHIELD, ELVEN_BUCKLER,
            MAGE_ROBE, CHAINMAIL, RANGER_CLOAK,
            HEALTH_POTION, RING_OF_WISDOM, SCHOLAR_SCROLL, FOX_ELIXIR,
            // Level 3
            ARCHMAGE_STAFF, GREAT_SWORD, LONGBOW_WINDS, TOWER_SHIELD,
            STAR_ROBE, PLATE_ARMOR, SHADOW_LEATHER,
            GREATER_POTION, RING_OF_TITAN, OWL_ELIXIR, BEAR_ELIXIR));

    private ItemCatalog() {
    }

    /** Adds an item that is not built into the game (e.g. read from a file). */
    public static void register(Item item) {
        if (ALL.contains(item)) {
            throw new IllegalArgumentException("An item called " + item.getName() + " already exists");
        }
        ALL.add(item);
    }

    public static List<Item> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** Items that unlock exactly at this level. */
    public static List<Item> itemsForLevel(int level) {
        List<Item> result = new ArrayList<>();
        for (Item item : ALL) {
            if (item.getMinLevel() == level) {
                result.add(item);
            }
        }
        return result;
    }

    /** Everything this hero could use right now (level, class and stats). */
    public static List<Item> usableBy(Hero hero) {
        List<Item> result = new ArrayList<>();
        for (Item item : ALL) {
            if (item.canBeUsedBy(hero)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Random mission loot: any item up to the given level, even ones the
     * hero cannot use yet (they stay in the backpack).
     */
    public static Item randomLoot(int level, Random random) {
        List<Item> pool = new ArrayList<>();
        for (Item item : ALL) {
            if (item.getMinLevel() <= level) {
                pool.add(item);
            }
        }
        return pool.get(random.nextInt(pool.size()));
    }
}

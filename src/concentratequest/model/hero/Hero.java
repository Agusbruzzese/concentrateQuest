package concentratequest.model.hero;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import concentratequest.exception.GameException;
import concentratequest.exception.InventoryFullException;
import concentratequest.exception.ItemNotFoundException;
import concentratequest.exception.MissingItemsException;
import concentratequest.exception.NotEnoughGoldException;
import concentratequest.exception.RequirementNotMetException;
import concentratequest.model.home.Home;
import concentratequest.model.inventory.Equipment;
import concentratequest.model.inventory.Inventory;
import concentratequest.model.item.Consumable;
import concentratequest.model.item.EquipSlot;
import concentratequest.model.item.Gear;
import concentratequest.model.item.Item;
import concentratequest.model.mission.TaskCategory;

public abstract class Hero implements Serializable {
    // 2: gold and home were added, so saves from version 1 are refused
    private static final long serialVersionUID = 2L;

    public static final int STARTING_STAT_POINTS = 5;
    public static final int STAT_POINTS_PER_LEVEL = 3;
    public static final int BACKPACK_CAPACITY = 20;
    private static final int BASE_HEALTH = 50;
    private static final int XP_FOR_LEVEL_2 = 7200;        // 12 hours of focus at 10 XP per minute
    private static final int EXTRA_XP_PER_LEVEL = 1200;    // each next level takes 2 hours more
    private String name;
    private int level = 1;
    private int experience;
    private int freeStatPoints = STARTING_STAT_POINTS;
    private int health;
    private int gold;
    private CharacterStats baseStats;

    // Composition: the hero owns these parts, they are created and saved with it
    private final Equipment equipment = new Equipment();
    private final Inventory<Item> backpack = new Inventory<>(BACKPACK_CAPACITY);
    private final FocusRecord record = new FocusRecord();
    private final Home home = new Home();

    /**
     * The starting gear is passed in by the subclass, instead of being added by
     * an overridable method, so no subclass code runs before it is initialised.
     */
    protected Hero(String name, CharacterStats baseStats,
                   List<? extends Gear> startingGear, List<? extends Item> startingItems) {
        this.name = validName(name);
        this.baseStats = baseStats;
        for (Gear gear : startingGear) {
            equipment.put(gear);
        }
        for (Item item : startingItems) {
            try {
                backpack.add(item);
            } catch (InventoryFullException e) {
                throw new IllegalArgumentException("Too many starting items", e);
            }
        }
        this.health = getMaxHealth();
    }

    // ---------- what each class defines ----------

    public abstract HeroType getType();

    /** The kind of real-life task this class is best at. */
    public abstract TaskCategory getAffinity();

    /** Stats gained automatically at every level up. */
    protected abstract CharacterStats getLevelUpGrowth();

    // ---------- identity and progression ----------

    private static String validName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The hero needs a name");
        }
        return name.trim();
    }

    public void setName(String name) {
        this.name = validName(name);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public final int getExperienceToNextLevel() {
        return XP_FOR_LEVEL_2 + (level - 1) * EXTRA_XP_PER_LEVEL;
    }

    public int getFreeStatPoints() {
        return freeStatPoints;
    }

    /** Read-only for everybody outside this package. */
    public FocusRecord getRecord() {
        return record;
    }

    /** Adds experience and returns how many levels were gained. */
    public int gainExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }
        experience += amount;
        int levelsGained = 0;
        while (experience >= getExperienceToNextLevel()) {
            experience -= getExperienceToNextLevel();
            levelUp();
            levelsGained++;
        }
        return levelsGained;
    }

    /** Penalty: experience goes down, but the level is never lost. */
    public void loseExperience(int amount) {
        experience = Math.max(0, experience - amount);
    }

    private void levelUp() {
        level++;
        baseStats = baseStats.plus(getLevelUpGrowth());
        freeStatPoints += STAT_POINTS_PER_LEVEL;
        health = getMaxHealth();
    }

    public void spendStatPoint(StatType stat) {
        if (freeStatPoints == 0) {
            throw new IllegalStateException(name + " has no free stat points");
        }
        freeStatPoints--;
        baseStats = baseStats.plus(stat, 1);
    }

    /** Permanent improvement earned by training, i.e. by completing missions. */
    public void train(StatType stat, int amount) {
        baseStats = baseStats.plus(stat, amount);
    }

    // ---------- health ----------

    public int getHealth() {
        return Math.min(health, getMaxHealth());
    }

    /** Stamina (gear bonuses included) makes the hero tougher. */
    public final int getMaxHealth() {
        return BASE_HEALTH + getTotalStats().getStamina();
    }

    public void heal(int amount) {
        health = Math.min(getMaxHealth(), getHealth() + amount);
    }

    public void takeDamage(int amount) {
        health = Math.max(0, getHealth() - amount);
    }

    /** At 0 HP the hero cannot go on quests: only a potion or a rest at home helps. */
    public boolean isKnockedOut() {
        return getHealth() == 0;
    }

    // ---------- gold ----------

    public int getGold() {
        return gold;
    }

    public void earnGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold earned cannot be negative");
        }
        gold += amount;
    }

    /** Sells an unwanted item from the backpack and returns the gold received. */
    public int sell(Item item) throws ItemNotFoundException {
        backpack.remove(item);
        gold += item.getValue();
        return item.getValue();
    }

    /** Pays the price and puts the item in the backpack; nothing changes if either fails. */
    public void buy(Item item, int price) throws NotEnoughGoldException, InventoryFullException {
        if (price > gold) {
            throw new NotEnoughGoldException(item.getName() + " costs " + price
                    + " gold, but " + name + " has only " + gold + ".");
        }
        backpack.add(item);
        gold -= price;
    }

    // ---------- home shelf ----------

    /** Read-only view of the items kept on the shelf at home. */
    public List<Item> getShelf() {
        return home.getShelfItems();
    }

    public int getShelfCapacity() {
        return home.getShelfCapacity();
    }

    /** Moves an item from the backpack to the shelf at home. */
    public void storeOnShelf(Item item) throws ItemNotFoundException, InventoryFullException {
        if (home.isShelfFull()) {
            throw new InventoryFullException("The shelf is full (" + home.getShelfCapacity() + " items).");
        }
        backpack.remove(item);
        home.store(item);
    }

    /** Moves an item from the shelf back into the backpack. */
    public void takeFromShelf(Item item) throws ItemNotFoundException, InventoryFullException {
        backpack.add(item); // throws if full, before the item leaves the shelf
        try {
            home.take(item);
        } catch (ItemNotFoundException e) {
            backpack.remove(item); // undo, so the item is not duplicated
            throw e;
        }
    }

    // ---------- stats and equipment ----------

    /** Base stats, without gear bonuses. Item requirements use these. */
    public CharacterStats getBaseStats() {
        return baseStats;
    }

    /** Base stats plus the bonuses of everything equipped. */
    public final CharacterStats getTotalStats() {
        return baseStats.plus(equipment.getStatBonus());
    }

    public int getDamage() {
        return equipment.getDamage();
    }

    public int getDefense() {
        return equipment.getDefense();
    }

    public Optional<Gear> getEquipped(EquipSlot slot) {
        return equipment.get(slot);
    }

    /** Moves gear from the backpack to its slot; the old gear goes back to the backpack. */
    public void equip(Gear gear) throws ItemNotFoundException, RequirementNotMetException {
        gear.checkUsableBy(this);
        backpack.remove(gear);
        Optional<Gear> oldGear = equipment.put(gear);
        if (oldGear.isPresent()) {
            returnToBackpack(oldGear.get());
        }
    }

    public void unequip(EquipSlot slot) throws ItemNotFoundException, InventoryFullException {
        Optional<Gear> equipped = equipment.get(slot);
        if (!equipped.isPresent()) {
            throw new ItemNotFoundException("Nothing is equipped as " + slot.getDisplayName());
        }
        Gear gear = equipped.get();
        backpack.add(gear); // throws if full, before the gear leaves its slot
        equipment.remove(slot);
    }

    /** A slot was just freed in the backpack, so this can only fail because of a bug. */
    private void returnToBackpack(Gear old) {
        try {
            backpack.add(old);
        } catch (InventoryFullException e) {
            throw new IllegalStateException("Backpack full right after taking an item out", e);
        }
    }

    // ---------- backpack ----------

    /** Read-only view of the backpack. */
    public List<Item> getBackpack() {
        return backpack.getItems();
    }

    /** Only the items of one type, e.g. getBackpackItems(Potion.class). */
    public <T> List<T> getBackpackItems(Class<T> type) {
        return backpack.itemsOfType(type);
    }

    public int getBackpackCapacity() {
        return backpack.getCapacity();
    }

    public void addItem(Item item) throws InventoryFullException {
        backpack.add(item);
    }

    public void dropItem(Item item) throws ItemNotFoundException {
        backpack.remove(item);
    }

    public Optional<Item> loseRandomItem(Random random) {
        return backpack.removeRandom(random);
    }

    /** True if the backpack holds every item of the list, duplicates included. */
    public boolean hasItems(List<Item> items) {
        for (Item needed : items) {
            if (backpack.count(needed) < Collections.frequency(items, needed)) {
                return false;
            }
        }
        return true;
    }

    /** Removes all the items, or none of them if one is missing. */
    public void useUpItems(List<Item> items) throws MissingItemsException {
        if (!hasItems(items)) {
            throw new MissingItemsException(name + " does not carry everything this quest needs.");
        }
        for (Item item : items) {
            try {
                backpack.remove(item);
            } catch (ItemNotFoundException e) {
                throw new IllegalStateException("Item checked a moment ago is missing", e);
            }
        }
    }

    /**
     * Uses any item without knowing what it is: gear gets equipped, potions
     * drunk, scrolls read. Each Item subclass decides (dynamic binding).
     */
    public void useItem(Item item) throws GameException {
        item.use(this);
    }

    /** Only accepts things that are both an Item (in the backpack) and Consumable. */
    public <C extends Item & Consumable> void consume(C item) throws ItemNotFoundException {
        backpack.remove(item);
        item.applyEffect(this);
    }

    // ---------- focus history ----------

    public void registerCompletedMission(int minutes) {
        record.registerSuccess(minutes);
    }

    public void registerFailedMission() {
        record.registerFailure();
    }

    @Override
    public String toString() {
        return name + " the " + getType();
    }
}

package concentratequest.ui.console;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import concentratequest.data.CustomItemLoader;
import concentratequest.exception.GameException;
import concentratequest.exception.InvalidDataException;
import concentratequest.exception.InventoryFullException;
import concentratequest.exception.ItemNotFoundException;
import concentratequest.exception.MissingItemsException;
import concentratequest.exception.NotEnoughGoldException;
import concentratequest.exception.SaveException;
import concentratequest.game.GameSettings;
import concentratequest.game.GameState;
import concentratequest.game.MissionController;
import concentratequest.model.hero.CharacterStats;
import concentratequest.model.hero.FocusRecord;
import concentratequest.model.hero.Hero;
import concentratequest.model.hero.HeroType;
import concentratequest.model.hero.StatType;
import concentratequest.model.item.EquipSlot;
import concentratequest.model.item.Gear;
import concentratequest.model.item.Item;
import concentratequest.model.item.ItemCatalog;
import concentratequest.model.mission.Mission;
import concentratequest.model.mission.MissionBoard;
import concentratequest.model.mission.TaskCategory;
import concentratequest.model.shop.Shop;
import concentratequest.persistence.SaveManager;
import concentratequest.rules.PenaltyPolicy;
import concentratequest.rules.RewardPolicy;
import concentratequest.rules.Rule;
import concentratequest.ui.swing.BrownNoise;
import concentratequest.ui.swing.QuestDialog;

/** The text version of the game: menus, the focus session and the hero sheet. */
public class ConsoleGame {
    private static final Path CUSTOM_ITEMS_FILE = Path.of("data", "custom_items.txt");
    private static final int MAX_MINUTES = 180;
    private static final int BAR_WIDTH = 20;

    private final ConsoleInput input = new ConsoleInput(System.in);
    private final SaveManager saveManager = new SaveManager();
    private final Random random = new Random();
    private final Shop shop = new Shop();
    private final boolean forceDemoMode;

    private GameState state;
    private MissionController controller;
    private MissionBoard board;
    private QuestDialog questDialog;

    public ConsoleGame(boolean forceDemoMode) {
        this.forceDemoMode = forceDemoMode;
    }

    public void run() {
        System.out.println("=====================================");
        System.out.println("   CONCENTRATE QUEST");
        System.out.println("   Focus in real life, your hero adventures for you.");
        System.out.println("=====================================");
        loadCustomItems();
        try {
            state = loadOrCreate();
            if (forceDemoMode) {
                state.getSettings().setDemoMode(true);
            }
            controller = new MissionController(state, saveManager, Clock.systemUTC(), random);
            controller.addListener(new ConsoleMissionView(state.getHero().getName()));
            if (!GraphicsEnvironment.isHeadless()) {
                questDialog = new QuestDialog(controller);
                controller.addListener(questDialog); // shows the result in a message dialog
            }
            board = new MissionBoard(random);

            controller.resolveInterruptedMission();
            mainMenu();
        } catch (SaveException e) {
            System.out.println("Problem with the save file: " + e.getMessage());
        } catch (NoSuchElementException e) {
            // the input was closed (Ctrl+D): the last save is already on disk
            System.out.println();
            System.out.println("Input closed, goodbye.");
        }
    }

    // ---------- start up ----------

    private void loadCustomItems() {
        try {
            List<Item> custom = new CustomItemLoader().load(CUSTOM_ITEMS_FILE);
            for (Item item : custom) {
                ItemCatalog.register(item);
            }
            if (!custom.isEmpty()) {
                System.out.println("Loaded " + custom.size() + " custom items from " + CUSTOM_ITEMS_FILE);
            }
        } catch (IOException e) {
            System.out.println("Could not read " + CUSTOM_ITEMS_FILE + ": " + e.getMessage());
        } catch (InvalidDataException | IllegalArgumentException e) {
            System.out.println("Custom items not loaded, " + CUSTOM_ITEMS_FILE + " " + e.getMessage());
        }
    }

    private GameState loadOrCreate() throws SaveException {
        if (saveManager.exists()) {
            try {
                GameState loaded = saveManager.load();
                System.out.println("Welcome back, " + loaded.getHero() + "!");
                return loaded;
            } catch (SaveException e) {
                System.out.println(e.getMessage());
                System.out.println("Starting a new game instead.");
            }
        }
        return newGame();
    }

    private GameState newGame() throws SaveException {
        System.out.println();
        String name = input.readNonEmpty("Name your hero: ");
        List<HeroType> types = Arrays.asList(HeroType.values());
        List<String> typeLabels = new ArrayList<>();
        for (HeroType t : types) {
            typeLabels.add(t.getDisplayName() + " - " + t.getDescription());
        }
        HeroType type = input.chooseOne("Choose the class:", types, typeLabels);
        Hero hero = type.create(name);
        System.out.println(hero + " is ready. Best at " + hero.getAffinity().getDisplayName() + " tasks.");

        GameState created = new GameState(hero);
        spendStatPoints(hero);
        saveManager.save(created);
        return created;
    }

    // ---------- main menu ----------

    private void mainMenu() throws SaveException {
        while (true) {
            Hero hero = state.getHero();
            System.out.println();
            System.out.println("--- " + hero + " | Lv " + hero.getLevel()
                    + " | HP " + hero.getHealth() + "/" + hero.getMaxHealth()
                    + " | XP " + hero.getExperience() + "/" + hero.getExperienceToNextLevel()
                    + " | " + hero.getGold() + " gold"
                    + (state.getSettings().isDemoMode() ? " | DEMO MODE" : "") + " ---");
            if (hero.isKnockedOut()) {
                System.out.println("  " + hero.getName() + " is KNOCKED OUT (0 HP) and cannot go on quests."
                        + " Drink a potion (Backpack), buy one (Shop), or choose 'Rest at home' in a focus session.");
            }
            System.out.println("  1. Start a focus session");
            System.out.println("  2. Hero sheet");
            System.out.println("  3. Backpack: use, equip, store, sell or drop items");
            System.out.println("  4. Unequip");
            System.out.println("  5. Home shelf (" + hero.getShelf().size() + "/" + hero.getShelfCapacity() + ")");
            System.out.println("  6. Shop: buy potions");
            System.out.println("  7. Spend stat points (" + hero.getFreeStatPoints() + ")");
            System.out.println("  8. Rules and settings");
            System.out.println("  0. Save and quit");
            switch (input.readInt("> ", 0, 8)) {
                case 1 -> startFocusSession();
                case 2 -> printHeroSheet();
                case 3 -> backpackMenu();
                case 4 -> unequipMenu();
                case 5 -> shelfMenu();
                case 6 -> shopMenu();
                case 7 -> spendStatPoints(hero);
                case 8 -> settingsMenu();
                case 0 -> {
                    saveManager.save(state);
                    System.out.println("Saved. See you at the next session!");
                    return;
                }
                default -> throw new IllegalStateException("readInt only returns 0..8");
            }
            saveManager.save(state); // autosave, so closing the app never loses progress
        }
    }

    // ---------- focus session ----------

    private void startFocusSession() throws SaveException {
        Hero hero = state.getHero();
        GameSettings settings = state.getSettings();

        String task = input.readNonEmpty("What are you going to work on? ");
        List<TaskCategory> categories = Arrays.asList(TaskCategory.values());
        List<String> categoryLabels = new ArrayList<>();
        for (TaskCategory c : categories) {
            categoryLabels.add(c.getDisplayName() + " (trains " + c.getTrainedStat().getDisplayName() + ")"
                    + (c == hero.getAffinity() ? "  * " + hero.getType() + " bonus" : ""));
        }
        TaskCategory category = input.chooseOne("What kind of task is it?", categories, categoryLabels);
        String demoNote = settings.isDemoMode() ? " (demo mode: 1 minute lasts 1 second)" : "";
        int minutes = input.readInt("How many minutes will you focus? [1-" + MAX_MINUTES + "]"
                + demoNote + " ", 1, MAX_MINUTES);

        if (hero.isKnockedOut()) {
            System.out.println("(" + hero.getName() + " is knocked out: the only choice is to rest at home.)");
        } else if (minutes < MissionBoard.EPIC_QUEST_MIN_MINUTES) {
            System.out.println("(Commit " + MissionBoard.LONG_QUEST_MIN_MINUTES + "+ minutes for long quests, "
                    + MissionBoard.EPIC_QUEST_MIN_MINUTES + "+ for epic quests.)");
        }
        List<Mission> offers = board.offers(hero, task, category, minutes, settings.realDurationFor(minutes));
        List<String> offerLabels = new ArrayList<>();
        for (Mission m : offers) {
            offerLabels.add(m + (m.getRequiredItems().isEmpty() ? "" : "  needs " + m.describeRequiredItems()
                    + (hero.hasItems(m.getRequiredItems()) ? "" : " (MISSING)")));
        }
        Optional<Mission> chosen = input.choose("Where should " + hero.getName() + " go?", offers, offerLabels);
        if (chosen.isEmpty()) {
            return;
        }
        try {
            controller.start(chosen.get());
        } catch (MissingItemsException e) {
            System.out.println(e.getMessage() + " It needs " + chosen.get().describeRequiredItems()
                    + ": short quests find materials, long quests make keys.");
            return;
        }
        if (questDialog == null) {
            waitInConsole(chosen.get());
        } else {
            waitInWindow(chosen.get());
        }
    }

    /**
     * With a screen: a dialog shows the time left until the quest is over;
     * closing it means giving up. Brown noise plays while the dialog is open.
     */
    @SuppressWarnings("try") // the noise is only there to be closed at the end of the block
    private void waitInWindow(Mission mission) throws SaveException {
        System.out.println("The quest window is open. Closing it means giving up the quest.");
        boolean completed;
        try (BrownNoise noise = BrownNoise.startIf(state.getSettings().isBrownNoise())) {
            completed = questDialog.waitForQuest(mission);
        }
        if (completed) {
            controller.finish();
        } else {
            controller.giveUp();
        }
    }

    /**
     * Without a screen: a background timer prints the progress while the main
     * thread waits for the player. The mission only ends when the player comes back.
     */
    private void waitInConsole(Mission mission) throws SaveException {
        System.out.println("Press ENTER to check on the hero, or type 'give up' to call them back.");
        long period = Math.max(1000, mission.getRealDuration().toMillis() / 10);
        Timer timer = new Timer("mission-timer", true); // daemon: must not keep the program alive
        // (anonymous inner class: the task the timer repeats)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                controller.tick();
            }
        }, period, period);
        try {
            while (true) {
                String line = input.readLine("");
                if (controller.isTimeUp()) {
                    controller.finish();
                    return;
                }
                if (line.equalsIgnoreCase("give up")) {
                    if (input.confirm("Really give up? The penalty rule is "
                            + state.getSettings().getPenaltyPolicy().getName() + ".")) {
                        if (controller.isTimeUp()) {
                            System.out.println("Too late to give up, the time is over!");
                            controller.finish();
                        } else {
                            controller.giveUp();
                        }
                        return;
                    }
                } else {
                    controller.tick();
                }
            }
        } finally {
            timer.cancel();
        }
    }

    // ---------- hero ----------

    private void printHeroSheet() {
        Hero hero = state.getHero();
        CharacterStats base = hero.getBaseStats();
        CharacterStats total = hero.getTotalStats();
        System.out.println();
        System.out.println("=== " + hero + " - level " + hero.getLevel() + " ===");
        System.out.println("HP " + ConsoleFormat.bar(hero.getHealth(), hero.getMaxHealth(), BAR_WIDTH)
                + " " + hero.getHealth() + "/" + hero.getMaxHealth());
        System.out.println("XP " + ConsoleFormat.bar(hero.getExperience(), hero.getExperienceToNextLevel(), BAR_WIDTH)
                + " " + hero.getExperience() + "/" + hero.getExperienceToNextLevel());
        for (StatType stat : StatType.values()) {
            int bonus = total.get(stat) - base.get(stat);
            System.out.println(String.format("  %-13s %3d%s", stat.getDisplayName(), total.get(stat),
                    bonus > 0 ? " (+" + bonus + " from gear)" : ""));
        }
        if (hero.getFreeStatPoints() > 0) {
            System.out.println("  Free stat points: " + hero.getFreeStatPoints());
        }
        System.out.println("Equipment:");
        for (EquipSlot slot : EquipSlot.values()) {
            Optional<Gear> gear = hero.getEquipped(slot);
            System.out.println(String.format("  %-9s %s", slot.getDisplayName() + ":",
                    gear.isPresent() ? gear.get().describe() : "none"));
        }
        System.out.println("Damage " + hero.getDamage() + " | Defense " + hero.getDefense()
                + " | Backpack " + hero.getBackpack().size() + "/" + hero.getBackpackCapacity()
                + " | Shelf " + hero.getShelf().size() + "/" + hero.getShelfCapacity()
                + " | Gold " + hero.getGold());
        if (hero.isKnockedOut()) {
            System.out.println("KNOCKED OUT: drink a potion or rest at home before the next quest.");
        }
        FocusRecord record = hero.getRecord();
        System.out.println("Focus: " + record.getCompletedMissions() + " missions completed, "
                + record.getFailedMissions() + " given up, " + record.getTotalFocusMinutes()
                + " minutes in total, streak " + record.getStreak() + " (best " + record.getBestStreak() + ")");
        System.out.println("Affinity: " + hero.getAffinity().getDisplayName()
                + " tasks give +25% XP and one more item.");
    }

    private void spendStatPoints(Hero hero) {
        while (hero.getFreeStatPoints() > 0) {
            System.out.println();
            System.out.println("Free stat points: " + hero.getFreeStatPoints());
            List<StatType> stats = Arrays.asList(StatType.values());
            List<String> statLabels = new ArrayList<>();
            for (StatType s : stats) {
                statLabels.add(s.getDisplayName() + " (" + hero.getBaseStats().get(s) + ")");
            }
            Optional<StatType> stat = input.choose("Improve which stat?", stats, statLabels);
            if (stat.isEmpty()) {
                return;
            }
            hero.spendStatPoint(stat.get());
        }
        System.out.println("No free stat points left.");
    }

    // ---------- items ----------

    private void backpackMenu() {
        Hero hero = state.getHero();
        while (true) {
            System.out.println();
            if (hero.getBackpack().isEmpty()) {
                System.out.println("The backpack is empty.");
                return;
            }
            List<String> itemLabels = new ArrayList<>();
            for (Item i : hero.getBackpack()) {
                itemLabels.add(i.describe() + (i.canBeUsedBy(hero) ? "" : "  (cannot use)"));
            }
            Optional<Item> chosen = input.choose(
                    "Backpack (" + hero.getBackpack().size() + "/" + hero.getBackpackCapacity() + "):",
                    hero.getBackpack(), itemLabels);
            if (chosen.isEmpty()) {
                return;
            }
            Item item = chosen.get();
            System.out.println(item.getName() + ":");
            System.out.println("  1. " + item.getUseVerb());
            System.out.println("  2. Put on the shelf at home");
            System.out.println("  3. Sell for " + item.getValue() + " gold");
            System.out.println("  4. Drop");
            System.out.println("  0. Back");
            int action = input.readInt("> ", 0, 4);
            try {
                switch (action) {
                    case 1 -> {
                        hero.useItem(item); // polymorphic: equip, drink, read...
                        System.out.println(item.getUseVerb() + ": " + item.getName() + " - done.");
                    }
                    case 2 -> {
                        hero.storeOnShelf(item);
                        System.out.println(item.getName() + " is now on the shelf.");
                    }
                    case 3 -> System.out.println("Sold for " + hero.sell(item) + " gold. Gold: " + hero.getGold());
                    case 4 -> {
                        if (input.confirm("Drop " + item.getName() + " forever?")) {
                            hero.dropItem(item);
                        }
                    }
                    default -> {
                        // back
                    }
                }
            } catch (GameException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void shelfMenu() {
        Hero hero = state.getHero();
        while (true) {
            System.out.println();
            if (hero.getShelf().isEmpty()) {
                System.out.println("The shelf is empty. Put items there from the backpack menu.");
                return;
            }
            List<String> itemLabels = new ArrayList<>();
            for (Item i : hero.getShelf()) {
                itemLabels.add(i.describe());
            }
            Optional<Item> chosen = input.choose(
                    "Shelf at home (" + hero.getShelf().size() + "/" + hero.getShelfCapacity()
                            + "). Choose an item to take it back:",
                    hero.getShelf(), itemLabels);
            if (chosen.isEmpty()) {
                return;
            }
            try {
                hero.takeFromShelf(chosen.get());
                System.out.println(chosen.get().getName() + " is back in the backpack.");
            } catch (ItemNotFoundException | InventoryFullException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void shopMenu() {
        Hero hero = state.getHero();
        while (true) {
            System.out.println();
            System.out.println("The village shop. You have " + hero.getGold() + " gold."
                    + " (Unwanted items can be sold from the backpack menu.)");
            List<Item> stock = shop.getStock(hero);
            List<String> itemLabels = new ArrayList<>();
            for (Item i : stock) {
                itemLabels.add(i.describe() + "  -  " + shop.priceOf(i) + " gold");
            }
            Optional<Item> chosen = input.choose("Potions for sale:", stock, itemLabels);
            if (chosen.isEmpty()) {
                return;
            }
            try {
                shop.buy(hero, chosen.get());
                System.out.println("Bought " + chosen.get().getName() + ". Gold left: " + hero.getGold());
            } catch (NotEnoughGoldException | InventoryFullException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void unequipMenu() {
        Hero hero = state.getHero();
        List<EquipSlot> used = new ArrayList<>();
        for (EquipSlot slot : EquipSlot.values()) {
            if (hero.getEquipped(slot).isPresent()) {
                used.add(slot);
            }
        }
        if (used.isEmpty()) {
            System.out.println("Nothing is equipped.");
            return;
        }
        List<String> slotLabels = new ArrayList<>();
        for (EquipSlot s : used) {
            slotLabels.add(s.getDisplayName() + ": " + hero.getEquipped(s).get().getName()); // used slots only
        }
        Optional<EquipSlot> slot = input.choose("Unequip what?", used, slotLabels);
        if (slot.isEmpty()) {
            return;
        }
        try {
            hero.unequip(slot.get());
            System.out.println("Moved to the backpack.");
        } catch (ItemNotFoundException | InventoryFullException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---------- settings ----------

    private void settingsMenu() {
        GameSettings settings = state.getSettings();
        while (true) {
            System.out.println();
            System.out.println("Reward rule:  " + settings.getRewardPolicy().getName()
                    + " - " + settings.getRewardPolicy().getDescription());
            System.out.println("Penalty rule: " + settings.getPenaltyPolicy().getName()
                    + " - " + settings.getPenaltyPolicy().getDescription());
            System.out.println("Demo mode:    " + (settings.isDemoMode() ? "on" : "off"));
            System.out.println("Brown noise:  " + (settings.isBrownNoise() ? "on" : "off"));
            switch (input.readInt("1. Reward rule  2. Penalty rule  3. Demo mode on/off  4. Brown noise on/off"
                    + "  0. Back\n> ", 0, 4)) {
                case 1 -> {
                    Optional<RewardPolicy> reward =
                            chooseRule("Reward rules:", GameSettings.REWARD_POLICIES);
                    if (reward.isPresent()) {
                        settings.setRewardPolicy(reward.get());
                    }
                }
                case 2 -> {
                    Optional<PenaltyPolicy> penalty =
                            chooseRule("Penalty rules:", GameSettings.PENALTY_POLICIES);
                    if (penalty.isPresent()) {
                        settings.setPenaltyPolicy(penalty.get());
                    }
                }
                case 3 -> settings.setDemoMode(!settings.isDemoMode());
                case 4 -> settings.setBrownNoise(!settings.isBrownNoise());
                default -> {
                    return;
                }
            }
        }
    }

    /** Works for any kind of rule thanks to the bound R extends Rule. */
    private <R extends Rule> Optional<R> chooseRule(String title, List<R> rules) {
        List<String> ruleLabels = new ArrayList<>();
        for (R rule : rules) {
            ruleLabels.add(rule.getName() + " - " + rule.getDescription());
        }
        return input.choose(title, rules, ruleLabels);
    }
}

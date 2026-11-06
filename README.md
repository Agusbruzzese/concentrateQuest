# Concentrate Quest

A "Forest"-style focus app with RPG elements. You commit to a real-life task
(study, work, exercise, creative) for a number of minutes, and your hero leaves
on a mission for exactly that long. If you stay until the end, the hero comes back
with experience, a stat point for the kind of task you did, and random items.
If you give up or close the app before the end, the mission fails and the
penalty rule applies.

During a quest a simple window (JOptionPane, as in chapter 3 of the textbook)
shows the time left and the percentage, with a Give up button. Brown noise can
play in the background (switch it off in the settings) and a message announces
the result. Closing the window means giving up (after a confirmation). Without a display
(e.g. over SSH) the game falls back to a console countdown.

## Build and run

```
javac -d out $(find src -name '*.java')
java -cp out concentratequest.Main           # real time
java -cp out concentratequest.Main --demo    # 1 minute lasts 1 second
```

Run it from the project folder: the save goes to `save/` and custom items are read
from `data/custom_items.txt`.

## Game rules

| Rule | Where |
|---|---|
| XP = minutes x 10 (Standard) or with a streak bonus up to x2 (Streak) | `rules/StandardRewardPolicy`, `rules/StreakRewardPolicy` |
| Loot: 1 item + 1 per 30 min, +1 for long/epic quests, +1 for affinity; 1 gold per minute | `Mission.computeReward` |
| Quests are themed by task (Study: libraries, Exercise: mountains and arenas...) | `TaskCategory`, `MissionBoard` |
| Long quests need 25+ minutes, epic quests 50+; epic loot is one level higher | `MissionBoard`, `EpicQuest` |
| Quest-item chain per category: a short quest always finds 1 material; a long quest needs 2 materials and makes a key; an epic quest needs the key. Required items are used up when the quest starts | `TaskCategory`, `QuestItem`, `Mission.getRequiredItems` |
| At 0 HP the hero is knocked out: only "Rest at home" (full heal, half XP, no loot) | `Hero.isKnockedOut`, `RestMission` |
| Unwanted items can be stored on the home shelf (30) or sold; the shop sells potions | `Home`, `Hero.sell`, `Shop` |
| One permanent stat point per 25 minutes, in the stat of the task category | `TaskCategory`, `Mission.computeReward` |
| Class affinity (Wizard→Study, Champion→Exercise, Elf→Creative): +25% XP, +1 item | `Hero.getAffinity`, `Mission.AFFINITY_XP_BONUS` |
| Level up: 7200 XP for level 2 (12 h of focus at 10 XP/min), then 1200 XP (2 h) more per level: class growth, +3 free points, full HP | `Hero.gainExperience` |
| Giving up: Gentle (-10 HP) or Hardcore (-30 HP, XP, a random item); the streak resets | `rules/*PenaltyPolicy`, `MissionController.giveUp` |
| Closing the app during a mission counts as giving up (unless the time was over) | `MissionController.resolveInterruptedMission` |

## Packages

```
model/hero       Hero and its classes, stats, focus history
model/item       Item hierarchy, Equippable and Consumable interfaces, the catalog
model/inventory  Inventory<T> (backpack) and Equipment (slots)
model/mission    Mission hierarchy (Short, Long, Epic quests, Rest), states, rewards, board
model/home       Home with the shelf
model/shop       Shop (buys items, sells potions)
rules            Reward and penalty policies (Strategy)
game             GameState, settings, MissionController and MissionListener (Observer)
persistence      SaveManager (serialization)
data             CustomItemLoader (items from a text file)
exception        GameException hierarchy
ui/console       Text interface: menus and the console countdown
ui/swing         QuestDialog (quest window with JOptionPane), BrownNoise
```

The model never prints anything, so a GUI can replace `ui/console` without changing it.

## Where each OOP concept is

| Concept | Main examples |
|---|---|
| Encapsulation / information hiding | private fields everywhere; `Hero.getBackpack()` returns a read-only view; `FocusRecord` mutators are package-private; `Mission` state changes only via `start/complete/fail`; `GameState.setActiveMission` is package-private |
| Immutability | `CharacterStats` (final, `plus` returns new objects), `MissionReward` |
| Inheritance | `Item → Gear → Weapon/Shield/Clothes/Ring`; `Item → Potion/Scroll/Elixir`; `Hero → Wizard/Champion/Elf`; `Mission → ShortQuest/LongQuest/EpicQuest/RestMission`; `StreakRewardPolicy extends StandardRewardPolicy` (calls `super`) |
| Composition | `Hero` owns `Equipment`, `Inventory<Item>`, `FocusRecord`; `GameState` owns `Hero`, `GameSettings`, `Mission` |
| Abstraction | abstract `Item`, `Gear`, `Hero`, `Mission`; interfaces `Equippable`, `Consumable`, `Rule`, `RewardPolicy`, `PenaltyPolicy`, `MissionListener` |
| Multiple typing with interfaces | `Gear` is an `Item` and an `Equippable`; `Potion` is an `Item` and a `Consumable`; `Item` is `Serializable` and `Comparable<Item>`; `Inventory` is `Iterable`; `RewardPolicy` extends `Rule` which extends `Serializable` |
| Inclusion polymorphism | `Hero.useItem(item)` calls `item.use(hero)`: equips, drinks or reads depending on the real class; `Equipment.getDamage()` sums `Equippable` bonuses; `Mission.computeReward` uses the subclass hooks; listeners and policies are called through their interfaces |
| Overloading | `CharacterStats.plus(CharacterStats)` / `plus(StatType, int)`; `Item` and `Mission` constructors; `MissionController.giveUp()` / `giveUp(String)` and two constructors; `ConsoleInput.choose` (2 and 3 arguments); `ConsoleFormat.bar(double,int)` / `bar(int,int,int)`; `SaveManager()` / `SaveManager(Path)` |
| Parametric polymorphism | `Inventory<T extends Item>`; `<S> List<S> itemsOfType(Class<S>)`; `<C extends Item & Consumable> void consume(C)` (intersection bound); `<R extends Rule> chooseRule(...)`; `List<? extends Gear>` in the `Hero` constructor; `<T> Optional<T> choose(...)` with `Function<? super T, String>` |
| Coercion | `Mission.getProgress` (long → double); `StreakRewardPolicy.experienceFor` (int x double); `ConsoleFormat.bar` (`(double) value / max`); explicit narrowing `(int) Math.round(...)`; string conversion in concatenation |
| Exception handling | checked `GameException` hierarchy vs unchecked `InvalidMissionStateException`; chaining (`SaveException`, `InvalidDataException` keep the cause); multi-catch (`SaveManager.load`, `ConsoleGame.unequipMenu`); try-with-resources (`SaveManager`, `CustomItemLoader`); `finally` stops the timer in `ConsoleGame.waitForMission`; `ConsoleInput.readInt` recovers from `NumberFormatException` |
| Design patterns | Template Method (`Mission.computeReward`), Strategy (policies), Observer (`MissionListener`), Factory (`HeroType.create`), State machine (`MissionState`) |
| Extensibility | new hero class = subclass + one `HeroType` constant and one `case` in `HeroType.create`; new item = one line in `data/custom_items.txt`, new item kind = one `case` in `CustomItemLoader.createItem`; new mission = subclass + `MissionBoard`; new rule = class + one entry in `GameSettings` |
| Customization | custom items file, reward and penalty rules picked in game, demo mode |

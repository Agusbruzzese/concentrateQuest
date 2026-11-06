package concentratequest.model.shop;

import java.util.ArrayList;
import java.util.List;

import concentratequest.exception.InventoryFullException;
import concentratequest.exception.NotEnoughGoldException;
import concentratequest.model.hero.Hero;
import concentratequest.model.item.Item;
import concentratequest.model.item.ItemCatalog;
import concentratequest.model.item.Potion;

/**
 * The village shop. It buys any item (Hero.sell) and sells potions, so a
 * knocked-out hero always has a way back with the gold earned by focusing.
 */
public class Shop {
    /** The shop sells at three times the price it pays. */
    public static final int PRICE_MULTIPLIER = 3;

    /** The potions this hero's level allows. */
    public List<Item> getStock(Hero hero) {
        List<Item> stock = new ArrayList<>();
        for (Item item : ItemCatalog.all()) {
            if (item instanceof Potion && item.getMinLevel() <= hero.getLevel()) {
                stock.add(item);
            }
        }
        return stock;
    }

    public int priceOf(Item item) {
        return item.getValue() * PRICE_MULTIPLIER;
    }

    public void buy(Hero hero, Item item) throws NotEnoughGoldException, InventoryFullException {
        hero.buy(item, priceOf(item));
    }
}

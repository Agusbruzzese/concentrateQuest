package concentratequest.model.home;

import java.io.Serializable;
import java.util.List;

import concentratequest.exception.InventoryFullException;
import concentratequest.exception.ItemNotFoundException;
import concentratequest.model.inventory.Inventory;
import concentratequest.model.item.Item;

/**
 * The hero's home. For now it holds a shelf where items can be kept
 * without filling the backpack; later it will be the isometric room.
 */
public class Home implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int SHELF_CAPACITY = 30;

    // The same generic Inventory used for the backpack, with a different capacity
    private final Inventory<Item> shelf = new Inventory<>(SHELF_CAPACITY);

    public void store(Item item) throws InventoryFullException {
        shelf.add(item);
    }

    public void take(Item item) throws ItemNotFoundException {
        shelf.remove(item);
    }

    public boolean isShelfFull() {
        return shelf.isFull();
    }

    /** Read-only view of the shelf. */
    public List<Item> getShelfItems() {
        return shelf.getItems();
    }

    public int getShelfCapacity() {
        return shelf.getCapacity();
    }
}

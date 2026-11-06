package concentratequest.model.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import concentratequest.exception.InventoryFullException;
import concentratequest.exception.ItemNotFoundException;
import concentratequest.model.item.Item;

/**
 * A bag with a maximum number of items. Generic so the same class could hold
 * only a subtype too (e.g. an Inventory&lt;Potion&gt; for a potion belt).
 */
public class Inventory<T extends Item> implements Serializable, Iterable<T> {
    private static final long serialVersionUID = 1L;

    private final int capacity;
    private final ArrayList<T> items = new ArrayList<>();

    public Inventory(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        this.capacity = capacity;
    }

    public void add(T item) throws InventoryFullException {
        if (isFull()) {
            throw new InventoryFullException(item.getName()
                    + " does not fit: the backpack is full (" + capacity + " items).");
        }
        items.add(item);
    }

    public void remove(T item) throws ItemNotFoundException {
        if (!items.remove(item)) {
            throw new ItemNotFoundException(item.getName() + " is not in the backpack.");
        }
    }

    public boolean contains(T item) {
        return items.contains(item);
    }

    /** How many copies of this item are in the inventory. */
    public int count(T item) {
        int count = 0;
        for (T inside : items) {
            if (inside.equals(item)) {
                count++;
            }
        }
        return count;
    }

    public Optional<T> removeRandom(Random random) {
        if (items.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(items.remove(random.nextInt(items.size())));
    }

    /** Read-only view: the list can only change through add and remove. */
    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    /** Every item that is an instance of the given class or interface. */
    public <S> List<S> itemsOfType(Class<S> type) {
        List<S> result = new ArrayList<>();
        for (T item : items) {
            if (type.isInstance(item)) {
                result.add(type.cast(item));
            }
        }
        return result;
    }

    /** A sorted copy, using the natural order of Item (level, then name). */
    public List<T> sorted() {
        List<T> copy = new ArrayList<>(items);
        Collections.sort(copy);
        return copy;
    }

    public int size() {
        return items.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isFull() {
        return items.size() >= capacity;
    }

    @Override
    public Iterator<T> iterator() {
        return getItems().iterator();
    }
}

package concentratequest.model.item;

/** The places on the hero's body where gear goes, one item per slot. */
public enum EquipSlot {
    WEAPON("Weapon"),
    SHIELD("Shield"),
    CLOTHES("Clothes"),
    RING("Ring");

    private final String displayName;

    EquipSlot(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

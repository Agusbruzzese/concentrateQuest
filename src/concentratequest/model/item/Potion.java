package concentratequest.model.item;

import concentratequest.exception.ItemNotFoundException;
import concentratequest.model.hero.Hero;

/** Restores health. */
public class Potion extends Item implements Consumable {
    private static final long serialVersionUID = 1L;

    private final int healAmount;

    public Potion(String name, int minLevel, int healAmount) {
        super(name, minLevel);
        this.healAmount = healAmount;
    }

    @Override
    public String getKind() {
        return "Potion";
    }

    @Override
    protected String getDetails() {
        return "Heals " + healAmount;
    }

    @Override
    public String getUseVerb() {
        return "Drink";
    }

    @Override
    public void use(Hero hero) throws ItemNotFoundException {
        hero.consume(this);
    }

    @Override
    public void applyEffect(Hero hero) {
        hero.heal(healAmount);
    }
}

package concentratequest.model.item;

import concentratequest.exception.ItemNotFoundException;
import concentratequest.model.hero.Hero;
import concentratequest.model.hero.StatType;

/** Permanently raises one stat. */
public class Elixir extends Item implements Consumable {
    private static final long serialVersionUID = 1L;

    private final StatType stat;
    private final int amount;

    public Elixir(String name, int minLevel, StatType stat, int amount) {
        super(name, minLevel);
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public String getKind() {
        return "Elixir";
    }

    @Override
    protected String getDetails() {
        return "Permanent +" + amount + " " + stat.getShortName();
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
        hero.train(stat, amount);
    }
}

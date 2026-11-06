package concentratequest.model.item;

import concentratequest.exception.ItemNotFoundException;
import concentratequest.model.hero.Hero;

/** Notes of past adventures: reading them gives experience. */
public class Scroll extends Item implements Consumable {
    private static final long serialVersionUID = 1L;

    private final int experience;

    public Scroll(String name, int minLevel, int experience) {
        super(name, minLevel);
        this.experience = experience;
    }

    @Override
    public String getKind() {
        return "Scroll";
    }

    @Override
    protected String getDetails() {
        return "Grants " + experience + " XP";
    }

    @Override
    public String getUseVerb() {
        return "Read";
    }

    @Override
    public void use(Hero hero) throws ItemNotFoundException {
        hero.consume(this);
    }

    @Override
    public void applyEffect(Hero hero) {
        hero.gainExperience(experience);
    }
}

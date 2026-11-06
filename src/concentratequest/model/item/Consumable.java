package concentratequest.model.item;

import concentratequest.model.hero.Hero;

/** Something used up in one go: it has an effect and then it is gone. */
public interface Consumable {

    void applyEffect(Hero hero);
}

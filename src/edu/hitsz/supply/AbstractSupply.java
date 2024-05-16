package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.basic.AbstractFlyingObject;

/**
 * 所有种类道具的抽象父类：
 * HP, FIRE, BOMB
 *
 * @author ssd
 */
public abstract class AbstractSupply extends AbstractFlyingObject {
    public AbstractSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }
    public abstract void effect(HeroAircraft heroAircraft);
}

package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public interface StrategyShoot {
    public abstract List<BaseBullet> shoot(int XAircraft, int YAircraft, int speedXAircraft, int speedYAircraft, int direction,int power,int type);
}

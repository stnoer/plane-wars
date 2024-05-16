package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class Context {
     private StrategyShoot strategyShoot;

    public Context(StrategyShoot strategyShoot){
        this.strategyShoot=strategyShoot;
    }

    public void setStrategy(StrategyShoot strategyShoot) {
        this.strategyShoot=strategyShoot;
    }

    public List<BaseBullet> strategyShoot(int XAircraft, int YAircraft, int speedXAircraft, int speedYAircraft, int direction,int power, int type){
        return strategyShoot.shoot(XAircraft, YAircraft,speedXAircraft,speedYAircraft,direction,power,type);
    }
}

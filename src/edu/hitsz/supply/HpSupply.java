package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;

public class HpSupply extends AbstractSupply{
    public HpSupply(int locationX, int locationY) {
        super(locationX, locationY, 0,5);
    }
    public void effect(HeroAircraft heroAircraft){
        heroAircraft.increaseHp(10);
    }
}
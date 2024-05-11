package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;

public class BombSupply extends AbstractSupply{
    public BombSupply(int locationX, int locationY) {
        super(locationX, locationY, 0, 5);
    }
    public void effect(HeroAircraft heroAircraft){

        System.out.println("BombSupply active!");
    }
}

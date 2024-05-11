package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;

import java.util.concurrent.CompletableFuture;


public class FireSupply extends AbstractSupply{
    public FireSupply(int locationX, int locationY) {
        super(locationX, locationY, 0,5);
    }
    public void effect(HeroAircraft heroAircraft) {
        heroAircraft.changeShootType(1);
        BulletThread thread = new BulletThread(heroAircraft);
        thread.start();
    }
}

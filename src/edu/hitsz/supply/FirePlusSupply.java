package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FirePlusSupply extends AbstractSupply {
    private ExecutorService executor = Executors.newSingleThreadExecutor(); // 创建单线程的线程池
    public FirePlusSupply(int locationX, int locationY) {
        super(locationX, locationY, 0,5);
    }
    public void effect(HeroAircraft heroAircraft) {
        heroAircraft.changeShootType(2);
        BulletThread thread = new BulletThread(heroAircraft);
        thread.start();
    }
}


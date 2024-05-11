package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;


public class BulletThread extends Thread {
    private HeroAircraft heroAircraft;

    public BulletThread(HeroAircraft heroAircraft) {
        this.heroAircraft = heroAircraft;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(4000); // 休眠4秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        heroAircraft.changeShootType(0); // 执行回调函数
    }
}

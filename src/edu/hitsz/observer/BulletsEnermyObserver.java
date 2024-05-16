package edu.hitsz.observer;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

public class BulletsEnermyObserver implements Observer {
    private final BaseBullet baseBullet;
    public BulletsEnermyObserver(BaseBullet baseBullet){
        this.baseBullet=baseBullet;
    }
    @Override
    public  void update() {
        baseBullet.vanish();
    }
}

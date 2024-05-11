package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class StraightShoot implements StrategyShoot {
    public List<BaseBullet> shoot(int XAircraft, int YAircraft, int speedXAircraft, int speedYAircraft, int direction,int power,int type){
        List<BaseBullet> res = new LinkedList<>();
        int x = XAircraft;
        int y = YAircraft + direction*2;
        int speedX = 0;
        int speedY = speedYAircraft + direction*5;
        int shootNum = 1;
        BaseBullet bullet;
        for(int i=0; i < shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            if(type==0){
                bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            }
            else{
                bullet = new HeroBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            }
            res.add(bullet);
        }
        return res;
    }

}

package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class CycleShoot implements StrategyShoot{
    @Override
    public List<BaseBullet> shoot(int XAircraft, int YAircraft, int speedXAircraft, int speedYAircraft, int direction,int power,int type){
        List<BaseBullet> res = new LinkedList<>();
        BaseBullet bullet;

        int shootNum = 20;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            double R = 35;
            double v = 10;
            int x = (int)(XAircraft + R * Math.cos(i*Math.PI/10));
            int y = (int)(YAircraft + R * Math.sin(i*Math.PI/10));
            int speedX = (int)(v * Math.cos(i*Math.PI/10));
            int speedY = (int)(v * Math.sin(i*Math.PI/10));
            if(type==0){
                bullet = new EnemyBullet(x , y, speedX , speedY, power);
            }
            else{
                bullet = new HeroBullet(x , y, speedX , speedY, power);
            }
            res.add(bullet);
        }
        return res;
    }
}

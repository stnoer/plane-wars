package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.strategy.Context;
import edu.hitsz.strategy.DiffuseShoot;
import edu.hitsz.strategy.StraightShoot;
import edu.hitsz.strategy.StrategyShoot;


import java.util.LinkedList;
import java.util.List;
/**
 * 超级精英敌机
 * 不可射击
 *
 * @author ssd
 */
public class ElitePlus extends AbstractAircraft {
    /**
     * 子弹一次发射数量
     */
    private int shootNum = 3;

    /**
     * 子弹伤害
     */
    private int power = 30;

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */
    private int direction = 1;

    public ElitePlus() {
        super((int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                (int)((Math.random()-0.5)*15),
                6,
                60);
    }
    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        StrategyShoot strategyShoot = new DiffuseShoot();
        Context context = new Context(strategyShoot);
        return context.strategyShoot(getLocationX(),getLocationY(),getSpeedX(),getSpeedY(),direction,power,0);
    }

}

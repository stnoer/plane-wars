package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.music.MusicThread;
import edu.hitsz.observer.BulletsEnermyObserver;
import edu.hitsz.observer.EnermyObserver;
import edu.hitsz.supply.*;
import edu.hitsz.swing.EndEasyMenu;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractGame extends JPanel {
    protected  int backGroundTop = 0;
    protected  ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    protected  int timeInterval = 40;

    protected  HeroAircraft heroAircraft;
    protected  List<AbstractAircraft> enemyAircrafts;
    protected  List<BaseBullet> heroBullets;
    protected  List<BaseBullet> enemyBullets;
    protected  List<AbstractSupply> supplies;

    protected  boolean isPlay;
    /**
     * 屏幕中出现的敌机最大数量
     */
    protected  int enemyMaxNumber = 5;

    /**
     * 当前得分
     */
    protected  int score = 0;
    /**
     * 游戏日期
     */
    protected  String formattedDateTime;
    /**
     * 当前时刻
     */
    protected  int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    protected  int cycleDuration = 600;
    protected  int cycleTime = 0;
    /**
     * 随机数
     */
    protected  double random = 0.5;

    public int difficulty;
    protected  boolean gameOverFlag = false;

    public AbstractGame(int difficulty, boolean isPlay) {
        this.isPlay = isPlay;
        this.difficulty = difficulty;
        heroAircraft = HeroAircraft.getHeroAircraft();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        supplies = new LinkedList<>();

        /**
         * Scheduled 线程池，用于定时任务调度
         * 关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
         * apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        //启动英雄机鼠标监听
        new HeroController(this,heroAircraft);

    }

    //***********************
    //      Action 各部分
    //***********************

    protected  boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    protected  void shootAction() {
        //  敌机射击
        for (AbstractAircraft aircraft : enemyAircrafts) {
            enemyBullets.addAll(aircraft.shoot());
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    protected  void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    protected  void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    protected  void suppliesMoveAction() {
        for (AbstractSupply supply : supplies) {
            supply.forward();
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     * 无效的原因可能是撞击或者飞出边界
     */
    protected  void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supplies.removeIf(AbstractFlyingObject::notValid);
    }


    protected  void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    protected  void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }



    public abstract void action ();
    public abstract void crashCheckAction();

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param
     */
    public void paint(Graphics g) {
        super.paint(g);
    }
}





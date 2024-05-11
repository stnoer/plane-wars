package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.file.DAO;
import edu.hitsz.file.ScoreDAO;
import edu.hitsz.music.MusicThread;
import edu.hitsz.supply.*;
import edu.hitsz.swing.EndEasyMenu;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public class Game extends JPanel {
    private JPanel mainPanel;
    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractSupply> supplies;

    private boolean isPlay;


    /**
     * 屏幕中出现的敌机最大数量
     */
    private int enemyMaxNumber = 5;

    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 游戏日期
     */
    private String formattedDateTime;
    /**
     * 当前时刻
     */
    private int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;
    /**
     * 随机数
     */
    private double random = 0.5;

    public int difficulty;

    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;

    public Game(int difficulty,boolean isPlay) {
        this.isPlay=isPlay;
        this.difficulty=difficulty;
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
        new HeroController(this, heroAircraft);

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        MusicThread bgm = new MusicThread("src/videos/bgm.wav",isPlay,true);
        bgm.start();
        MusicThread bgm_boss=new MusicThread("src/videos/bgm_boss.wav",isPlay,true);
        bgm_boss.start();
        bgm_boss.stopPlaying();
        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;
            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    AAFactory aaFactory;
                    AbstractAircraft abstractAircraft;
                    if(Math.random()<0.60) {
                        aaFactory = new MEFactory();
                        abstractAircraft = aaFactory.creatAircraft();
                        enemyAircrafts.add(abstractAircraft );
                    }
                    else if(Math.random()>=0.60 && Math.random()<0.80) {
                        aaFactory = new EEFactory();
                        abstractAircraft = aaFactory.creatAircraft();
                        enemyAircrafts.add(abstractAircraft );
                    }
                    else if(Math.random()>=0.80){
                        aaFactory = new EPFactor();
                        abstractAircraft = aaFactory.creatAircraft();
                        enemyAircrafts.add(abstractAircraft);
                    }
                    boolean if_boss=false;
                    for(AbstractAircraft enemyAircraft : enemyAircrafts){
                        if (enemyAircraft instanceof Boss)
                        {
                            if_boss = true;
                            break;
                        }
                    }
                    if(!if_boss) {
                        bgm_boss.stopPlaying();
                        if (score > 0 && (score % 50 == 0 || score % 210 == 0 || score % 220 == 0)){
                            aaFactory = new BFactory();
                            abstractAircraft = aaFactory.creatAircraft();
                            enemyAircrafts.add(abstractAircraft);
                            bgm_boss.resumePlaying();
                        }
                    }
                }
                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            //道具移动
            suppliesMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;

                bgm.stopPlaying();
                bgm_boss.stopPlaying();
                MusicThread gameOver = new MusicThread("src/videos/game_over.wav",isPlay,false);
                gameOver.start();

                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
                formattedDateTime = currentDateTime.format(formatter);

                System.out.println("Game Over!");

                Main.cardPanel.add(new EndEasyMenu(difficulty,score,formattedDateTime).getMainPanel());
                Main.cardLayout.last(Main.cardPanel);

            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        //  敌机射击
        for (AbstractAircraft aircraft : enemyAircrafts) {
            enemyBullets.addAll(aircraft.shoot());
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void suppliesMoveAction() {
        for (AbstractSupply supply : supplies) {
            supply.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        //  敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }

        }
        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    MusicThread bullet_hit = new MusicThread("src/videos/bullet_hit.wav",isPlay,false);
                    bullet_hit.start();

                    if (enemyAircraft.notValid()) {
                        //  获得分数，产生道具补给
                        if(enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof ElitePlus){
                            score += 20;
                            random = Math.random();
                            AbstractSupply abstractSupply;
                            ASFactory asFactory;
                            if(random<0.20) {;}
                            else if (0.20 <= random && random<0.40) {
                                asFactory = new BSFactory();
                                abstractSupply = asFactory.creatSupply(enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY());
                                supplies.add(abstractSupply);
                            }
                            else if (0.40 <= random && random<0.60) {
                                asFactory = new FSFactory();
                                abstractSupply = asFactory.creatSupply(enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY());
                                supplies.add(abstractSupply);
                            }
                            else if (0.60 <= random && random<0.80) {
                                asFactory = new FPSFactor();
                                abstractSupply = asFactory.creatSupply(enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY());
                                supplies.add(abstractSupply);
                            }
                            else {
                                asFactory = new HSFactory();
                                abstractSupply = asFactory.creatSupply(enemyAircraft.getLocationX(),
                                        enemyAircraft.getLocationY());
                                supplies.add(abstractSupply);
                            }
                        } else if (enemyAircraft instanceof  Boss) {
                            score +=30;
                            int ran = (int)(Math.random()*4);
                            for (int i = 0; i < ran ; i++){
                                AbstractSupply abstractSupply;
                                ASFactory asFactory;
                                random = Math.random();
                                if(random<0.10) {;}
                                else if (0.10 <= random && random<0.40) {
                                    asFactory = new BSFactory();
                                    abstractSupply = asFactory.creatSupply(enemyAircraft.getLocationX() + (i*2 - ran + 1)*25,
                                            enemyAircraft.getLocationY());
                                    supplies.add(abstractSupply);
                                }
                                else if (0.40 <= random && random<0.70) {
                                    asFactory = new FSFactory();
                                    abstractSupply = asFactory.creatSupply(enemyAircraft.getLocationX() + (i*2 - ran + 1)*25,
                                            enemyAircraft.getLocationY());
                                    supplies.add(abstractSupply);
                                }
                                else {
                                    asFactory = new HSFactory();
                                    abstractSupply = asFactory.creatSupply(enemyAircraft.getLocationX() + (i*2 - ran + 1)*25,
                                            enemyAircraft.getLocationY());
                                    supplies.add(abstractSupply);
                                }
                            }
                        } else {
                            score += 10;
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        //  我方获得道具，道具生效
        for(AbstractSupply supply : supplies) {
            if (supply.notValid()) {
                continue;
            }
            if (supply.crash(heroAircraft) || heroAircraft.crash(supply)){
                supply.vanish();
                supply.effect(heroAircraft);
                MusicThread getSupply = new MusicThread("src/videos/get_supply.wav",isPlay,false);
                getSupply.start();
                if(supply instanceof BombSupply){
                    MusicThread bomb = new MusicThread("src/videos/bomb_explosion.wav",isPlay,false);
                    bomb.start();
                }
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supplies.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(difficulty==0){
        // 绘制背景,图片滚动
            g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
            this.backGroundTop += 1;
            if (this.backGroundTop == Main.WINDOW_HEIGHT) {
                this.backGroundTop = 0;
            }
        }
        else if (difficulty==1) {
            g.drawImage(ImageManager.BACKGROUND_IMAGE_NOLMAL, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(ImageManager.BACKGROUND_IMAGE_NOLMAL, 0, this.backGroundTop, null);
            this.backGroundTop += 1;
            if (this.backGroundTop == Main.WINDOW_HEIGHT) {
                this.backGroundTop = 0;
            }
        }
        else{
            g.drawImage(ImageManager.BACKGROUND_IMAGE_DIFFICULT, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
            g.drawImage(ImageManager.BACKGROUND_IMAGE_DIFFICULT, 0, this.backGroundTop, null);
            this.backGroundTop += 1;
            if (this.backGroundTop == Main.WINDOW_HEIGHT) {
                this.backGroundTop = 0;
            }
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, supplies);


        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
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

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }

}

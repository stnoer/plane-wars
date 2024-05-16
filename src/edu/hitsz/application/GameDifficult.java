package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.music.MusicThread;
import edu.hitsz.observer.BulletsEnermyObserver;
import edu.hitsz.observer.EnermyObserver;
import edu.hitsz.supply.*;
import edu.hitsz.swing.EndEasyMenu;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/*
*
游戏界面中出现的敌机数量的最大值
敌机血量
精英敌机的产生概率
普通和精英敌机的产生周期
Boss敌机产生的得分阈值
Boss敌机每次出现的血量
* */
public class GameDifficult extends AbstractGame{

    private int BossHp = 100;
    public GameDifficult(int difficulty, boolean isPlay) {
        super(difficulty, isPlay);
        enemyMaxNumber = 5;
        timeInterval = 35;
    }

    @Override
    public void action() {
        MusicThread bgm = new MusicThread("src/videos/bgm.wav", isPlay, true);
        bgm.start();
        MusicThread bgm_boss = new MusicThread("src/videos/bgm_boss.wav", isPlay, true);
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
                    if (Math.random() < 0.50) {
                        aaFactory = new MEFactory();
                        abstractAircraft = aaFactory.creatAircraft();
                        abstractAircraft.setMaxHP(40);
                        enemyAircrafts.add(abstractAircraft);
                    } else if (Math.random() >= 0.50 && Math.random() < 0.75) {
                        aaFactory = new EEFactory();
                        abstractAircraft = aaFactory.creatAircraft();
                        abstractAircraft.setMaxHP(70);
                        enemyAircrafts.add(abstractAircraft);
                    } else if (Math.random() >= 0.75) {
                        aaFactory = new EPFactor();
                        abstractAircraft = aaFactory.creatAircraft();
                        abstractAircraft.setMaxHP(70);
                        enemyAircrafts.add(abstractAircraft);
                    }
                    boolean if_boss = false;
                    for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                        if (enemyAircraft instanceof Boss) {
                            if_boss = true;
                            break;
                        }
                    }
                    if (!if_boss) {
                        bgm_boss.stopPlaying();
                        if (score > 0 && (score % 40 == 0 || score % 210 == 0 || score % 220 == 0)) {
                            aaFactory = new BFactory();
                            abstractAircraft = aaFactory.creatAircraft();
                            BossHp=+10;
                            abstractAircraft.setMaxHP(BossHp);
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
                MusicThread gameOver = new MusicThread("src/videos/game_over.wav", isPlay, false);
                gameOver.start();

                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
                formattedDateTime = currentDateTime.format(formatter);

                System.out.println("Game Over!");

                Main.cardPanel.add(new EndEasyMenu(difficulty, score, formattedDateTime).getMainPanel());
                Main.cardLayout.last(Main.cardPanel);

            }
        };
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void crashCheckAction() {
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
                if(supply instanceof BombSupply){
                    for (BaseBullet baseBullet:enemyBullets)
                    {
                        BulletsEnermyObserver bulletsEnermyObserver = new BulletsEnermyObserver(baseBullet);
                        ((BombSupply) supply).addObserver(bulletsEnermyObserver);
                    }
                    for(AbstractAircraft abstractAircraft : enemyAircrafts){
                        if(abstractAircraft instanceof MobEnemy||abstractAircraft instanceof EliteEnemy||abstractAircraft instanceof  ElitePlus){
                            if(abstractAircraft instanceof MobEnemy)
                                score+=10;
                            else
                                score += 20;
                            EnermyObserver enermyObserver = new EnermyObserver(abstractAircraft);
                            ((BombSupply) supply).addObserver(enermyObserver);
                        }
                    }
                    ((BombSupply) supply).notifyAllObserver();
                }
                else{supply.effect(heroAircraft);}
                MusicThread getSupply = new MusicThread("src/videos/get_supply.wav",isPlay,false);
                getSupply.start();
                if(supply instanceof BombSupply){
                    MusicThread bomb = new MusicThread("src/videos/bomb_explosion.wav",isPlay,false);
                    bomb.start();
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(ImageManager.BACKGROUND_IMAGE_DIFFICULT, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE_DIFFICULT, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
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
}

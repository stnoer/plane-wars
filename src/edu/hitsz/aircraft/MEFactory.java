package edu.hitsz.aircraft;

public class MEFactory implements AAFactory {
    public AbstractAircraft creatAircraft() {
        return new MobEnemy();
    }
}

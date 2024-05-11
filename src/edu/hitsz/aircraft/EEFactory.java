package edu.hitsz.aircraft;

public class EEFactory implements AAFactory {
    @Override
    public AbstractAircraft creatAircraft() {
        return new EliteEnemy();
    }
}

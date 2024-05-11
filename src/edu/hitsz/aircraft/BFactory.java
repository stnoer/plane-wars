package edu.hitsz.aircraft;

public class BFactory implements AAFactory {
    @Override
    public AbstractAircraft creatAircraft() {
        return new Boss();
    }
}

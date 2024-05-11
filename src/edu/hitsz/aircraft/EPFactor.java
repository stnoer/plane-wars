package edu.hitsz.aircraft;

public class EPFactor implements AAFactory {
    @Override
    public AbstractAircraft creatAircraft() {
        return new ElitePlus();
    }
}

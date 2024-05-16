package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class BombSupply extends AbstractSupply{
    private final List<Observer> bombObserverlist = new ArrayList<>();
    public BombSupply(int locationX, int locationY) {
        super(locationX, locationY, 0, 5);
    }
    public void addObserver(Observer observer)
    {
        bombObserverlist.add(observer);
    }

    public void removeObserver(Observer observer)
    {
        bombObserverlist.remove(observer);
    }

    public void notifyAllObserver()
    {
        for(Observer observer :bombObserverlist)
            observer.update();
    }

    @Override
    public void effect(HeroAircraft heroAircraft) {
        ;
    }
}

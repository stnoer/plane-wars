package edu.hitsz.observer;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.ElitePlus;
import edu.hitsz.aircraft.MobEnemy;

public class EnermyObserver implements Observer{
    private final AbstractAircraft abstractAircraft;
    public EnermyObserver(AbstractAircraft abstractAircraft)
    {
        this.abstractAircraft=abstractAircraft;
    }
    @Override
    public  void update() {
        if(abstractAircraft instanceof MobEnemy || abstractAircraft instanceof EliteEnemy)
        {
            abstractAircraft.vanish();
        }
        else if(abstractAircraft instanceof ElitePlus)
        {
            abstractAircraft.decreaseHp(20);
        }
    }
}

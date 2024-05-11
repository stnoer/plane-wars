package edu.hitsz.supply;

public class FPSFactor implements ASFactory{
    public AbstractSupply creatSupply(int locationX, int locationY) {
        return new FirePlusSupply(locationX,locationY);
    }
}
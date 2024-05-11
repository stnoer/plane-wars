package edu.hitsz.supply;

public class FSFactory implements ASFactory{
    public AbstractSupply creatSupply(int locationX, int locationY) {
        return new FireSupply(locationX,locationY);
    }
}

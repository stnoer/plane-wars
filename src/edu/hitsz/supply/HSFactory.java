package edu.hitsz.supply;

public class HSFactory implements ASFactory{
    public AbstractSupply creatSupply(int locationX,int locationY){
        return new HpSupply(locationX,locationY);
    }
}

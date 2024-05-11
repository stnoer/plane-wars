package edu.hitsz.supply;

public class BSFactory  implements ASFactory{
    public AbstractSupply creatSupply(int locationX, int locationY){
            return new BombSupply(locationX,locationY);
    }
}

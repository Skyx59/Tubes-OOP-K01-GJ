package controller;

import entity.Chef;
import station.Station;

public class StationController {

    public void  interact(Station station, Chef chef){
        if(chef.isBusy()) return;
        if(!station.isChefAdjacent(chef)) return;
        station.interact(chef);
    }
}

package controller;

import java.util.List;

import station.CookingStation;
import station.Station;

public class CookingController {

    public void tick(List<Station> stations){
        for(Station s : stations){
            if(s instanceof CookingStation cs){
                cs.updateCooking();
            }
        }
    }
}

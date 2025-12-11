package controller;

import entity.Chef;
import entity.Player;
import station.Station;
import world.TileManager;

public class InputController {

    private final StationController stationController;
    private final InventoryController inventoryController;
    private final TileManager tileManager;

    public InputController(TileManager tm){
        this.stationController = new StationController();
        this.inventoryController = new InventoryController();
        this.tileManager = tm;
    }

    public void onInteract(Player p, Chef chef){
        Station station = tileManager.getStationInFrontOf(p);

        if(station != null){
            stationController.interact(station, chef);
            return;
        }

        if(chef.getInventory() != null){
            inventoryController.drop(chef);
        }
    }
}

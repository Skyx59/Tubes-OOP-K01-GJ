package station;

import controller.ServingController;
import entity.Chef;
import entity.item.kitchen.Plate;

public class ServingCounter extends Station {

    private ServingController controller;

    public void bind(ServingController sc){
        this.controller = sc;
    }

    @Override
    public void interact(Chef chef){
        if(controller == null) return;

        if(chef.getInventory() instanceof Plate){
            controller.serve(chef);
        }
    }
}

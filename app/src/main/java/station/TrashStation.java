package station;

import entity.Chef;
import entity.item.kitchen.KitchenUtensil;

public class TrashStation extends Station {

    @Override
    public void interact(Chef chef){
        if(chef.getInventory() == null) return;

        if(chef.getInventory() instanceof KitchenUtensil ut){
            ut.getContents().clear();
        } else {
            chef.setInventory(null);
        }
    }
}

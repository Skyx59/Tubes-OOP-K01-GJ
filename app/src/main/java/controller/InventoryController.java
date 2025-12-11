package controller;

import entity.Chef;
import entity.item.Item;
import entity.item.ingredient.Preparable;
import entity.item.kitchen.KitchenUtensil;
import entity.item.kitchen.Plate;

public class InventoryController {

    public void drop(Chef chef){
        chef.setInventory(null);
    }

    public void pick(Chef chef, Item item){
        if(chef.getInventory() == null){
            chef.setInventory(item);
        }
    }

    public boolean give(Chef from, Chef to){
        if(from.getInventory() == null) return false;
        if(to.getInventory() != null) return false;
        to.setInventory(from.getInventory());
        from.setInventory(null);
        return true;
    }

    public boolean addToPlate(Plate plate, Preparable prep){
        if(plate.isDirty()) return false;
        plate.addIngredient(prep);
        return true;
    }

    public boolean putUtensilOnStation(KitchenUtensil utensil, Chef chef){
        if(chef.getInventory() != utensil) return false;
        chef.setInventory(null);
        return true;
    }
}

package controller;

import entity.item.ingredient.Preparable;
import entity.item.kitchen.Plate;

public class PlateController {

    public boolean addToPlate(Plate plate, Preparable prep){
        if(plate.isDirty()) return false;
        plate.addIngredient(prep);
        return true;
    }
}

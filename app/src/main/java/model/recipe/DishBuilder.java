package model.recipe;

import entity.item.ingredient.Preparable;
import entity.item.kitchen.Plate;

public final class DishBuilder {

    private DishBuilder(){}

    public static void addToPlate(Plate plate, Preparable prep){
        if(plate.isDirty()) throw new IllegalStateException("Cannot plate on dirty plate.");
        plate.addIngredient(prep);
    }
}

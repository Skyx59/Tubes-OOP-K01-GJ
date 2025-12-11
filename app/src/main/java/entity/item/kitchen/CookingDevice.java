package entity.item.kitchen;

import entity.item.ingredient.Preparable;

public interface CookingDevice {

    boolean isPortable();
    int capacity();
    boolean canAccept(Preparable ingredient);

    void addIngredient(Preparable ingredient);
    void startCooking();
}
package entity.item.kitchen;

import java.util.Set;

import entity.item.ingredient.Ingredient;
import entity.item.ingredient.IngredientState;
import entity.item.ingredient.Preparable;
import util.Constants;

public class FryingPan extends KitchenUtensil implements CookingDevice {

    public FryingPan(){
        super("FryingPan");
    }

    @Override
    public boolean isPortable(){
        return true;
    }

    @Override
    public int capacity(){
        return 1;
    }

    @Override
    public boolean canAccept(Preparable ingredient){
        return (ingredient instanceof Ingredient ing)
                && ing.getState() == IngredientState.CHOPPED;
    }

    @Override
    public void addIngredient(Preparable ingredient){
        if(getContentsCount() >= 1)
            throw new IllegalStateException("Frying pan sudah penuh.");

        super.addIngredient(ingredient);
    }

    @Override
    public void startCooking(){
        if(!isCooking)
            startCookTimer();
    }

    /**
     * Dipanggil setiap frame oleh CookingStation atau CookingController.
     */
    public void updateCooking(){
        if(!isCooking)
            return;

        Set<Preparable> set = getContents();
        if(set.isEmpty()){
            isCooking = false;
            return;
        }

        Preparable p = set.iterator().next();
        if(!(p instanceof Ingredient ing))
            return;

        long elapsed = System.currentTimeMillis() - cookingStartTime;

        if(elapsed >= Constants.BURNED_MS){
            ing.setState(IngredientState.BURNED);
            isCooking = false;
        }
        else if(elapsed >= Constants.COOKED_MS){
            ing.setState(IngredientState.COOKED);
        }
        else {
            ing.setState(IngredientState.COOKING);
        }
    }
}

package entity.item.ingredient;

import entity.item.Item;
import util.Constants;

public class Ingredient extends Item implements Preparable {

    private IngredientState state;
    private double cookingPercentage;
    private final boolean isChoppable;
    private final boolean isCookable;

    public Ingredient(String name, boolean isChoppable, boolean isCookable){
        super(name);
        this.state = IngredientState.RAW;
        this.cookingPercentage = 0.0;
        this.isChoppable = isChoppable;
        this.isCookable = isCookable;
    }

    public double getCookingPercentage(){
        return this.cookingPercentage;
    }
    
    public synchronized IngredientState getState(){ return state; }
    public  void setState(IngredientState state){ 
    this.state = state;
    }

    @Override
    public synchronized boolean canBeChopped(){
        return isChoppable && state == IngredientState.RAW;
    }

    @Override
    public synchronized boolean canBeCooked(){
        return isCookable && state != IngredientState.BURNED;
    }

    @Override
    public synchronized boolean canBePlacedOnPlate(){ return true; }

    @Override
    public synchronized void chop(){
        if(!isChoppable) throw new IllegalStateException("Cannot chop");
        if(state != IngredientState.RAW) throw new IllegalStateException("Not RAW");
        this.state = IngredientState.CHOPPED;
    }

    @Override
    public synchronized void cook(){
        if(!isCookable) throw new IllegalStateException("Cannot cook");
        if(state == IngredientState.BURNED) throw new IllegalStateException("Already burned");
        this.state = IngredientState.COOKING;
    }

    public synchronized void cookProgress(long elapsedMs){
        if(!isCookable) return;
        if(state == IngredientState.BURNED) return;

        if(state == IngredientState.RAW) state = IngredientState.COOKING;

        double addPercent = (double) elapsedMs * 100.0 / (double) Constants.COOKED_MS;
        cookingPercentage += addPercent;

        if(cookingPercentage >= 150.0){
            state = IngredientState.BURNED;
            cookingPercentage = 150.0;
        }
        else if(cookingPercentage >= 100.0){
            if(state != IngredientState.COOKED){
                state = IngredientState.COOKED;
            }
        }
    }

    @Override
    public synchronized boolean isValid(){
        return state != IngredientState.BURNED;
    }
}


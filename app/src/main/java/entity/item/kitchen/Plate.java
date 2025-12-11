package entity.item.kitchen;

import java.util.Set;

import entity.item.ingredient.Preparable;

public class Plate extends KitchenUtensil {

    private boolean dirty = false;

    public Plate(){
        super("Plate");
    }

    public Set<Preparable> getIngredients(){
        return getContents();
    }

    public void addIngredient(Preparable p){
        if(dirty) throw new IllegalStateException("Cannot add to dirty plate.");
        super.addIngredient(p);
    }

    public boolean isDirty(){ return dirty; }
    public boolean isClean(){ return !dirty; }

    public void markDirty(){ dirty = true; }

    public void clean(){
        dirty = false;
        contents.clear();
    }

    @Override
    public boolean isValid(){
        return isClean() && getContentsCount() > 0;
    }
}

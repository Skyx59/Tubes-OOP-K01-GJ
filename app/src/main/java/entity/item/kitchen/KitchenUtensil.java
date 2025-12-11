package entity.item.kitchen;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import entity.item.Item;
import entity.item.ingredient.Preparable;

public class KitchenUtensil extends Item {

    protected final Set<Preparable> contents;
    protected long cookingStartTime = 0;
    protected boolean isCooking = false;

    public KitchenUtensil(String name){
        super(name);
        this.contents = Collections.synchronizedSet(new HashSet<>());
    }

    public Set<Preparable> getContents(){
        synchronized(contents){
            return new HashSet<>(contents);
        }
    }

    public int getContentsCount(){
        synchronized(contents){
            return contents.size();
        }
    }

    public void addIngredient(Preparable p){
        synchronized(contents){
            contents.add(p);
        }
    }

    public void removeIngredient(Preparable p){
        synchronized(contents){
            contents.remove(p);
        }
    }

    public boolean isCooking(){
        return isCooking;
    }

    public long getCookingStartTime(){
        return cookingStartTime;
    }

    public void startCookTimer(){
        isCooking = true;
        cookingStartTime = System.currentTimeMillis();
    }

    /**
     * Update progress cooking untuk seluruh bahan.
     * Tidak menghentikan cooking meski chef pergi (sesuai spesifikasi Anda).
     */
    public void updateCooking(long elapsedMs){
        if(!isCooking) return;

        synchronized(contents){
            for(Preparable p : contents){
                p.cookProgress(elapsedMs); // method dari Ingredient
            }
        }
    }

    @Override
    public boolean isValid(){
        return true;
    }
}

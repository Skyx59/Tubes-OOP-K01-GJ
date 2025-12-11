package station;

import entity.Chef;
import entity.item.kitchen.FryingPan;
import entity.item.kitchen.KitchenUtensil;

public class CookingStation extends Station {

    private KitchenUtensil utensilOnStation = null;

    public boolean hasUtensil(){
        return utensilOnStation != null;
    }
    
    public KitchenUtensil getUtensil(){
        return utensilOnStation;
    }

    public void placeUtensil(KitchenUtensil u){
        this.utensilOnStation = u;
    }

    public void removeUtensil(){
        this.utensilOnStation = null;
    }

    @Override
    public synchronized void interact(Chef chef){

        var inv = chef.getInventory();

        // Chef membawa pan → taruh di station
        if(inv instanceof FryingPan pan){
            if(utensilOnStation == null){
                utensilOnStation = pan;
                chef.setInventory(null);
            }
            return;
        }

        // Chef membawa ingredient → masukkan ke frying pan
        if(inv instanceof entity.item.ingredient.Preparable prep){
            if(utensilOnStation instanceof FryingPan pan){
                if(!pan.canAccept(prep))
                    throw new IllegalStateException("Ingredient tidak cocok untuk frying pan.");

                pan.addIngredient(prep);
                chef.setInventory(null);
                return;
            }
        }

        // Chef tidak membawa apa-apa → ambil pan bila ada
        if(inv == null && utensilOnStation instanceof FryingPan pan){
            chef.setInventory(pan);
            utensilOnStation = null;
            return;
        }

        // Mulai cooking jika pan ada
        if(utensilOnStation instanceof FryingPan pan){
            pan.startCooking();
        }
    }

    public void updateCooking(){
        if(utensilOnStation instanceof FryingPan pan)
            pan.updateCooking();
    }
}

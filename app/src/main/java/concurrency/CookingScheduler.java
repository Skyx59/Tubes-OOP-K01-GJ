package concurrency;

import java.util.ArrayList;
import java.util.List;

import entity.item.ingredient.Ingredient;
import entity.item.ingredient.IngredientState;
import entity.item.ingredient.Preparable;
import entity.item.kitchen.KitchenUtensil;
import util.Constants;

public class CookingScheduler {

    private static final List<KitchenUtensil> tracked = new ArrayList<>();

    public static void track(KitchenUtensil u){
        if(!tracked.contains(u)){
            tracked.add(u);
        }
    }

    public static void start(){
        Thread t = new Thread(() -> {
            while(true){
                try {
                    Thread.sleep(100);
                } catch(Exception e){}

                long now = System.currentTimeMillis();

                for(KitchenUtensil u : tracked){
                    if(!u.isCooking()) continue;

                    long elapsed = now - u.getCookingStartTime();

                    for(Preparable p : u.getContents()){
                        if(p instanceof Ingredient ing){
                            if(elapsed >= Constants.BURNED_MS){
                                ing.setState(IngredientState.BURNED);
                            }
                            else if(elapsed >= Constants.COOKED_MS){
                                ing.setState(IngredientState.COOKED);
                            }
                        }
                    }
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }
}

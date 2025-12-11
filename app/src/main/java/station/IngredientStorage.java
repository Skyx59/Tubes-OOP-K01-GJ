package station;

import entity.Chef;
import entity.item.Item;
import entity.item.ingredient.Ingredient;

public class IngredientStorage extends Station {

    private final String ingredientName;
    private final boolean chop;
    private final boolean cook;

    public IngredientStorage(String name, boolean chop, boolean cook){
        this.ingredientName = name;
        this.chop = chop;
        this.cook = cook;
    }

    @Override
    public void interact(Chef chef){
        if(chef.getInventory() == null){
            chef.setInventory(new Ingredient(ingredientName, chop, cook));
        }
    }

    public Item dispense(){
        return new Ingredient(ingredientName, chop, cook);
    }
}

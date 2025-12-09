package BaseItem;
import java.util.ArrayList;
import java.util.List;

public class Dish extends Item {
    private List<Ingredient> ingredients;

    public Dish(String name) {
        super(name);
        this.ingredients = new ArrayList<>();
    }


    public void setIngredients(List<Ingredient> ingredientsFromPlate) {
        this.ingredients = new ArrayList<>(ingredientsFromPlate);
        System.out.println("Dish " + getName() + " siap disajikan.");
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean isValid() {
        // 1. Gak boleh piring kosong
        if (ingredients.isEmpty()) return false;
        
        // 2. Cek satu-satu isinya
        for (Ingredient i : ingredients) {
            if (!i.isValid()) return false; 
        }
        return true;
    }
}

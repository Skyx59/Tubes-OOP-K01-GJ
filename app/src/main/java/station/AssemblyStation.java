package station;

import entity.Chef;
import entity.item.ingredient.Ingredient;
import entity.item.ingredient.IngredientState;
import entity.item.ingredient.Preparable;
import entity.item.kitchen.Plate;

public class AssemblyStation extends Station {

    private Plate plateOnStation;

    @Override
    public synchronized void interact(Chef chef){

        // === 1. Chef membawa Plate bersih → letakkan ke station ===
        if(chef.getInventory() instanceof Plate plate) {

            if(plate.isDirty())
                throw new IllegalStateException("Tidak bisa meletakkan piring kotor di Assembly Station.");

            if(plateOnStation == null) {
                plateOnStation = plate;
                chef.setInventory(null);
            }
            return;
        }

        // === 2. Chef membawa ingredient → masukkan ke piring ===
        if(chef.getInventory() instanceof Preparable prep) {

            if(plateOnStation == null)
                throw new IllegalStateException("Tidak ada plate di assembly station.");

            if(!plateOnStation.isClean())
                throw new IllegalStateException("Plate di station kotor.");

            validateIngredientState(prep);

            plateOnStation.addIngredient(prep);
            chef.setInventory(null);
            return;
        }

        // === 3. Chef tidak membawa apapun → ambil plate dari station ===
        if(chef.getInventory() == null && plateOnStation != null){
            chef.setInventory(plateOnStation);
            plateOnStation = null;
        }
    }

    /**
     * Validasi state ingredient sesuai Opsi A:
     * Patty → HARUS COOKED
     * Lettuce → HARUS CHOPPED
     * Tomato → HARUS CHOPPED
     * Cheese → HARUS CHOPPED
     * Bun → selalu valid
     */
    private void validateIngredientState(Preparable p){

        if(!(p instanceof Ingredient ing))
            return; // dish atau lainnya

        String name = ing.toString();
        IngredientState st = ing.getState();

        switch(name){

            case "Patty" -> {
                if(st != IngredientState.COOKED)
                    throw new IllegalStateException("Patty harus COOKED sebelum dirakit.");
            }

            case "Lettuce", "Tomato", "Cheese" -> {
                if(st != IngredientState.CHOPPED)
                    throw new IllegalStateException(name + " harus CHOPPED sebelum dirakit.");
            }

            case "Bun" -> {
                // Always valid
            }

            default -> throw new IllegalStateException("Ingredient tidak valid untuk Burger: " + name);
        }
    }
}

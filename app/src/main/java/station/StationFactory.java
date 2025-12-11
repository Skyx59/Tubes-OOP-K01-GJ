package station;

public final class StationFactory {

    private StationFactory(){}

    // Untuk map C (burger): ada beberapa I (Ingredient Storage)
    // Kita assign berdasarkan urutan kemunculan 7 di file map.
    private static int ingredientStorageCounter = 0;

    public static Station create(int tileId) {
        return switch(tileId){
            case 2 -> new CuttingStation();
            case 3 -> new CookingStation();
            case 4 -> new AssemblyStation();
            case 5 -> new ServingCounter();
            case 6 -> new WashingStation();
            case 7 -> createBurgerIngredientStorage(); 
            case 8 -> new PlateStorage(4);
            case 9 -> new TrashStation();
            default -> null;
        };
    }

    private static IngredientStorage createBurgerIngredientStorage(){

        // Urutan kemunculan tile "7" (Ingredient Storage) di map C:
        // 1) Tomat   (perlu dipotong)
        // 2) Daging  (perlu dipotong lalu dimasak)
        // 3) Roti    (langsung dipakai, tidak chop/cook)
        // 4) Keju    (perlu dipotong)
        // 5) Lettuce (perlu dipotong)

        return switch(ingredientStorageCounter++){
            case 0 -> new IngredientStorage("Tomat",   true,  false);
            case 1 -> new IngredientStorage("Daging",  true,  true);
            case 2 -> new IngredientStorage("Roti",    false, false);
            case 3 -> new IngredientStorage("Keju",    true,  false);
            case 4 -> new IngredientStorage("Lettuce", true,  false);
            default -> new IngredientStorage("DEFAULT", true, true);
        };
    }
}

package model.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BurgerRecipes {

    private BurgerRecipes(){}

    // Nama ingredient diseragamkan sebagai String
    // agar mudah dicocokkan dengan Preparable.toString() / getName()
    public static final String ROTI = "Roti";
    public static final String DAGING = "Daging";
    public static final String KEJU = "Keju";
    public static final String LETTUCE = "Lettuce";
    public static final String TOMAT = "Tomat";

    public static Recipe classicBurger(){
        List<String> ing = Arrays.asList(ROTI, DAGING);
        return new Recipe("Classic Burger", ing);
    }

    public static Recipe cheeseBurger(){
        List<String> ing = Arrays.asList(ROTI, DAGING, KEJU);
        return new Recipe("Cheeseburger", ing);
    }

    public static Recipe bltBurger(){
        List<String> ing = Arrays.asList(ROTI, LETTUCE, TOMAT, DAGING);
        return new Recipe("BLT Burger", ing);
    }

    public static Recipe deluxeBurger(){
        List<String> ing = Arrays.asList(ROTI, LETTUCE, DAGING, KEJU);
        return new Recipe("Deluxe Burger", ing);
    }

    public static List<Recipe> allBurgerRecipes(){
        List<Recipe> list = new ArrayList<>();
        list.add(classicBurger());
        list.add(cheeseBurger());
        list.add(bltBurger());
        list.add(deluxeBurger());
        return list;
    }
}

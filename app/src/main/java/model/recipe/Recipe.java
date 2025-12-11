package model.recipe;

import java.util.List;

import entity.item.ingredient.Preparable;

public class Recipe {

    private String name;
    private List<String> required;

    public Recipe(String name, List<String> required){
        this.name = name;
        this.required = required;
    }

    public boolean matches(List<Preparable> comps){
        return comps.stream().map(c->c.toString()).toList().containsAll(required);
    }

    public String getName(){
        return name;
    }
}

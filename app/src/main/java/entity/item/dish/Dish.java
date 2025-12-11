package entity.item.dish;

import java.util.ArrayList;
import java.util.List;

import entity.item.Item;
import entity.item.ingredient.Preparable;

public class Dish extends Item {

    private List<Preparable> components;

    public Dish(String name){
        super(name);
        this.components = new ArrayList<>();
    }

    public void addComponent(Preparable component){
        if(component == null) return;
        components.add(component);
    }

    public List<Preparable> getComponents(){
        return new ArrayList<>(components);
    }

    @Override
    public boolean isValid(){
        return !components.isEmpty();
    }
}

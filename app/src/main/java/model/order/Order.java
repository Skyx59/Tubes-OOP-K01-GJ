package model.order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.item.ingredient.Preparable;
import entity.item.kitchen.Plate;

public class Order {

    private final String name;
    private final List<String> required;
    private long remainingMs;

    public Order(String name, List<String> required, long durationMs){
        this.name = name;
        this.required = required;
        this.remainingMs = durationMs;
    }

    public void tick(long deltaMs){
        remainingMs -= deltaMs;
    }

    public boolean isExpired(){
        return remainingMs <= 0;
    }

    public long getRemainingMs(){
        return remainingMs;
    }

    public String getName(){
        return name;
    }

    public List<String> getRequired(){
        return required;
    }

    public boolean matchesPlate(Plate plate){
        Set<Preparable> comps = plate.getContents();
        if(comps.isEmpty()) return false;

        Set<String> compNames = new HashSet<>();
        for(Preparable p : comps){
            // Preparable tidak punya getName() → gunakan toString()
            compNames.add(p.toString());
        }

        return compNames.containsAll(required);
    }
}

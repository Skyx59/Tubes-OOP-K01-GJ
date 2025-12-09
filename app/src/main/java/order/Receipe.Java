package Order;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String name;
    private List<String> requiredIngredinents;
    
    public Recipe(String name, List<String>ingredients){
        this.name = name;
        this.requiredIngredinents = new ArrayList<>(ingredients);
    }
    public String getName(){
        return name;
    }
    public List<String> getRequiredIngredients(){
        return requiredIngredinents;
    }
}

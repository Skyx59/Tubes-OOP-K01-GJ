import java.util.List;

public class Recipe {
    private String name;
    private List<IngredientRequirement> requirements;

    public Recipe(String name, List<IngredientRequirement> requirements) {
        this.name = name;
        this.requirements = requirements;
    }

    public String getName() {
        return name;
    }

    public List<IngredientRequirement> getRequirements() {
        return requirements;
    }

    /** 
     * Validate if a dish matches all recipe requirements
     */
    public boolean matchDish(Dish dish) {
        List<Preparable> components = dish.getComponents();

        if (components.size() != requirements.size()) {
            return false;
        }

        for (IngredientRequirement req : requirements) {
            boolean found = false;

            for (Preparable ingredient : components) {
                if (ingredient.getName().equalsIgnoreCase(req.getIngredientName()) &&
                        ingredient.getState() == req.getRequiredState()) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }
}

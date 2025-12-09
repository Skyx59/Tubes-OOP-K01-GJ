import java.util.List;

public class Dish extends Item {
    private String name;
    private List<Preparable> components;

    public Dish(String name, List<Preparable> components) {
        super(name);
        this.name = name;
        this.components = components;
    }

    public List<Preparable> getComponents() {
        return components;
    }
}

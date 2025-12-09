import java.util.List;

public class Dish extends Item {
    private List<Preparable> components;

    public Dish(String name, List<Preparable> components) {
        super(name);
        this.components = components;
    }

    public List<Preparable> getComponents() {
        return components;
    }

    @Override
    public boolean isPortable() {
        return true; // Dish selalu bisa dibawa
    }
}

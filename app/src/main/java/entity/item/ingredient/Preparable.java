package entity.item.ingredient;

public interface Preparable {

    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    public abstract IngredientState getState();
    

    void chop() throws IllegalStateException;
    void cook() throws IllegalStateException;
    void cookProgress(long elapsedMs);
}


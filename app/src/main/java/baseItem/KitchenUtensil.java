package BaseItem;

import java.util.ArrayList;
import java.util.List;

public class KitchenUtensil extends Item{
    private List<Ingredient> storage;
    private int capacity;

    public KitchenUtensil(String name, int capacity){
        super(name);
        this.storage = new ArrayList<>();
        this.capacity = capacity;
    }
    public boolean addIngredient(Ingredient ingredient){
        //cek apakah muat
        if(storage.size() < capacity){
            storage.add(ingredient);
            System.out.println(ingredient.getName() + " dimasukkan ke " + this.getName());
            return true;
        }
        System.out.println(this.getName() + " sudah penuh!");
        return false;
    }
    public Ingredient takeIngredient() {
        if (!storage.isEmpty()) {
            // remove(indeks terakhir) = Mengambil tumpukan paling atas (LIFO)
            Ingredient taken = storage.remove(storage.size() - 1);
            System.out.println("Mengambil " + taken.getName() + " dari " + this.getName());
            return taken;
        }
        return null; // Gak ada yang bisa diambil
    }

    public void processCooking(double duration) {

        for (Ingredient ing : storage) {
            ing.cook(duration);
        }
    }
    public List<Ingredient> getContents() {
        return storage;
    }
    @Override
    public boolean isValid() {
        return true;
    }
}

package model.order;

import java.util.List;
import java.util.Random;

public class OrderGenerator {

    private final Random rng = new Random();

    public Order generate(){

        int pick = rng.nextInt(3);

        return switch(pick){

            case 0 -> new Order(
                    "Burger Biasa",
                    List.of("Patty", "Bun"),
                    30000);

            case 1 -> new Order(
                    "Burger Sayur",
                    List.of("Patty", "Lettuce", "Bun"),
                    35000);

            case 2 -> new Order(
                    "Double Patty",
                    List.of("Patty", "Patty", "Bun"),
                    40000);

            default -> throw new IllegalStateException();
        };
    }
}

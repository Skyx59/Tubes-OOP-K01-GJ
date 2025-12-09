package Order;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class OrderGenerator {
    private Queue<Order> orderQueue;      
    private List<Recipe> possibleRecipes; 
    private Random random;

    public OrderGenerator() {
        this.orderQueue = new LinkedList<>();
        this.possibleRecipes = new ArrayList<>();
        this.random = new Random();

        // inisialisasi Resep Burger (Map C)
        initBurgerRecipes();
    }

    private void initBurgerRecipes() {
        // resep 1: Basic Burger
        List<String> r1 = new ArrayList<>();
        r1.add("Bun");
        r1.add("Patty"); 
        possibleRecipes.add(new Recipe("Basic Burger", r1));

        // resep 2: Lettuce Burger
        List<String> r2 = new ArrayList<>();
        r2.add("Bun");
        r2.add("Patty");
        r2.add("Lettuce"); 
        possibleRecipes.add(new Recipe("Lettuce Burger", r2));

        // resep 3: Tomato Burger
        List<String> r3 = new ArrayList<>();
        r3.add("Bun");
        r3.add("Patty");
        r3.add("Tomato"); 
        possibleRecipes.add(new Recipe("Tomato Burger", r3));

        // resep 4: Deluxe Burger
        List<String> r4 = new ArrayList<>();
        r4.add("Bun");
        r4.add("Patty");
        r4.add("Lettuce");
        r4.add("Tomato");
        possibleRecipes.add(new Recipe("Deluxe Burger", r4));
    }

    public void generateOrder() {
        if (possibleRecipes.isEmpty()) return;

        int randomIndex = random.nextInt(possibleRecipes.size());
        Recipe selectedRecipe = possibleRecipes.get(randomIndex);

        int randomTime = 40 + random.nextInt(21);

        Order newOrder = new Order(selectedRecipe, randomTime);
        orderQueue.add(newOrder);

        System.out.println("NEW ORDER: " + selectedRecipe.getName() + " (" + randomTime + "s)");
    }

    public void updateOrders() {
        Iterator<Order> iterator = orderQueue.iterator();

        while (iterator.hasNext()) {
            Order order = iterator.next();
            order.decreaseTime();

            if (order.isExpired()) {
                System.out.println("ORDER EXPIRED: " + order.getRecipe().getName());
                iterator.remove(); 
            }
        }
    }

    public Queue<Order> getOrderQueue() {
        return orderQueue;
    }
}

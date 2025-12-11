package controller;

import java.util.ArrayList;
import java.util.List;

import entity.item.kitchen.Plate;
import model.order.Order;
import model.order.OrderGenerator;
import model.order.ScoreTracker;

public class OrderController {

    private final List<Order> activeOrders = new ArrayList<>();
    private final OrderGenerator generator = new OrderGenerator();
    private final ScoreTracker score = new ScoreTracker();

    private long spawnTimer = 0;
    private static final long ORDER_SPAWN_INTERVAL = 8000; // 8 detik


    public void addOrder(Order o){
        activeOrders.add(o);
    }

    public void removeOrder(Order o){
        activeOrders.remove(o);
    }

    public void tick(long deltaMs){
        // Update countdown setiap order
        List<Order> expired = new ArrayList<>();

        for (Order o : activeOrders){
            o.tick(deltaMs);
            if(o.isExpired()) expired.add(o);
        }

        activeOrders.removeAll(expired);
        for(int i = 0; i < expired.size(); i++){
            score.penalty(10);
        }

        // Spawn order baru
        spawnTimer += deltaMs;
        if(spawnTimer >= ORDER_SPAWN_INTERVAL){
            activeOrders.add(generator.generate());
            spawnTimer = 0;
        }
    }

    public boolean tryServe(Plate plate){
        for(Order o : activeOrders){
            if(o.matchesPlate(plate)){
                activeOrders.remove(o);
                score.reward(20);
                return true;
            }
        }
        score.penalty(5);
        return false;
    }

    public List<Order> getActiveOrders(){
        return new ArrayList<>(activeOrders);
    }

    public ScoreTracker getScore(){
        return score;
    }
}

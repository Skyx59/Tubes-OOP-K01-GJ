package model.order;

import java.util.LinkedList;
import java.util.Queue;

public class OrderManager {

    private Queue<Order> active = new LinkedList<>();
    private int maxActiveOrders = 4;

    public void push(Order o){
        if(active.size() < maxActiveOrders){
            active.add(o);
        }
    }

    public Order peek(){
        return active.peek();
    }

    public Order pop(){
        return active.poll();
    }

    public Queue<Order> getActiveOrders(){
        return active;
    }

    public boolean isEmpty(){
        return active.isEmpty();
    }

    public int size(){
        return active.size();
    }

    public int getMaxActiveOrders(){
        return maxActiveOrders;
    }

    public void setMaxActiveOrders(int max){
        this.maxActiveOrders = max;
    }
}

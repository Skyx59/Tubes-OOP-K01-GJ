package controller;

public class OrderTickController {

    private final OrderController orderController;

    public OrderTickController(OrderController oc){
        this.orderController = oc;
    }

    public void tick(long deltaMs){
        orderController.tick(deltaMs);
    }
}

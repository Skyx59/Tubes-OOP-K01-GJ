package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import controller.OrderController;
import model.order.Order;

public class OrderRenderer {

    private final OrderController orderController;

    public OrderRenderer(OrderController oc){
        this.orderController = oc;
    }

    public void draw(Graphics2D g2, int x, int y){
        g2.setFont(new Font("Arial", Font.BOLD, 16));

        int offsetY = 0;

        for(Order o : orderController.getActiveOrders()){

            long ms = o.getRemainingMs();
            long sec = Math.max(0, ms / 1000);

            // Warna status
            Color color;
            if(ms <= 0){
                color = Color.GRAY;
            }
            else if(ms < 5000){
                color = Color.RED;
            }
            else if(ms < 15000){
                color = Color.YELLOW;
            }
            else{
                color = Color.GREEN;
            }

            g2.setColor(color);
            g2.drawString(o.getName() + " [" + sec + "s]", x, y + offsetY);
            offsetY += 20;
        }
    }
}

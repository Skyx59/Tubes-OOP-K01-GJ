package controller;

import entity.Chef;
import entity.item.kitchen.Plate;

public class ServingController {

    private final OrderController orderController;

    public ServingController(OrderController oc){
        this.orderController = oc;
    }

    /**
     * Dipanggil oleh ServingCounter ketika chef membawa plate.
     */
    public void serve(Chef chef){

        if(!(chef.getInventory() instanceof Plate plate))
            throw new IllegalStateException("Chef tidak membawa plate.");

        boolean success = orderController.tryServe(plate);

        if(success){
            // Berhasil disajikan
            plate.markDirty();
            chef.setInventory(plate);
        } else {
            // Gagal → plate jadi kotor
            plate.markDirty();
            chef.setInventory(plate);
        }
    }
}

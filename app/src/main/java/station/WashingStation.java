package station;

import entity.Chef;
import entity.item.kitchen.Plate;
import util.Constants;

public class WashingStation extends Station {

    private long progressMs = 0;
    private boolean washingInProgress = false;
    private Plate plateBeingWashed = null;

    @Override
    public synchronized void interact(Chef chef){

        // Chef harus adjacent
        if(!isChefAdjacent(chef)) return;

        if(washingInProgress) return;

        // Jika belum ada plate di station, ambil dari chef
        if(plateBeingWashed == null){
            if(!(chef.getInventory() instanceof Plate plate)) return;
            if(!plate.isDirty()) return;

            plateBeingWashed = plate;
            chef.setInventory(null);
        }

        washingInProgress = true;

        new Thread(() -> {
            try{
                long remaining = Constants.WASH_DURATION_MS - progressMs;
                long step = 50;

                while(progressMs < Constants.WASH_DURATION_MS){
                    Thread.sleep(step);

                    // cek adjacency
                    if(!isChefAdjacent(chef)){
                        // pause
                        washingInProgress = false;
                        return;
                    }

                    progressMs += step;
                }

                // selesai
                plateBeingWashed.clean();
                progressMs = Constants.WASH_DURATION_MS;

            }catch(Exception e){
                // abaikan
            } finally {
                washingInProgress = false;
            }
        }).start();
    }
}

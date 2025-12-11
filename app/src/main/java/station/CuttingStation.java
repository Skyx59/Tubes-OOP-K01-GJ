package station;

import entity.Chef;
import entity.item.ingredient.Preparable;
import util.Constants;

public class CuttingStation extends Station {

    private long progressMs = 0;
    private boolean cuttingInProgress = false;

    @Override
    public synchronized void interact(Chef chef){

        // Chef harus adjacent
        if(!isChefAdjacent(chef)) return;

        if(cuttingInProgress) return;

        if(!(chef.getInventory() instanceof Preparable p)) return;
        if(!p.canBeChopped()) return;

        cuttingInProgress = true;

        new Thread(() -> {
            try{
                long remaining = Constants.CUT_DURATION_MS - progressMs;
                long step = 50;

                while(progressMs < Constants.CUT_DURATION_MS){
                    Thread.sleep(step);

                    // cek adjacency
                    if(!isChefAdjacent(chef)){
                        // pause
                        cuttingInProgress = false;
                        return;
                    }

                    progressMs += step;
                }

                // selesai
                p.chop();
                progressMs = Constants.CUT_DURATION_MS;

            }catch(Exception e){
                // abaikan
            } finally {
                cuttingInProgress = false;
            }
        }).start();
    }
}

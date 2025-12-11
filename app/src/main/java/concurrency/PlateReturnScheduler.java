package concurrency;

import entity.item.kitchen.Plate;
import station.PlateStorage;

public final class PlateReturnScheduler {

    private PlateReturnScheduler(){}

    public static void scheduleReturn(PlateStorage storage, Plate plate, long delayMs){

        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                storage.returnDirtyPlate(plate);
            } catch(Exception e){
                // logging jika mau
            }
        }).start();
    }
}


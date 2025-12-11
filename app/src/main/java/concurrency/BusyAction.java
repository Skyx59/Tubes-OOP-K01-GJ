package concurrency;

import entity.Chef;

public final class BusyAction {

    private BusyAction(){}

    public static void runBusy(Chef chef, long durationMs, Runnable action){
        if(chef.isBusy()) return;

        chef.setBusy(true);

        new Thread(() -> {
            try {
                Thread.sleep(durationMs);
                if(action != null){
                    action.run();
                }
            } catch(Exception e){
                // logging jika mau
            } finally {
                chef.setBusy(false);
            }
        }).start();
    }
}


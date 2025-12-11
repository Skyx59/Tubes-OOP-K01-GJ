package controller;

import model.order.StageState;

public class StageController {

    private final StageState stage;

    public StageController(StageState s){
        this.stage = s;
    }

    public boolean isStageOver(){
        return stage.isStageOver();
    }

    public long getRemainingMs(){
        return stage.getRemainingTimeMs();
    }

    public int getScore(){
        return stage.getScore();
    }
}

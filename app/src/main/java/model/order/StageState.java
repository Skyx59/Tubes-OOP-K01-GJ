package model.order;

public class StageState {

    private int score = 0;
    private boolean stageOver = false;
    private boolean stageSuccess = false;
    private int failedOrdersInRow = 0;
    private final int failLimit;
    private final long stageDurationMs;
    private long stageStartTime;

    public StageState(int failLimit, long stageDurationMs){
        this.failLimit = failLimit;
        this.stageDurationMs = stageDurationMs;
        this.stageStartTime = System.currentTimeMillis();
    }

    public int getScore(){ return score; }

    public void addScore(int delta){
        score += delta;
    }

    public void addFail(){
        failedOrdersInRow++;
        if(failedOrdersInRow >= failLimit){
            stageOver = true;
            stageSuccess = false;
        }
    }

    public void resetFail(){
        failedOrdersInRow = 0;
    }

    public boolean isStageOver(){
        if(stageOver) return true;
        long now = System.currentTimeMillis();
        if(now - stageStartTime >= stageDurationMs){
            stageOver = true;
        }
        return stageOver;
    }

    public long getRemainingTimeMs(){
        long now = System.currentTimeMillis();
        long used = now - stageStartTime;
        long remain = stageDurationMs - used;
        return Math.max(remain, 0);
    }

    public boolean isStageSuccess(){
        return stageSuccess;
    }

    public void setStageSuccess(boolean success){
        this.stageSuccess = success;
    }
}

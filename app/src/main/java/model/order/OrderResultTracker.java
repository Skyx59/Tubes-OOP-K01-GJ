package model.order;

public class OrderResultTracker {

    private int successCount = 0;
    private int failedCount = 0;

    public void addSuccess(){
        successCount++;
    }

    public void addFailure(){
        failedCount++;
    }

    public int getSuccessCount(){
        return successCount;
    }

    public int getFailedCount(){
        return failedCount;
    }

    public void reset(){
        successCount = 0;
        failedCount = 0;
    }
}

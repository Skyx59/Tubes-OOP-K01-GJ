package controller;

import model.order.FinalScoreDTO;
import model.order.OrderResultTracker;
import model.order.ScoreTracker;

public class StageEndController {

    private final ScoreTracker scoreTracker;
    private final OrderResultTracker resultTracker;

    private int requiredScore = 300;   // contoh threshold

    public StageEndController(ScoreTracker score, OrderResultTracker tracker){
        this.scoreTracker = score;
        this.resultTracker = tracker;
    }

    public FinalScoreDTO buildResultDTO(){
        int score = scoreTracker.getScore();
        int success = resultTracker.getSuccessCount();
        int failed = resultTracker.getFailedCount();
        boolean passed = score >= requiredScore;
        return new FinalScoreDTO(score, success, failed, passed);
    }
}

package model.order;

public class ScoreTracker {

    private int score = 0;

    public void reward(int points){
        score += points;
    }

    public void penalty(int points){
        score -= points;
        if(score < 0) score = 0;
    }

    public void add(int value){
        score += value;
    }

    public void subtract(int value){
        score -= value;
        if(score < 0) score = 0;
    }

    public int getScore(){
        return score;
    }

    public void reset(){
        score = 0;
    }
}

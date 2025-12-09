package Order;

public class Order {
    private int timeLeft;
    private Recipe recipe;

    public Order(Recipe recipe, int timeLeft){
        this.recipe = recipe;
        this.timeLeft = timeLeft;
    }
    public Recipe getRecipe(){
        return recipe;
    }
    public int getTimeLeft(){
        return timeLeft;
    }

    public void decreaseTime(){
        if(timeLeft > 0){
            timeLeft--;
        }
    }
    public boolean isExpired(){
        return timeLeft <= 0;
    }

    @Override
    public String toString(){
        return "Order: " + recipe.getName() + " (" + timeLeft + "s left)";
    }
}

package BaseItem;

public class Ingredient extends Item {
    private IngredientState state;
    private double cookingPercentage;
    private boolean isChoppable;
    private boolean isCookable;

    public Ingredient(String name, boolean isChoppable, boolean isCookeable) {
        super(name);
        //TODO Auto-generated constructor stub
        this.state = IngredientState.RAW; //semua bahan pasti mentah
        this.cookingPercentage = 0.0;
        this.isChoppable = isChoppable;
        this.isCookable = isCookeable;
    }
    public IngredientState getState(){
        return state; 
    }
    public void chop(){
        //cek apakah bahan ini bisa dipotong atau gak
        //cek apakah status bahan masih mentah atau gak, karena gabis amotong yang udah dimasak
        if (isChoppable && state == IngredientState.RAW){
            this.state = IngredientState.CHOPPED;
            System.out.println("Successed: " + getName() + "berhasil dipotong.");

        }
        else {
            System.out.println("Failed: " + getName() + "tidak bisa dipotong/sudah diproses.");
        }
    }

    public void cook(double duration){
        if(!isCookable) return;
        //pertamakali kena panas, ubah jadi cooking
        if(state == IngredientState.RAW){
            state = IngredientState.COOKING;
        }
        //jika sedang dimasak atau sudah matang tetapi diatas kompor
        if (state == IngredientState.COOKING || state == IngredientState.COOKED){
            cookingPercentage += (duration * 15); //menambah tingkat kematangan
            if(cookingPercentage >= 100 && cookingPercentage < 150){
                //MATANG di 100% dan gosong di 150%
                if(state != IngredientState.COOKED){
                    state = IngredientState.COOKED;
                    System.out.println(getName() + " sudah MATANG!");
                }
            }
            else if(cookingPercentage >= 150){
                if(state != IngredientState.BURNED){
                    state = IngredientState.BURNED;
                    System.out.println("Waduh! " + getName() + "GOSONG.");
                }
            }
        }
    }

    @Override
    public boolean isValid(){
        return state != IngredientState.BURNED;
    }
}

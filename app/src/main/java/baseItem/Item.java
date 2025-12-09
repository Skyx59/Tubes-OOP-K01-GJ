package BaseItem;

public abstract class Item {
    private String name;

    public Item(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public abstract boolean isValid();

    @Override
    public String toString(){
        return this.name;
    }
}

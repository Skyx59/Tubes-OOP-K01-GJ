package station;

import java.util.Stack;

import entity.Chef;
import entity.item.kitchen.Plate;

public class PlateStorage extends Station {

    private final Stack<Plate> clean = new Stack<>();

    public PlateStorage(int count){
        for(int i=0;i<count;i++){
            clean.push(new Plate());
        }
    }

    @Override
    public void interact(Chef chef){
        if(chef.getInventory()==null && !clean.isEmpty()){
            chef.setInventory(clean.pop());
        }
    }

    public Plate takePlate(){
        if(clean.isEmpty()) return null;
        return clean.pop();
    }

    public void returnDirtyPlate(Plate p){
        p.markDirty();
        clean.push(p);
    }

}


package entity;

import entity.item.Item;

public class Chef {

    private Item inventory;
    private boolean busy = false;

    private int gridCol;
    private int gridRow;

    public Item getInventory() {
        return inventory;
    }

    public void setInventory(Item item) {
        this.inventory = item;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean v) {
        this.busy = v;
    }

    public int getGridCol() {
        return gridCol;
    }

    public int getGridRow() {
        return gridRow;
    }

    public void setGridPosition(int col, int row) {
        this.gridCol = col;
        this.gridRow = row;
    }
}

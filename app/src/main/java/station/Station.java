package station;

import entity.Chef;

public abstract class Station {

    protected int col;
    protected int row;

    public void setGridPosition(int c, int r){
        this.col = c;
        this.row = r;
    }

    public int getCol(){ return col; }
    public int getRow(){ return row; }

    // Chef harus berada di sel adjacent
    public boolean isChefAdjacent(Chef chef){
        int dx = Math.abs(chef.getGridCol() - col);
        int dy = Math.abs(chef.getGridRow() - row);
        return dx + dy == 1;
    }

    public abstract void interact(Chef chef);
}

package util;

import java.util.Objects;

public class TilePos {
    public final int col;
    public final int row;

    public TilePos (int col, int row){
        this.col = col;
        this.row = row;
    }

    public TilePos newPos(int dcol, int drow){
        return new TilePos(this.col + dcol, this.row + drow);
    }

    public TilePos move (Direction d){
        return new TilePos(this.col + d.dx, this.row + d.dy);
    }

    public Vector toPixel(double tileSize){
        double x = this.col * tileSize;
        double y = this.row * tileSize;
        return new Vector(x,y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TilePos)) return false;
        TilePos other = (TilePos) o;
        return this.col == other.col && this.row == other.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return "TilePos[" + col + "," + row + "]";
    }

}

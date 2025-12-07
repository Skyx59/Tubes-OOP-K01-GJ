package util;

public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    public final int dy;
    public final int dx;

    Direction(int dy, int dx){
        this.dy = dy;
        this.dx = dx;
    }
}

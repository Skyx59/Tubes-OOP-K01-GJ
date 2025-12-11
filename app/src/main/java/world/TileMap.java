package world;

public class TileMap {

    private final TileType[][] map;

    public TileMap(int width, int height){
        map = new TileType[width][height];
    }

    public TileType get(int x, int y){
        return map[x][y];
    }

    public void set(int x, int y, TileType t){
        map[x][y] = t;
    }

    public int getWidth(){ return map.length; }
    public int getHeight(){ return map[0].length; }
}


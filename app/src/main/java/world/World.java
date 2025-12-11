package world;

public class World {

    private TileMap map;

    public World(TileMap map){
        this.map = map;
    }

    public boolean isWalkable(int gx, int gy){
        TileType t = map.get(gx,gy);
        return switch(t){
            case WALL, CUTTING, COOKING, ASSEMBLY, WASHING, SERVING,
                 INGREDIENT, PLATE, TRASH -> false;
            default -> true;
        };
    }

    public TileType getTile(int x, int y){
        return map.get(x,y);
    }
}

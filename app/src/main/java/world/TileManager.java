package world;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import entity.Player;
import view.GamePanel;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager(GamePanel gp){
        this.gp = gp;

        tile = new Tile[15];
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];

        loadTiles();
        loadMap("/map/map01.txt");
    }

    private void loadTiles(){

        try {
            tile[0] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/floor01.png")), false);
            tile[1] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/wall.png")), true);
            tile[2] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/cutting.png")), true);
            tile[3] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/oven.png")), true);
            tile[4] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/assembly.png")), true);
            tile[5] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/serving.png")), true);
            tile[6] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/wash.png")), true);
            tile[7] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/chest.png")), true);
            tile[8] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/plate.png")), true);
            tile[9] = new Tile(ImageIO.read(getClass().getResourceAsStream("/map/trash.png")), true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMap(String path){
        try {
            InputStream is = getClass().getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int row = 0;
            while (row < gp.maxScreenRow){
                String line = br.readLine();
                String[] nums = line.split(" ");

                for (int col = 0; col < gp.maxScreenCol; col++){
                    mapTileNum[col][row] = Integer.parseInt(nums[col]);
                }
                row++;
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){
        int x = 0, y = 0;

        for (int row = 0; row < gp.maxScreenRow; row++){
            for (int col = 0; col < gp.maxScreenCol; col++){
                int tileId = mapTileNum[col][row];
                g2.drawImage(tile[tileId].image, x, y, gp.tileSize, gp.tileSize, null);
                x += gp.tileSize;
            }
            x = 0;
            y += gp.tileSize;
        }
    }

    // Now using StationGrid stored in GamePanel
    public station.Station getStationInFrontOf(Player p){
        int px = p.x / gp.tileSize;
        int py = p.y / gp.tileSize;

        int tx = px;
        int ty = py;

        switch(p.direction){
            case UP -> ty--;
            case DOWN -> ty++;
            case LEFT -> tx--;
            case RIGHT -> tx++;
        }

        return gp.stationGrid.getStationAt(tx, ty);
    }
}

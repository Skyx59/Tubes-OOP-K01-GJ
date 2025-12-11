package world;

import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage image;
    public boolean collision;

    public Tile() {
        
    }

    public Tile(BufferedImage img, boolean col) {
        this.image = img;
        this.collision = col;
    }
}

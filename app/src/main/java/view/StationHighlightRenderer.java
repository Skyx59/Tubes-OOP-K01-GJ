package view;

import java.awt.Color;
import java.awt.Graphics2D;

import entity.Player;
import station.CookingStation;
import station.PlateStorage;
import station.Station;
import station.WashingStation;
import world.TileManager;

public class StationHighlightRenderer {

    private final TileManager tileManager;

    public StationHighlightRenderer(TileManager tm){
        this.tileManager = tm;
    }

    public void drawHighlight(Graphics2D g2, Player p, int tileSize){

        Station st = tileManager.getStationInFrontOf(p);
        if(st == null) return;

        int tx = p.x / tileSize;
        int ty = p.y / tileSize;

        // Move one tile ahead based on direction
        switch(p.direction){
            case UP ->    ty--;
            case DOWN ->  ty++;
            case LEFT ->  tx--;
            case RIGHT -> tx++;
        }

        // Default highlight color
        Color color = new Color(0, 120, 255, 120); // Blue transparent

        // Station state–based colors:
        if(st instanceof WashingStation ws){
            color = new Color(255, 255, 0, 160); // Yellow
        }
        else if(st instanceof CookingStation cs){
            if(cs.hasUtensil()){
                color = new Color(255, 165, 0, 160); // Orange
            }
        }
        else if(st instanceof PlateStorage ps){
            if(ps.takePlate() == null){
                color = new Color(255, 0, 0, 160); // Red: no plates
            }
        }

        // Draw overlay highlight
        g2.setColor(color);
        g2.fillRect(tx * tileSize, ty * tileSize, tileSize, tileSize);

        // Border for clarity
        g2.setColor(Color.WHITE);
        g2.drawRect(tx * tileSize, ty * tileSize, tileSize, tileSize);
    }
}


package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import entity.item.ingredient.Ingredient;
import entity.item.ingredient.IngredientState;
import entity.item.ingredient.Preparable;
import entity.item.kitchen.FryingPan;
import station.CookingStation;

public class CookingRenderer {

    public void drawCookingStation(Graphics2D g2, CookingStation st, int tileX, int tileY, int tileSize){
        if(!st.hasUtensil()) return;

        if(!(st.getUtensil() instanceof FryingPan pan)) return;

        // Ambil ingredient tunggal
        if(pan.getContentsCount() == 0) return;

        Preparable p = pan.getContents().iterator().next();
        if(!(p instanceof Ingredient ing)) return;

        IngredientState state = ing.getState();

        // Warna station
        switch(state){
            case COOKING -> g2.setColor(new Color(255, 180, 0, 180)); // Orange
            case COOKED  -> g2.setColor(new Color(0, 255, 0, 150));  // Green
            case BURNED  -> g2.setColor(new Color(255, 0, 0, 150));  // Red
            default      -> g2.setColor(new Color(255, 255, 255, 80));
        }

        // Glow overlay
        g2.fillRect(tileX, tileY, tileSize, tileSize);

        // Progress bar
        drawProgressBar(g2, ing, tileX, tileY + tileSize + 2, tileSize, 8);
    }

    private void drawProgressBar(Graphics2D g2, Ingredient ing, int x, int y, int width, int height){

        double percent = ing.getCookingPercentage(); // Anda punya cookingPercentage internal

        // Background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);

        // Progress fill
        g2.setColor(Color.ORANGE);
        int filled = (int)(width * Math.min(percent, 100) / 100.0);
        g2.fillRect(x, y, filled, height);

        // Border
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);

        // Text
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString((int)percent + "%", x + 2, y + height - 1);
    }
}

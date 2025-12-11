package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import entity.Chef;
import entity.item.Item;
import entity.item.ingredient.Ingredient;
import entity.item.ingredient.IngredientState;
import entity.item.ingredient.Preparable;
import entity.item.kitchen.Plate;

public class HudRenderer {

    public void drawChefHud(Graphics2D g2, Chef chef, int x, int y){
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);

        Item inv = chef.getInventory();

        // Title
        g2.drawString("Inventory:", x, y);
        int offset = 20;

        if(inv == null){
            g2.drawString("Empty", x, y + offset);
            return;
        }

        // Draw item name
        g2.setColor(Color.CYAN);
        g2.drawString(inv.toString(), x, y + offset);
        offset += 20;

        // If holding ingredient
        if(inv instanceof Ingredient ing){
            drawIngredientInfo(g2, ing, x, y + offset);
            return;
        }

        // If holding plate
        if(inv instanceof Plate plate){
            drawPlateInfo(g2, plate, x, y + offset);
        }
    }

    private void drawIngredientInfo(Graphics2D g2, Ingredient ing, int x, int y){

        IngredientState st = ing.getState();

        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        // Color by state
        Color c = switch(st){
            case RAW     -> Color.GRAY;
            case CHOPPED -> Color.YELLOW;
            case COOKING -> Color.ORANGE;
            case COOKED  -> Color.GREEN;
            case BURNED  -> Color.RED;
        };

        g2.setColor(c);
        g2.drawString("State: " + st, x, y);
    }

    private void drawPlateInfo(Graphics2D g2, Plate plate, int x, int y){

        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        if(plate.isDirty()){
            g2.setColor(Color.RED);
            g2.drawString("DIRTY PLATE", x, y);
            return;
        }

        if(plate.getContents().isEmpty()){
            g2.setColor(Color.GRAY);
            g2.drawString("Plate empty", x, y);
            return;
        }

        g2.setColor(Color.WHITE);
        g2.drawString("Plate Contents:", x, y);

        int offset = 20;

        for(Preparable p : plate.getContents()){
            String name = p.toString();
            IngredientState st = (p instanceof Ingredient i ? i.getState() : IngredientState.RAW);

            Color c = switch(st){
                case RAW     -> Color.GRAY;
                case CHOPPED -> Color.YELLOW;
                case COOKING -> Color.ORANGE;
                case COOKED  -> Color.GREEN;
                case BURNED  -> Color.RED;
            };

            g2.setColor(c);
            g2.drawString("- " + name + " (" + st + ")", x, y + offset);
            offset += 16;
        }
    }
}

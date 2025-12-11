package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import controller.InputController;
import controller.KeyHandler;
import util.Direction;
import view.GamePanel;

public class Player extends Entity {

    public final Chef chef = new Chef();     // Chef attached to this Player
    private final GamePanel gp;
    private final KeyHandler keyH;
    private final InputController inputController;

    private final String color;

    public Player(GamePanel gp, KeyHandler kh, String color, InputController inputC){
        this.gp = gp;
        this.keyH = kh;
        this.color = color;
        this.inputController = inputC;

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
        loadImages();
    }

    private void setDefaultValues(){
        x = 290;
        y = 95;
        speed = 1;
        direction = Direction.DOWN;
    }

    private void loadImages() {
        try {
            up1    = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_up_1.png"));
            up2    = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_up_2.png"));
            down1  = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_down_1.png"));
            down2  = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_down_2.png"));
            left1  = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_left_1.png"));
            left2  = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/"+ color + "_right_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
    public void update(){
        if (gp.gameState != gp.playState) return;

        // Handle interact button --------------------------------------
        if (keyH.interactPressed) {
            interact();
            keyH.interactPressed = false;
        }

        // Movement identical to your original --------------------------
        if (!isMoving) {

            if (keyH.upPressed) {
                direction = Direction.UP;
                isMoving = true;
                goalX = x;
                goalY = y - gp.tileSize;
            }
            else if (keyH.downPressed) {
                direction = Direction.DOWN;
                isMoving = true;
                goalX = x;
                goalY = y + gp.tileSize;
            }
            else if (keyH.leftPressed) {
                direction = Direction.LEFT;
                isMoving = true;
                goalX = x - gp.tileSize;
                goalY = y;
            }
            else if (keyH.rightPressed) {
                direction = Direction.RIGHT;
                isMoving = true;
                goalX = x + gp.tileSize;
                goalY = y;
            }

            spriteCounter++;
            if(spriteCounter > 200){
                spriteNum = (spriteNum == 1 ? 2 : 1);
                spriteCounter = 0;
            }
        }

        if (isMoving) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkPlayer(this, gp.players, gp.activePlayerIndex);

            if (!collisionOn) {
                switch (direction) {
                    case UP:
                        y -= speed;
                        if (y <= goalY) { y = goalY; isMoving = false; }
                        break;
                    case DOWN:
                        y += speed;
                        if (y >= goalY) { y = goalY; isMoving = false; }
                        break;
                    case LEFT:
                        x -= speed;
                        if (x <= goalX) { x = goalX; isMoving = false; }
                        break;
                    case RIGHT:
                        x += speed;
                        if (x >= goalX) { x = goalX; isMoving = false; }
                        break;
                }
            } else {
                isMoving = false;
            }
        }
        chef.setGridPosition(x / gp.tileSize, y / gp.tileSize);
    }

    // Player triggers InputController
    private void interact() {
        inputController.onInteract(this, chef);
    }

 
    public void draw(Graphics2D g2){
        BufferedImage img = null;

        switch(direction){
            case UP:    img = (spriteNum == 1 ? up1 : up2); break;
            case DOWN:  img = (spriteNum == 1 ? down1 : down2); break;
            case LEFT:  img = (spriteNum == 1 ? left1 : left2); break;
            case RIGHT: img = (spriteNum == 1 ? right1 : right2); break;
        }

        g2.drawImage(img, x, y, gp.tileSize, gp.tileSize, null);
    }
}

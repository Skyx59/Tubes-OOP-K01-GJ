package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, leftPressed, rightPressed, downPressed;
    public boolean switchPressed;
    public boolean interactPressed;
    public boolean enterPressed;

    @Override
    public void keyTyped(KeyEvent e){}

    @Override
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W) upPressed = true;
        if(code == KeyEvent.VK_A) leftPressed = true;
        if(code == KeyEvent.VK_S) downPressed = true;
        if(code == KeyEvent.VK_D) rightPressed = true;

        if(code == KeyEvent.VK_SPACE) switchPressed = true;

        if(code == KeyEvent.VK_E) interactPressed = true;   // interact button
        if(code == KeyEvent.VK_ENTER) enterPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W) upPressed = false;
        if(code == KeyEvent.VK_A) leftPressed = false;
        if(code == KeyEvent.VK_S) downPressed = false;
        if(code == KeyEvent.VK_D) rightPressed = false;

        if(code == KeyEvent.VK_SPACE) switchPressed = false;

        if(code == KeyEvent.VK_E) interactPressed = false;
        if(code == KeyEvent.VK_ENTER) enterPressed = false;
    }
}

package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import controller.InputController;
import controller.KeyHandler;
import controller.OrderController;
import controller.ServingController;
import entity.Player;
import station.PlateStorage;
import world.CollisionChecker;
import world.StationGrid;
import world.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // ==========================================================
    // SCREEN CONFIG
    // ==========================================================
    final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;

    public final int maxScreenCol = 14;
    public final int maxScreenRow = 10;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // ==========================================================
    // GAME STATE
    // ==========================================================
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int gameOverState = 2;
    public final int instructionState = 3;

    public int commandNum = 0; // menu selector

    // ==========================================================
    // TIMER
    // ==========================================================
    private final int GAME_DURATION_SECONDS = 60; // 60 detik
    private long startTime;
    private long remainingTimeMillis;

    // ==========================================================
    // WORLD & COLLISION
    // ==========================================================
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public final StationGrid stationGrid = new StationGrid(this);

    // ==========================================================
    // INPUT
    // ==========================================================
    public KeyHandler keyH = new KeyHandler();

    // ==========================================================
    // THREAD
    // ==========================================================
    Thread gameThread;

    // ==========================================================
    // ENTITIES
    // ==========================================================
    public Player[] players = new Player[2];
    public int activePlayerIndex = 0;

    // ==========================================================
    // CONTROLLERS (LOGIC)
    // ==========================================================
    public InputController inputController;
    public OrderController orderController;
    public ServingController servingController;
    public PlateStorage plateStorage;


    // ==========================================================
    // ==========================================================
    // CONSTRUCTOR
    // ==========================================================
    public GamePanel() {

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);

        // CONTROLLERS
        inputController = new InputController(tileM);
        orderController = new OrderController();
        plateStorage = new PlateStorage(4);
        servingController = new ServingController(orderController);
        // Catatan: ServingCounter.bind(servingController) belum di-wire di sini.
        // Saat ini serving akan menyusul ketika Station/TileManager dihubungkan.
        // PLAYERS
        players[0] = new Player(this, keyH, "red", inputController);
        players[1] = new Player(this, keyH, "blue", inputController);
        players[1].x = 386;
        players[1].y = 239;

        // INITIAL GAME STATE
        gameState = titleState;
        startTime = System.currentTimeMillis();
        remainingTimeMillis = GAME_DURATION_SECONDS * 1000L;
    }

    // ==========================================================
    // START THREAD
    // ==========================================================
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // ==========================================================
    // GAME LOOP
    // ==========================================================
    @Override
    public void run() {

        int FPS = 60;
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {

            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // ==========================================================
    // UPDATE
    // ==========================================================
    public void update() {

        if (gameState == titleState) {
            updateTitleState();
            return;
        }

        if (gameState == instructionState) {
            updateInstructionState();
            return;
        }

        if (gameState == playState) {

            // --- TIMER ---
            long elapsed = System.currentTimeMillis() - startTime;
            remainingTimeMillis = (GAME_DURATION_SECONDS * 1000L) - elapsed;

            if (remainingTimeMillis <= 0) {
                remainingTimeMillis = 0;
                gameState = gameOverState;
                return;
            }

            // --- ORDER LOOP ---
            // Per frame ~16 ms (60 FPS); untuk kesederhanaan gunakan nilai tetap
            orderController.tick(16);

            // --- SWITCH PLAYER ---
            if (keyH.switchPressed) {
                activePlayerIndex = (activePlayerIndex + 1) % players.length;
                keyH.switchPressed = false;
            }

            // --- PLAYER UPDATE (MOVEMENT + INTERACT via InputController) ---
            players[activePlayerIndex].update();
        }

        if (gameState == gameOverState) {
            // Tekan ENTER untuk kembali ke title
            if (keyH.enterPressed) {
                keyH.enterPressed = false;
                resetGame();
                gameState = titleState;
            }
        }
    }

    // ==========================================================
    // RESET GAME
    // ==========================================================
    private void resetGame() {
        // Reset timer
        startTime = System.currentTimeMillis();
        remainingTimeMillis = GAME_DURATION_SECONDS * 1000L;

        // Reset player posisi & inventory (Chef ada di dalam Player)
        players[0].x = 290;
        players[0].y = 95;

        players[1].x = 386;
        players[1].y = 239;

        players[0].chef.setInventory(null);
        players[1].chef.setInventory(null);

        activePlayerIndex = 0;

        // Reset order controller (buat instance baru)
        orderController = new OrderController();
        // servingController masih mengacu ke plateStorage yang sama;
        // saat ServingCounter di-bind, gunakan controller ini.
        servingController = new ServingController(orderController);
    }

    // ==========================================================
    // TITLE STATE LOGIC
    // ==========================================================
    private void updateTitleState() {

        if (keyH.upPressed) {
            commandNum--;
            if (commandNum < 0) commandNum = 2;
            keyH.upPressed = false;
        }

        if (keyH.downPressed) {
            commandNum++;
            if (commandNum > 2) commandNum = 0;
            keyH.downPressed = false;
        }

        if (keyH.enterPressed) {
            keyH.enterPressed = false;

            if (commandNum == 0) {
                // START GAME
                resetGame();
                gameState = playState;
            } else if (commandNum == 1) {
                // HOW TO PLAY
                gameState = instructionState;
            } else if (commandNum == 2) {
                // EXIT
                System.exit(0);
            }
        }
    }

    // ==========================================================
    // INSTRUCTION STATE LOGIC
    // ==========================================================
    private void updateInstructionState() {
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
            gameState = titleState;
        }
    }

    // ==========================================================
    // DRAW
    // ==========================================================
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            drawTitleScreen(g2);
        } else if (gameState == instructionState) {
            drawInstructionScreen(g2);
        } else if (gameState == playState) {
            drawPlayScreen(g2);
        } else if (gameState == gameOverState) {
            drawPlayScreen(g2);  // gambar map+player dulu
            drawGameOverScreen(g2);
        }

        g2.dispose();
    }

    // ==========================================================
    // DRAW: TITLE SCREEN
    // ==========================================================
    private void drawTitleScreen(Graphics2D g2) {

        g2.setColor(new Color(0, 50, 0));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 70));
        g2.setColor(Color.WHITE);
        String text = "NimonsCooked";
        int x = getXforCenteredText(g2, text);
        int y = tileSize * 2;
        g2.drawString(text, x, y);

        // Menu
        g2.setFont(new Font("Arial", Font.PLAIN, 32));
        g2.setColor(Color.YELLOW);

        text = "START GAME";
        x = getXforCenteredText(g2, text);
        y += tileSize * 3;
        g2.drawString(text, x, y);
        if (commandNum == 0) g2.drawString(">", x - tileSize, y);

        text = "HOW TO PLAY";
        x = getXforCenteredText(g2, text);
        y += tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 1) g2.drawString(">", x - tileSize, y);

        text = "EXIT";
        x = getXforCenteredText(g2, text);
        y += tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 2) g2.drawString(">", x - tileSize, y);
    }

    // ==========================================================
    // DRAW: INSTRUCTION SCREEN
    // ==========================================================
    private void drawInstructionScreen(Graphics2D g2) {

        g2.setColor(new Color(0, 0, 50));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.setColor(Color.WHITE);
        String title = "HOW TO PLAY";
        int x = getXforCenteredText(g2, title);
        int y = tileSize * 2;
        g2.drawString(title, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.setColor(Color.LIGHT_GRAY);

        int marginX = tileSize;
        y += tileSize * 2;

        g2.drawString("Kontrol:", marginX, y); y += tileSize;
        g2.drawString("- Gerak: W A S D", marginX, y); y += tileSize;
        g2.drawString("- Switch Player: SPACE", marginX, y); y += tileSize;
        g2.drawString("- Interact: E", marginX, y); y += tileSize;

        g2.setFont(new Font("Arial", Font.ITALIC, 24));
        g2.setColor(Color.YELLOW);
        String msg = "Tekan ENTER untuk kembali ke Main Menu";
        g2.drawString(msg, getXforCenteredText(g2, msg), screenHeight - tileSize * 2);
    }

    // ==========================================================
    // DRAW: PLAY SCREEN
    // ==========================================================
    private void drawPlayScreen(Graphics2D g2) {

        // Tiles
        tileM.draw(g2);

        // Players
        players[0].draw(g2);
        players[1].draw(g2);

        // Timer
        long sec = remainingTimeMillis / 1000;
        long min = sec / 60;
        sec = sec % 60;

        DecimalFormat f = new DecimalFormat("00");
        String timeText = f.format(min) + ":" + f.format(sec);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Time: " + timeText, 10, 20);
        g2.drawString("Active: " + (activePlayerIndex == 0 ? "RED" : "BLUE"), 10, 45);
    }

    // ==========================================================
    // DRAW: GAME OVER OVERLAY
    // ==========================================================
    private void drawGameOverScreen(Graphics2D g2) {

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        String endText = "STAGE OVER!";
        Font endFont = new Font("Arial", Font.BOLD, 60);
        g2.setFont(endFont);
        g2.setColor(Color.RED);

        int x = getXforCenteredText(g2, endText);
        int y = screenHeight / 2;
        g2.drawString(endText, x, y);

        String reasonText = "TIME'S UP!!!";
        Font reasonFont = new Font("Arial", Font.PLAIN, 30);
        g2.setFont(reasonFont);
        g2.setColor(Color.WHITE);
        x = getXforCenteredText(g2, reasonText);
        g2.drawString(reasonText, x, y + 50);

        g2.setFont(new Font("Arial", Font.ITALIC, 24));
        String prompt = "Press ENTER to return to Menu";
        g2.drawString(prompt, getXforCenteredText(g2, prompt), y + 110);
    }

    // ==========================================================
    // UTIL: CENTER TEXT
    // ==========================================================
    private int getXforCenteredText(Graphics2D g2, String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }
}

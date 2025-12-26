package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import controller.KeyHandler;
import model.entity.Player;
import model.map.CollisionChecker;
import model.order.Order;
import model.order.OrderManager;
import model.stage.StageConfig;
import model.stage.StageManager;
import model.stage.StageMeta;
import model.station.AssemblyStation;
import model.station.CookingStation;
import model.station.IngredientStation;
import model.station.PlateStorage;
import model.station.ServingStation;
import model.station.TrashStation;
import model.station.WashingStation;
import view.tile.TileManager;

// Game Screen
public class GamePanel extends JPanel implements Runnable{

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48 tile

    // Spesifikasi Map
    public final int maxScreenCol = 14;
    public final int maxScreenRow = 10;
    public final int screenWidth = tileSize * maxScreenCol; // 672 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 480 pixels

    // FPS
    public final int FPS = 60;

    // Game State
    public int gameState;
    public final int titleState = 0; // Main Menu
    public final int playState = 1;
    public final int gameOverState = 2; // (legacy, tidak dipakai di stage flow; dibiarkan agar tidak merusak kode lain)
    public final int instructionState = 3; // How to Play
    public final int stageSelectState = 4;
    public final int stagePreviewState = 5;
    public final int resultState = 6;

    private static final float BUTTON_SCALE = 0.65f;
    private static final int BUTTON_GAP = 3;
    private static final float RESULT_BTN_SCALE = 0.35f;
    private static final int RESULT_BTN_GAP = 16;
    BufferedImage imgTitleBG;
    BufferedImage btnStart, btnHowToPlay, btnExit;
    BufferedImage btnStage1, btnStage2, btnStage3;
    BufferedImage imgStageOver;
    BufferedImage imgTimesUp;
    BufferedImage btnRetry;
    BufferedImage btnBackToStage;

    private int resultCommandNum = 0;


    // Stage config
    public StageConfig stageConfig;

    // Menu State
    public int commandNum = 0; // 0: Start Game, 1: How to Play, 2: Exit

    // Timer
    private long startTime;
    public long remainingTimeMillis;
    public long lastUpdateTime;

    // Tile
    public TileManager tileM = new TileManager(this);


    // Key Handler
    KeyHandler keyH = new KeyHandler();

    // Thread
    Thread gameThread;

    // Floor
    // floor items stored by tile coordinates (null = empty)
    public String[][] floorItem; // item key names, e.g. "chopped_meat", "tomato", etc.
    public java.awt.image.BufferedImage[][] floorItemImage;

    


    // Order
    public OrderManager orderManager;
    public java.util.ArrayList<ServingStation> servingStations = new java.util.ArrayList<>();
    public int score = 0; // total score
    public int ordersCompleted = 0;
    public int ordersFailed = 0;

    // Plate and Assembly and Trash
    public java.util.ArrayList<PlateStorage> plateStorages = new java.util.ArrayList<>();
    public java.util.ArrayList<AssemblyStation> assemblyStations = new java.util.ArrayList<>();
    public java.util.ArrayList<TrashStation> trashStations = new java.util.ArrayList<>();
    public java.util.ArrayList<WashingStation> washingStations = new java.util.ArrayList<>();

    // Collision
    public CollisionChecker cChecker = new CollisionChecker(this);

    // Player
    public Player[] players = new Player[2];
    public int activePlayerIndex = 0;

    // Stage system
    public StageManager stageManager = new StageManager();
    public StageMeta currentStage = null;
    public int selectedStageIndex = 0;

    // Cooking stations list
    public ArrayList<CookingStation> cookingStations = new ArrayList<>();
    //Ingredient stations list
    public ArrayList<IngredientStation> ingredientStations = new ArrayList<>();

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        players[0] = new Player(this, keyH, "red");
        players[1] = new Player(this, keyH, "blue");

        players[1].x = 386;
        players[1].y = 239;
        
        players[0].syncPxPyToXY();
        players[1].syncPxPyToXY();

        // Stage/title flow versi baru
        gameState = titleState;
        stageConfig = null;

        startTime = System.currentTimeMillis();
        lastUpdateTime = System.currentTimeMillis();
        remainingTimeMillis = 0L; // belum ada stage berjalan

        // Build stations dari map awal (versi lama)
        rebuildStationsFromMap();

        // OrderManager sengaja TIDAK dibuat di constructor.
        // Ia dibuat saat startStage() supaya reward/penalty/maks order mengikuti stageConfig.
        orderManager = null;

        try {
            imgTitleBG = ImageIO.read(getClass().getResource("/stage/title_bg.png"));

            btnStart = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_start.png")));
            btnHowToPlay = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_howtoplay.png")));
            btnExit = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_exit.png")));

            btnStage1 = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_stage1.png")));
            btnStage2 = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_stage2.png")));
            btnStage3 = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_stage3.png")));

            imgStageOver = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/stage_over.png")));
            imgTimesUp   = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/times_up.png")));
            btnRetry     = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_retry.png")));
            btnBackToStage = cropNonTransparent(ImageIO.read(getClass().getResource("/stage/btn_back_to_stage.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void drawText(Graphics2D g2, String text, int x, int y, Font font, Color color) {
        g2.setFont(font);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    @Override
    public void run(){

        // delta method
        double drawInterval = 100000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    // -----------------------
    // UI STATE UPDATES (baru)
    // -----------------------

    public void updateTitleState() {
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
            if (commandNum == 0) {  // START GAME → Stage Select
                gameState = stageSelectState;
            }
            else if (commandNum == 1) { // HOW TO PLAY
                gameState = instructionState;
            }
            else if (commandNum == 2) { // EXIT
                System.exit(0);
            }
            keyH.enterPressed = false;
        }
    }

    public void updateInstructionState() {
        if(keyH.enterPressed){
            gameState = titleState;
            keyH.enterPressed = false;
        }
    }

    public void updateStageSelect() {

        // max index yang unlocked
        int maxUnlockedIndex = 0;
        for (int i = 0; i < stageManager.stages.size(); i++) {
            if (stageManager.stages.get(i).isUnlocked) maxUnlockedIndex = i;
        }

        // pastikan cursor tidak pernah lewat batas unlocked
        if (selectedStageIndex > maxUnlockedIndex) selectedStageIndex = maxUnlockedIndex;
        if (selectedStageIndex < 0) selectedStageIndex = 0;

        if (keyH.upPressed) {
            if (selectedStageIndex > 0) selectedStageIndex--;
            keyH.upPressed = false;
        }

        if (keyH.downPressed) {
            if (selectedStageIndex < maxUnlockedIndex) selectedStageIndex++;
            keyH.downPressed = false;
        }

        if (keyH.enterPressed) {
            StageMeta s = stageManager.stages.get(selectedStageIndex);
            if (s.isUnlocked) {
                currentStage = s;
                gameState = stagePreviewState;
            }
            keyH.enterPressed = false;
        }

        if (keyH.switchPressed) {
            gameState = titleState;
            keyH.switchPressed = false;
        }
    }


    public void updateStagePreview() {
        if (keyH.enterPressed) {
            startStage(currentStage);
            keyH.enterPressed = false;
        }

        if (keyH.switchPressed) {
            gameState = stageSelectState;
            keyH.switchPressed = false;
        }
    }

    public void updateResultState() {

        // navigasi 2 tombol: Retry / Back
        if (keyH.upPressed) {
            resultCommandNum--;
            if (resultCommandNum < 0) resultCommandNum = 1;
            keyH.upPressed = false;
        }

        if (keyH.downPressed) {
            resultCommandNum++;
            if (resultCommandNum > 1) resultCommandNum = 0;
            keyH.downPressed = false;
        }

        // ENTER: execute
        if (keyH.enterPressed) {
            if (resultCommandNum == 0) {
                // RETRY
                startStage(currentStage);
            } else {
                // BACK TO STAGE SELECT
                gameState = stageSelectState;
            }
            keyH.enterPressed = false;
        }

        //SPACE bisa juga sebagai shortcut kembali
        if (keyH.switchPressed) {
            gameState = stageSelectState;
            keyH.switchPressed = false;
        }
    }


    // -----------------------
    // STAGE START (baru)
    // -----------------------

    public void startStage(StageMeta stage) {

        // Apply stage config
        stageConfig = StageConfig.forStage(selectedStageIndex + 1);

        // Reset players (tanpa mengubah movement/collision; hanya reset posisi/default seperti sebelumnya)
        players[0].setDefaultValues();
        players[1].setDefaultValues();
        players[1].x = 386;
        players[1].y = 239;

        activePlayerIndex = 0;

        // Reset score stats
        score = 0;
        ordersCompleted = 0;
        ordersFailed = 0;

        // Load map stage & rebuild stations
        tileM.loadMap(stage.mapPath);
        rebuildStationsFromMap();
        floorItem = new String[maxScreenCol][maxScreenRow];
        floorItemImage = new java.awt.image.BufferedImage[maxScreenCol][maxScreenRow];

        // Reset timer stage
        startTime = System.currentTimeMillis();
        remainingTimeMillis = stageConfig.gameDurationSeconds * 1000L;

        // (fungsionalitas lama) reset cooking stations pan state
        for (CookingStation cs : cookingStations) {
            cs.panPresent = true;
            cs.panOwner = -1;
            cs.panItem = null;
            cs.panTimer = 0;
        }

        // Create order manager AFTER stageConfig exists
        orderManager = new OrderManager(this);
        orderManager.activeOrders.clear();
        orderManager.resetSequence();
        orderManager.trySpawnInitial();

        gameState = playState;
    }

    // -----------------------
    // REBUILD STATIONS (dipakai)
    // -----------------------

    public void rebuildStationsFromMap() {
        cookingStations.clear();
        assemblyStations.clear();
        trashStations.clear();
        plateStorages.clear();
        washingStations.clear();
        servingStations.clear();
        ingredientStations.clear();


        for (int col = 0; col < maxScreenCol; col++) {
            for (int row = 0; row < maxScreenRow; row++) {

                int t = tileM.mapTileNum[col][row];

                if (t == 3) cookingStations.add(new CookingStation(this, col, row));
                else if (t == 4) assemblyStations.add(new AssemblyStation(this, col, row));
                else if (t == 8) plateStorages.add(new PlateStorage(this, col, row, 5));
                else if (t == 9) trashStations.add(new TrashStation(this, col, row));
                else if (t == 5) servingStations.add(new ServingStation(this, col, row));
                else if (t == 6) washingStations.add(new WashingStation(this, col, row));
                if (t == 7) ingredientStations.add(
                    new IngredientStation(this, col, row, "bun", "/ingredient/bun.png")
                );
                else if (t == 10) ingredientStations.add(
                    new IngredientStation(this, col, row, "meat", "/ingredient/meat.png")
                );
                else if (t == 11) ingredientStations.add(
                    new IngredientStation(this, col, row, "cheese", "/ingredient/cheese.png")
                );
                else if (t == 12) ingredientStations.add(
                    new IngredientStation(this, col, row, "lettuce", "/ingredient/lettuce.png")
                );
                else if (t == 13) ingredientStations.add(
                    new IngredientStation(this, col, row, "tomato", "/ingredient/tomato.png")
                );
            }
        }

        // pair neighboring washing stations horizontally (left=washer, right=output)
        for (WashingStation ws : washingStations) {
            for (WashingStation ws2 : washingStations) {
                if (ws != ws2 && ws.row == ws2.row && ws2.col == ws.col + 1) {
                    ws.isWasher = true;
                    ws2.isWasher = false;
                    ws.linkedOutput = ws2;
                }
            }
        }
    }

    // -----------------------
    // MAIN UPDATE (gabungan)
    // -----------------------

    public void update() {

        long now = System.currentTimeMillis();
        long delta = now - lastUpdateTime;
        lastUpdateTime = now;

        // UI states: tidak menyentuh timer/stageConfig/order/station timers
        if (gameState == titleState) {
            updateTitleState();
            return;
        }
        if (gameState == instructionState) {
            updateInstructionState();
            return;
        }
        if (gameState == stageSelectState) {
            updateStageSelect();
            return;
        }
        if (gameState == stagePreviewState) {
            updateStagePreview();
            return;
        }
        if (gameState == resultState) {
            updateResultState();
            return;
        }

        // playState
        if (gameState == playState) {

            // Stage timer (baru)
            long elapsedTime = System.currentTimeMillis() - startTime;
            remainingTimeMillis = (stageConfig.gameDurationSeconds * 1000L) - elapsedTime;

            if (remainingTimeMillis <= 0) {
                remainingTimeMillis = 0;

                boolean pass = score >= currentStage.targetScore;

                if (pass) {
                    currentStage.isCleared = true;
                    int idx = stageManager.stages.indexOf(currentStage);
                    stageManager.unlockNext(idx);
                }

                gameState = resultState;
                return; // jangan update order/station setelah stage selesai
            }

            // Switch player (tidak ubah movement; hanya pilih aktif)
            if(keyH.switchPressed){
                activePlayerIndex  = (activePlayerIndex + 1) % players.length;
                keyH.switchPressed = false;
            }

            // ===== fungsionalitas lama yang harus berjalan setiap frame =====

            // 1) Update order timers (agar countdown jalan)
            if (orderManager != null) {
                orderManager.update(delta);
            }

            // 2) Update dirty plate timers
            for (PlateStorage ps : plateStorages) {
                ps.update(delta);
            }

            // 3) Update washing timers
            for (WashingStation ws : washingStations) {
                ws.update(delta);
            }

            // Update cooking stations
            for (CookingStation cs : cookingStations) {
                cs.update();
            }

            // Update all players
            for (int i = 0; i < players.length; i++) {
                if (players[i] != null) {
                    players[i].update();
                }
            }
        }
        // gameOverState dibiarkan (tidak dipakai dalam stage flow)
    }

    public int getXforCenteredText(Graphics2D g2, String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = screenWidth / 2 - length / 2;
        return x;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        if (gameState == stageSelectState) {
            drawStageSelectScreen(g2);
            g2.dispose();
            return;
        }

        if (gameState == stagePreviewState) {
            drawStagePreviewScreen(g2);
            g2.dispose();
            return;
        }

        if (gameState == resultState) {
            drawResultScreen(g2);
            g2.dispose();
            return;
        }

        if (gameState == titleState) {
            drawTitleScreen(g2);
            g2.dispose();
            return;
        } else if (gameState == playState) {

            // DRAW TILES
            tileM.draw(g2);

            // DRAW floor items (items dropped on floor)
            if (floorItem != null && floorItemImage != null) {
                for (int col = 0; col < maxScreenCol; col++) {
                    for (int row = 0; row < maxScreenRow; row++) {
                        String key = floorItem[col][row];
                        if (key != null) {
                            java.awt.image.BufferedImage img = floorItemImage[col][row];
                            if (img != null) {
                                int x = col * tileSize;
                                int y = row * tileSize;
                                int w = tileSize / 2;
                                int h = tileSize / 2;
                                int ix = x + (tileSize - w) / 2;
                                int iy = y + (tileSize - h) / 2;
                                g2.drawImage(img, ix, iy, w, h, null);
                            } else {
                                g2.setColor(Color.WHITE);
                                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                                g2.drawString(
                                    key,
                                    col * tileSize + 8,
                                    row * tileSize + tileSize / 2
                                );
                            }
                        }
                    }
                }
            }


            // DRAW INGREDIENT STATIONS (WAJIB DI SINI)
            for (IngredientStation is : ingredientStations) {
                is.draw(g2, this);
            }


            // DRAW trash stations (optional overlay)
            for (TrashStation ts : trashStations) {
                ts.draw(g2, this);
            }

            // DRAW cooking stations
            for (CookingStation cs : cookingStations) {
                cs.drawAtStation(g2, this);
            }

            // DRAW plate storages
            for (PlateStorage ps : plateStorages) ps.draw(g2, this);

            // DRAW assembly stations
            for (AssemblyStation a : assemblyStations) a.draw(g2, this);

            // Washing
            for (WashingStation ws : washingStations) ws.draw(g2, this);

            // DRAW PLAYERS + carried pan overlay (legacy behavior tetap)
            for(int i = 0; i < players.length; i++){
                players[i].draw(g2);

                for (CookingStation cs : cookingStations) {
                    cs.drawIfCarriedByPlayer(g2, this, i, players[i]);
                }

                for (AssemblyStation a : assemblyStations) a.draw(g2, this);
                for (PlateStorage ps : plateStorages) ps.draw(g2, this);
            }

            // DRAW UI / TIMER
            long seconds = remainingTimeMillis / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;

            DecimalFormat dFormat = new DecimalFormat("00");
            String timeText = dFormat.format(minutes) + ":" + dFormat.format(seconds);

            drawText(g2, "Time: " + timeText, 10, 20, new Font("Arial", Font.BOLD, 20), Color.WHITE);
            drawText(g2, "Player Aktif: " + (activePlayerIndex  == 0 ? "Merah" : "Biru"),
                    10, 45, new Font("Arial", Font.PLAIN, 16), Color.WHITE);

            String holdingText = "Holding: " + (players[activePlayerIndex].heldItem == null ? "None" : players[activePlayerIndex].heldItem);
            drawText(g2, holdingText, 10, 70, new Font("Arial", Font.PLAIN, 16), Color.WHITE);

            // draw orders UI bottom-left
            if (orderManager != null) {
                drawOrdersUI(g2);
            }

        } else if (gameState == instructionState){
            drawInstructionScreen(g2);
        }

        g2.dispose();
    }

    // -----------------------
    // DRAW SCREENS (baru)
    // -----------------------

    public void drawTitleScreen(Graphics2D g2) {

    // === DRAW FULLSCREEN BG (FIX #1) ===
    int bgW = imgTitleBG.getWidth();
    int bgH = imgTitleBG.getHeight();

    float scale = Math.max(
        (float) screenWidth / bgW,
        (float) screenHeight / bgH
    );

    int drawW = Math.round(bgW * scale);
    int drawH = Math.round(bgH * scale);

    int bgX = (screenWidth - drawW) / 2;
    int bgY = (screenHeight - drawH) / 2;

    g2.drawImage(imgTitleBG, bgX, bgY, drawW, drawH, null);

    // === BUTTON LAYOUT (FIX #2) ===
    BufferedImage[] buttons = { btnStart, btnHowToPlay, btnExit };

    int bw = Math.round(btnStart.getWidth() * BUTTON_SCALE);
    int bh = Math.round(btnStart.getHeight() * BUTTON_SCALE);

    int totalHeight =
            bh * buttons.length +
            BUTTON_GAP * (buttons.length - 1);

    int startY = (screenHeight - totalHeight) / 2;
    int centerX = screenWidth / 2;

    for (int i = 0; i < buttons.length; i++) {
        drawButtonScaled(
            g2,
            buttons[i],
            centerX,
            startY + i * (bh + BUTTON_GAP),
            bw,
            bh,
            commandNum == i
        );
    }
}



    public void drawInstructionScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 50));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.setColor(Color.WHITE);
        String title = "HOW TO PLAY";
        int x = getXforCenteredText(g2, title);
        int y = tileSize * 1;
        g2.drawString(title, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.setColor(Color.LIGHT_GRAY);

        int marginX = tileSize;
        y += tileSize * 1.5;

        drawText(g2, "Kontrol Pemain:", marginX, y, g2.getFont(), g2.getColor());
        y += tileSize * 0.7;
        drawText(g2, "- Movement: W (Atas), A (Kiri), S (Bawah), D (Kanan)", marginX, y, g2.getFont(), g2.getColor());
        y += tileSize * 0.7;
        drawText(g2, "- Switch Player: SPACE", marginX, y, g2.getFont(), g2.getColor());
        y += tileSize * 0.7;
        drawText(g2, "- Interaksi: E (ambil/taruh bahan / cooking station)", marginX, y, g2.getFont(), g2.getColor());
        y += tileSize * 0.7;
        drawText(g2, "- Interaksi: P (memotong bahan / cutting station)", marginX, y, g2.getFont(), g2.getColor());
        y += tileSize * 0.7;
        drawText(g2, "- Interaksi: Q (drop/jatuhkan bahan)", marginX, y, g2.getFont(), g2.getColor());
        y += tileSize * 0.7;
        drawText(g2, "", marginX, y, g2.getFont(), g2.getColor());
        y += tileSize * 0.7;
        drawText(g2, "Selesaikan semua order sesuai dengan waktunya!", marginX, y, g2.getFont(), g2.getColor());

        y += tileSize * 2;
        g2.setColor(Color.YELLOW);
        String back = "Tekan ENTER untuk kembali ke Main Menu...";
        drawText(g2, back, getXforCenteredText(g2, back), y, g2.getFont(), g2.getColor());
    }

    public void drawOrdersUI(Graphics2D g2) {
        int startX = 10;
        int startY = screenHeight - 10;
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        g2.setColor(java.awt.Color.WHITE);
        int gap = 18;
        int drawn = 0;

        for (int i = 0; i < orderManager.activeOrders.size() && drawn < 3; i++) {
            Order o = orderManager.activeOrders.get(i);
            String timePart;
            if (i == 0) {
                timePart = " (" + o.getRemainingSeconds() + "s)";
            } else {
                timePart = " (waiting)";
            }
            String text = "[" + o.position + "] " + o.recipeName + timePart;
            g2.drawString(text, startX, startY - (drawn * gap));
            drawn++;
        }
    }

    public void drawStageSelectScreen(Graphics2D g2) {

        // ===== BG fullscreen (cover) =====
        int bgW = imgTitleBG.getWidth();
        int bgH = imgTitleBG.getHeight();

        float s = Math.max((float) screenWidth / bgW, (float) screenHeight / bgH);
        int drawW = Math.round(bgW * s);
        int drawH = Math.round(bgH * s);
        int bgX = (screenWidth - drawW) / 2;
        int bgY = (screenHeight - drawH) / 2;

        g2.drawImage(imgTitleBG, bgX, bgY, drawW, drawH, null);


        int panelW = 520;
        int panelH = 383;
        int panelX = (screenWidth - panelW) / 2;
        int panelY = 82;

        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 32, 32);

        // ===== title Stage Select (simple & jelas) =====
        g2.setFont(new Font("Arial", Font.BOLD, 44));
        g2.setColor(new Color(220, 60, 60));
        String title = "Stage Select";
        g2.drawString(title, getXforCenteredText(g2, title), 113);

        BufferedImage[] stageBtns = { btnStage1, btnStage2, btnStage3 };

        float stageScale = 0.47f;      // kecilkan dari 1: sesuaikan 0.65 - 0.75 jika perlu
        int gap = 28;                  // jarak antar tombol (jelas)

        int bw = Math.round(stageBtns[0].getWidth() * stageScale);
        int bh = Math.round(stageBtns[0].getHeight() * stageScale);

        int n = stageBtns.length;
        int totalH = n * bh + (n - 1) * gap;

        int startY = (screenHeight - totalH) / 2 + 20; // sedikit turun agar tidak nabrak title
        int centerX = screenWidth / 2;

        // hitung max unlocked (agar tombol locked didim-kan)
        int maxUnlockedIndex = 0;
        for (int i = 0; i < stageManager.stages.size(); i++) {
            if (stageManager.stages.get(i).isUnlocked) maxUnlockedIndex = i;
        }

        for (int i = 0; i < n; i++) {

            boolean selected = (i == selectedStageIndex);
            boolean unlocked = (i <= maxUnlockedIndex);

            float alpha;
            if (!unlocked) alpha = 0.18f;     // locked sangat redup
            else if (selected) alpha = 1.0f;  // selected solid
            else alpha = 0.55f;               // unlocked tapi tidak dipilih

            int w = bw;
            int h = bh;

            // penanda selection: sedikit dibesarkan (tanpa butuh asset baru)
            if (selected) {
                w = Math.round(bw * 1.06f);
                h = Math.round(bh * 1.06f);
            }

            int x = centerX - w / 2;
            int y = startY + i * (bh + gap) + (bh - h) / 2;

            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(stageBtns[i], x, y, w, h, null);
            g2.setComposite(old);

            // tambahan highlight sederhana untuk selected (opsional tapi sangat membantu)
            if (selected) {
                g2.setColor(new Color(255, 255, 255, 120));
                g2.drawRoundRect(x - 6, y - 6, w + 12, h + 12, 28, 28);
            }
        }

        // ===== footer =====
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(new Color(230, 230, 230, 200));
        String footer = "ENTER: Select   |   SPACE: Back";
        g2.drawString(footer, getXforCenteredText(g2, footer), screenHeight - 25);
    }



    public void drawStagePreviewScreen(Graphics2D g2) {
        g2.setColor(new Color(20,20,20));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        g2.drawString("Preview: " + currentStage.name, 140, 60);

        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.drawString("Target Score: " + currentStage.targetScore, 200, 120);

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("ENTER: Start | SPACE: Back", 200, screenHeight - 50);

        tileM.draw(g2);
    }

        public void drawResultScreen(Graphics2D g2) {

        // 1) gambar map terakhir sebagai background (biar nggak hitam)
        tileM.draw(g2);

        // 2) overlay gelap
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // 3) panel gelap di tengah
        int panelW = (int)(screenWidth * 0.82);
        int panelH = (int)(screenHeight * 0.78);
        int panelX = (screenWidth - panelW) / 2;
        int panelY = (screenHeight - panelH) / 2;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 30, 30);

        // 4) gambar "STAGE OVER!" dan "TIME'S UP!" (centered)
        int centerX = screenWidth / 2;

        // stage over
        int stageOverY = panelY + 37;
        drawImageCenteredScaled(g2, imgStageOver, centerX, stageOverY, 0.55f);

        // times up (di bawahnya)
        int timesUpY = stageOverY + 37;
        drawImageCenteredScaled(g2, imgTimesUp, centerX, timesUpY, 0.37f);

        // 5) teks score
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(Color.WHITE);

        int textStartY = timesUpY + 53;
        int lineGap = 31;

        String s1 = "Score: " + score;
        String s2 = "Target: " + (currentStage != null ? currentStage.targetScore : 0);
        String s3 = "Orders Completed: " + ordersCompleted;
        String s4 = "Orders Failed: " + ordersFailed;

        int textX = centerX - 110;
        g2.drawString(s1, textX, textStartY);
        g2.drawString(s2, textX, textStartY + lineGap);
        g2.drawString(s3, textX, textStartY + lineGap * 2);
        g2.drawString(s4, textX, textStartY + lineGap * 3);

        // 6) tombol Retry + Back to Stage (pakai PNG)
        BufferedImage[] buttons = { btnRetry, btnBackToStage };

        int bw = Math.round(buttons[0].getWidth() * RESULT_BTN_SCALE);
        int bh = Math.round(buttons[0].getHeight() * RESULT_BTN_SCALE);

        int totalH = bh * buttons.length + RESULT_BTN_GAP * (buttons.length - 1);
        int startY = panelY + panelH - totalH - 25; // posisikan dekat bawah panel

        for (int i = 0; i < buttons.length; i++) {
            boolean selected = (i == resultCommandNum);

            int x = centerX - bw / 2;
            int y = startY + i * (bh + RESULT_BTN_GAP);

            Composite old = g2.getComposite();
            float alpha = selected ? 1.0f : 0.55f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(buttons[i], x, y, bw, bh, null);
            g2.setComposite(old);

            // highlight selection (tipis saja, tapi jelas)
            if (selected) {
                g2.setColor(new Color(255, 255, 255, 140));
                g2.drawRoundRect(x - 6, y - 6, bw + 12, bh + 12, 24, 24);
            }
        }

        // 7) footer hint
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(new Color(230, 230, 230, 200));
        String footer = "ENTER: Select   |   UP/DOWN: Move   |   SPACE: Back";
        g2.drawString(footer, getXforCenteredText(g2, footer), screenHeight - 18);
    }



    private void drawButtonScaled(
        Graphics2D g2,
        BufferedImage img,
        int centerX,
        int y,
        int w,
        int h,
        boolean selected) {
        int x = centerX - w / 2;

        float alpha = selected ? 1.0f : 0.45f;
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha));

        g2.drawImage(img, x, y, w, h, null);

        g2.setComposite(old);
    }


    private static BufferedImage cropNonTransparent(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();

        int minX = w, minY = h, maxX = -1, maxY = -1;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int a = (src.getRGB(x, y) >>> 24) & 0xFF;
                if (a != 0) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        // kalau semuanya transparan, kembalikan src
        if (maxX < minX || maxY < minY) return src;

        return src.getSubimage(minX, minY, (maxX - minX + 1), (maxY - minY + 1));
    }

    private void drawImageCenteredScaled(Graphics2D g2, BufferedImage img, int centerX, int y, float scale) {
        if (img == null) return;
        int w = Math.round(img.getWidth() * scale);
        int h = Math.round(img.getHeight() * scale);
        int x = centerX - w / 2;
        g2.drawImage(img, x, y - h / 2, w, h, null);
    }
}

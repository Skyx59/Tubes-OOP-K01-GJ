package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

// Import semua modul yang sudah dibuat
import entity.Player;
import map.TileManager;
import map.CollisionChecker;
import controller.KeyHandler;
import Order.OrderGenerator; // Modul Order
import Order.Order;
import BaseItem.Item;       // Modul Item

public class GamePanel extends JPanel implements Runnable{
    
    // SCREEN SETTINGS
    final int originalTileSize = 16; 
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 tile

    // Spesifikasi Map
    public final int maxScreenCol = 14;
    public final int maxScreenRow = 10;
    public final int screenWidth = tileSize * maxScreenCol; 
    public final int screenHeight = tileSize * maxScreenRow; 

    // FPS
    int FPS = 60;

    // SYSTEM
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public OrderGenerator orderGen = new OrderGenerator(); // Integrasi Order
    Thread gameThread;

    // ENTITIES & ITEMS
    public Player[] players = new Player[2];
    public int activePlayerIndex = 0;
    
    // Array 2D untuk menyimpan Item yang ditaruh di atas meja/lantai
    // [col][row] -> Item apa yang ada di situ
    public Item[][] stationItems = new Item[maxScreenCol][maxScreenRow];

    // GAME STATE
    public int score = 0;
    private long lastOrderTime = 0; // Timer untuk generate order baru

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); 
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Setup Players
        players[0] = new Player(this, keyH, "red", true); // Player 1 Aktif
        players[1] = new Player(this, keyH, "blue", false);

        players[1].x = 386;
        players[1].y = 239;
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){
        double drawInterval = 1000000000/FPS; // Koreksi: 1 Miliar nanosecond
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

    public void update(){
        // 1. Logic Ganti Pemain (Debounce sederhana)
        if(keyH.switchPressed == true){
            players[activePlayerIndex].setActive(false); // Nonaktifkan chef lama
            activePlayerIndex = (activePlayerIndex + 1) % players.length;
            players[activePlayerIndex].setActive(true);  // Aktifkan chef baru
            
            keyH.switchPressed = false; 
            
            // Tambahkan delay sedikit agar tidak switch berkal-kali
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }

        // 2. Update Pemain Aktif
        players[activePlayerIndex].update();

        // 3. Update Order System
        orderGen.updateOrders();
        
        // Generate order baru setiap 10 detik (estimasi)
        if (System.currentTimeMillis() - lastOrderTime > 10000) {
            orderGen.generateOrder();
            lastOrderTime = System.currentTimeMillis();
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // 1. Gambar Map
        tileM.draw(g2);

        // 2. Gambar Item yang ada di atas meja (Station Items)
        for (int col = 0; col < maxScreenCol; col++) {
            for (int row = 0; row < maxScreenRow; row++) {
                if (stationItems[col][row] != null) {
                    // Logic gambar item sederhana (String nama dulu jika belum ada gambar)
                    // Nanti ganti dengan g2.drawImage jika Item punya gambar
                    g2.setColor(Color.YELLOW);
                    g2.drawString(stationItems[col][row].getName(), 
                                  col * tileSize + 5, 
                                  row * tileSize + 24);
                }
            }
        }

        // 3. Gambar Players
        for(Player p : players){
            p.draw(g2);
        }
        
        // 4. Gambar UI (Skor & Order)
        drawUI(g2);

        g2.dispose();
    }

    private void drawUI(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Tampilkan Skor
        g2.drawString("Score: " + score, 10, 20);

        // Tampilkan Order Aktif di pojok kanan atas
        int yOrder = 20;
        for (Order o : orderGen.getOrders()) {
            g2.drawString(o.getRecipe().getName() + " (" + o.getTimeLeft() + ")", screenWidth - 200, yOrder);
            yOrder += 25;
        }
    }
}

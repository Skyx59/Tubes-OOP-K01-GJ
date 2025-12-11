package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import model.order.FinalScoreDTO;

public class GameOverScreenRenderer {

    public void draw(Graphics2D g2, FinalScoreDTO dto, int screenWidth, int screenHeight) {

        // Background gelap transparan
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Judul
        g2.setFont(new Font("Arial", Font.BOLD, 64));
        g2.setColor(Color.RED);
        String title = dto.passed() ? "STAGE CLEARED!" : "STAGE FAILED!";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (screenWidth - tw) / 2, screenHeight / 2 - 120);

        // Score
        g2.setFont(new Font("Arial", Font.PLAIN, 32));
        g2.setColor(Color.WHITE);

        String scoreLine = "Score: " + dto.score();
        g2.drawString(scoreLine, centerX(g2, scoreLine, screenWidth), screenHeight / 2 - 40);

        String s1 = "Orders Served: " + dto.success();
        g2.drawString(s1, centerX(g2, s1, screenWidth), screenHeight / 2 + 10);

        String s2 = "Orders Failed: " + dto.failed();
        g2.drawString(s2, centerX(g2, s2, screenWidth), screenHeight / 2 + 60);

        // Prompt kembali
        g2.setFont(new Font("Arial", Font.ITALIC, 26));
        g2.setColor(Color.YELLOW);

        String prompt = "Press ENTER to return to menu";
        g2.drawString(prompt, centerX(g2, prompt, screenWidth), screenHeight / 2 + 140);
    }

    private int centerX(Graphics2D g2, String text, int width){
        return (width - g2.getFontMetrics().stringWidth(text)) / 2;
    }
}

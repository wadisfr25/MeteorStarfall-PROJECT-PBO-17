import java.awt.*;
import javax.swing.*;

public class GameOverPanel extends JPanel {

    private Image backgroundImage;

    public GameOverPanel(Main mainApp, String username, int score, int timePlayed, Image bg) {

        this.backgroundImage = bg;

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // ====== GLASS PANEL ======
        JPanel glass = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                // Glass effect
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);

                // Neon border
                g2.setColor(new Color(0, 220, 255, 140));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
            }
        };

        glass.setOpaque(false);
        glass.setPreferredSize(new Dimension(520, 420));
        add(glass, gbc);

        // ======================= CONTENT ============================
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(15, 15, 15, 15);
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        // === TITLE ===
        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Pixel NES", Font.BOLD, 38));

        c.gridy = 0;
        glass.add(title, c);

        // === SCORE ===
        JLabel scoreLabel = new JLabel("Skor: " + score, SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Pixel NES", Font.PLAIN, 24));
        c.gridy = 1;
        glass.add(scoreLabel, c);

        // === TIME ===
        JLabel timeLabel = new JLabel("Waktu: " + timePlayed + " detik", SwingConstants.CENTER);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Pixel NES", Font.PLAIN, 24));
        c.gridy = 2;
        glass.add(timeLabel, c);

        // Spacer
        c.gridy = 3;
        glass.add(Box.createVerticalStrut(20), c);

        // === BUTTONS ===
        JButton playAgain = modernButton("Main Lagi");
        JButton backMenu = modernButton("Kembali ke Menu");

        c.gridy = 4; 
        glass.add(playAgain, c);

        c.gridy = 5;
        glass.add(backMenu, c);

        playAgain.addActionListener(e -> mainApp.showGame(username));
        backMenu.addActionListener(e -> mainApp.showMainMenu(username));
    }

    // ============================================================
    //                        MODERN BUTTON
    // ============================================================
    private JButton modernButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setForeground(Color.BLACK);
        btn.setBackground(new Color(0, 200, 255));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0, 240, 255));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0, 200, 255));
            }
        });

        return btn;
    }

    // ============================================================
    //                 BACKGROUND + DARK OVERLAY
    // ============================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // DRAW SCREENSHOT
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // DIM / BLUR SIMULATION
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}

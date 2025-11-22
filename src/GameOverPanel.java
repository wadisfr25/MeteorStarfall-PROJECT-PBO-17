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

        // ====== TITLE ======
        JLabel title = new JLabel("GAME OVER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Pixel NES", Font.BOLD, 48));
        add(title, gbc);

        // ====== SCORE ======
        gbc.gridy = 1;
        JLabel scoreLabel = new JLabel("Skor: " + score);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Pixel NES", Font.PLAIN, 28));
        add(scoreLabel, gbc);

        // ====== TIME ======
        gbc.gridy = 2;
        JLabel timeLabel = new JLabel("Waktu: " + timePlayed + " detik");
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Pixel NES", Font.PLAIN, 28));
        add(timeLabel, gbc);

        // ====== SPACER ======
        gbc.gridy = 3;
        add(Box.createVerticalStrut(40), gbc);

        // ===== BUTTONS =====
        JButton playAgain = createBtn("Main Lagi");
        JButton backMenu = createBtn("Kembali ke Menu");

        gbc.gridy = 4;
        add(playAgain, gbc);

        gbc.gridy = 5;
        add(backMenu, gbc);

        playAgain.addActionListener(e -> mainApp.showGame(username));
        backMenu.addActionListener(e -> mainApp.showMainMenu(username));
    }

    // ====== Modern Button ======
    private JButton createBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setForeground(Color.BLACK);
        btn.setBackground(new Color(0, 200, 255));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0, 230, 255));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0, 200, 255));
            }
        });

        return btn;
    }

    // ==== CUSTOM PAINT (Background Screenshot + Dim Overlay) ====
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // Dark transparent overlay for readability
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}

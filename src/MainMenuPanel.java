import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class MainMenuPanel extends JPanel {

    private Main mainApp;
    private String username;
    private Image backgroundImage;

    public MainMenuPanel(Main mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;

        // Load background
        backgroundImage = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\bg2.png").getImage();

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // ============================
        //     WELCOME LABEL
        // ============================
        JLabel welcome = new JLabel("Welcome, " + username + "!");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Pixel NES", Font.BOLD, 26));

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(welcome, gbc);

        // Tombol ukuran standar
        Dimension buttonSize = new Dimension(240, 45);

        // ============================
        //       START GAME
        // ============================
        gbc.gridy = 1;
        JButton startBtn = createButton("Start Game", new Color(0, 180, 255));
        startBtn.setPreferredSize(buttonSize);
        startBtn.addActionListener(e -> mainApp.showGame(username));
        add(startBtn, gbc);

        // ============================
        //      LEADERBOARD
        // ============================
        gbc.gridy = 2;
        JButton leaderboardBtn = createButton("Leaderboard", new Color(0, 180, 255));
        leaderboardBtn.setPreferredSize(buttonSize);
        leaderboardBtn.addActionListener(e -> mainApp.showLeaderboard(username));
        add(leaderboardBtn, gbc);

        // ============================
        //          LOGOUT
        // ============================
        gbc.gridy = 3;
        JButton logoutBtn = createButton("Logout", new Color(255, 80, 80));
        logoutBtn.setPreferredSize(buttonSize);
        logoutBtn.addActionListener(e -> mainApp.showLogin());
        add(logoutBtn, gbc);
        
    }
    

    // ==========================================================
    //                 BUTTON WITH HOVER EFFECT
    // ==========================================================
    private JButton createButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(baseColor);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        Color hoverColor = baseColor.brighter();

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }

    // ==========================================================
    //            DRAW BACKGROUND + DARK OVERLAY
    // ==========================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        // Overlay gelap supaya tulisan lebih jelas
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    String getUsername() {
        return username;
    }

}

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
        setupExitKey();

        backgroundImage = new ImageIcon("assets\\bg2.png").getImage();

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // ====================================================
        //                      TITLE
        // ====================================================
        JLabel title = new JLabel("METEOR STARFALL");
        title.setForeground(new Color(0, 220, 255));
        title.setFont(new Font("Pixel NES", Font.BOLD, 36));
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy = 1;
        add(Box.createVerticalStrut(10), gbc);

        // ====================================================
        //               GLASS CARD CONTAINER
        // ====================================================
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2.setColor(new Color(0, 220, 255, 120));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 420));

        GridBagConstraints gbcCard = new GridBagConstraints();
        gbcCard.insets = new Insets(20, 0, 20, 0);
        gbcCard.fill = GridBagConstraints.HORIZONTAL;

        // ====================================================
        //                 WELCOME LABEL
        // ====================================================
        JLabel welcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        gbcCard.gridy = 0;
        card.add(welcome, gbcCard);

        // ====================================================
        //                BUTTON STYLES
        // ====================================================
        Dimension btnSize = new Dimension(250, 45);

        // Normal Buttons (Blue)
        JButton startBtn = createButton("Start Game", new Color(0, 180, 255), new Color(0, 230, 255));
        startBtn.setPreferredSize(btnSize);

        JButton leaderboardBtn = createButton("Leaderboard", new Color(0, 180, 255), new Color(0, 230, 255));
        leaderboardBtn.setPreferredSize(btnSize);

        // Logout Button (Red)
        JButton logoutBtn = createButton("Logout", new Color(255, 80, 80), new Color(255, 120, 120));
        logoutBtn.setPreferredSize(btnSize);
        logoutBtn.setForeground(Color.WHITE);

        gbcCard.gridy = 1;
        card.add(startBtn, gbcCard);

        gbcCard.gridy = 2;
        card.add(leaderboardBtn, gbcCard);

        gbcCard.gridy = 3;
        card.add(logoutBtn, gbcCard);

        // Tambahkan card ke menu
        gbc.gridy = 2;
        add(card, gbc);

        // ====================================================
        //                BUTTON ACTIONS
        // ====================================================
        startBtn.addActionListener(e -> mainApp.showGame(username));
        leaderboardBtn.addActionListener(e -> mainApp.showLeaderboard(username));
        logoutBtn.addActionListener(e -> mainApp.showLogin());
    }

    // ====================================================
    //            CUSTOM BUTTON with custom colors
    // ====================================================
    private JButton createButton(String text, Color baseColor, Color hoverColor) {

        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(baseColor);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    // ====================================================
    //               BACKGROUND RENDER
    // ====================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    private void setupExitKey() {

    InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap am = getActionMap();

    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitProgram");
    am.put("exitProgram", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (ExitHandler.confirmExit(MainMenuPanel.this)) {
                System.exit(0);
            }
        }
    });
}

}

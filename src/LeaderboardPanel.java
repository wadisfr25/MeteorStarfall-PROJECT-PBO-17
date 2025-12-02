import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class LeaderboardPanel extends JPanel {

    private Main mainApp;
    private String username;
    private Image backgroundImage;

    public LeaderboardPanel(Main mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;

        backgroundImage = new ImageIcon(
            "D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\bg2.png"
        ).getImage();

        setLayout(new BorderLayout());
        setOpaque(false);

        // ======================================================
        //                   TITLE (NEON EFFECT)
        // ======================================================
        JLabel title = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        title.setForeground(new Color(0, 220, 255));
        title.setFont(new Font("Pixel NES", Font.BOLD, 36));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        // ======================================================
        //                   TABLE MODEL
        // ======================================================
        String[] columns = {"Rank", "Username", "Score", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(0, 0, 0, 0));
        table.setOpaque(false);
        table.setRowHeight(40);
        table.setEnabled(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Center alignment
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setOpaque(false);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // ======================================================
        //              HEADER STYLE (CYAN GLOW)
        // ======================================================
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setBackground(new Color(0, 180, 255));
        header.setForeground(Color.BLACK);
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        // ======================================================
        //                  GLASS CARD WRAPPER
        // ======================================================
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.setColor(new Color(0, 220, 255, 80));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        // Custom scrollbar (gelap & kecil)
        JScrollBar sb = scroll.getVerticalScrollBar();
        sb.setPreferredSize(new Dimension(8, 0));
        sb.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0, 180, 255);
                this.trackColor = new Color(0, 0, 0, 100);
            }
        });

        card.add(scroll);

        // ======================================================
        //                  BACK BUTTON
        // ======================================================
        JButton back = new JButton("Back");
        back.setFont(new Font("Arial", Font.BOLD, 18));
        back.setBackground(new Color(0, 180, 255));
        back.setForeground(Color.BLACK);
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        back.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                back.setBackground(new Color(0, 220, 255));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                back.setBackground(new Color(0, 180, 255));
            }
        });

        back.addActionListener(e -> mainApp.showMainMenu(username));

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(back);

        // ======================================================
        //          ADD ITEMS TO MAIN PANEL
        // ======================================================
        add(title, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadLeaderboard(model);
    }

    // ======================================================
    //               LOAD LEADERBOARD DATA
    // ======================================================
    private void loadLeaderboard(DefaultTableModel model) {
        try (Connection conn = KoneksiDatabase.getConnection()) {

            String query = """
                SELECT u.username, s.score, s.date_played
                FROM scores s JOIN users u ON s.user_id = u.user_id
                ORDER BY s.score DESC
                LIMIT 10
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            int rank = 1;

            while (rs.next()) {
                String user = rs.getString("username");
                int score = rs.getInt("score");
                String date = rs.getString("date_played");

                model.addRow(new Object[]{rank, user, score, date});
                rank++;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal memuat leaderboard: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ======================================================
    //                    BACKGROUND
    // ======================================================
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

            if (ExitHandler.confirmExit(LeaderboardPanel.this)) {
                System.exit(0);
            }
        }
    });
}

}
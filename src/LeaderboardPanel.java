
import java.awt.*;
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

        // Load background
        backgroundImage = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\bg2.png").getImage();

        setLayout(new BorderLayout());
        setOpaque(false);

        // ============================
        //            TITLE
        // ============================
        JLabel title = new JLabel("Top 10 Leaderboard", SwingConstants.CENTER);
        title.setFont(new Font("Pixel NES", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // ============================
        //        TABLE MODEL
        // ============================
        String[] columns = {"Username", "Score", "Date Played"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(20, 20, 20));
        table.setRowHeight(32);
        table.setEnabled(false);

        // Center alignment
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setBackground(new Color(0, 180, 255));
        header.setForeground(Color.BLACK);
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 150));

        // ============================
        //        BACK BUTTON
        // ============================
        JButton backBtn = new JButton("← Back to Menu");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(0, 180, 255));
        backBtn.setForeground(Color.BLACK);
        backBtn.setPreferredSize(new Dimension(180, 40));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Hover effect
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                backBtn.setBackground(new Color(0, 220, 255));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                backBtn.setBackground(new Color(0, 180, 255));
            }
        });

        backBtn.addActionListener(e -> mainApp.showMainMenu(username));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);

        // Add components
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load leaderboard data
        loadLeaderboard(model);
    }

    // ============================
    //      LOAD LEADERBOARD
    // ============================
    private void loadLeaderboard(DefaultTableModel model) {
        try (Connection conn = KoneksiDatabase.getConnection()) {

            String query = "SELECT u.username, s.score, s.date_played "
                         + "FROM scores s JOIN users u ON s.user_id = u.user_id "
                         + "ORDER BY s.score DESC LIMIT 10";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String user = rs.getString("username");
                int score = rs.getInt("score");
                String date = rs.getString("date_played");

                model.addRow(new Object[]{user, score, date});
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

    // ============================
    //     BACKGROUND DRAWING
    // ============================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        // Overlay gelap agar tabel dan text lebih jelas
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}

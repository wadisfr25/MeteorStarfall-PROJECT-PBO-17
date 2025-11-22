import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class LoginPanel extends JLayeredPane {

    private Main mainApp;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Image backgroundImage;
    private Image exitIcon;
    
    public LoginPanel(Main mainApp) {
        this.mainApp = mainApp;

        // Load background
        backgroundImage = new ImageIcon(
            "D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\bg2.png"
        ).getImage();

        // Load exit icon (arrow)
        exitIcon = new ImageIcon(
            "D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\ic-backArrow.png"
        ).getImage();

        setLayout(null);
        setOpaque(false);

        // =============================
        //      EXIT BUTTON (ICON)
        // =============================
        Image scaledIcon = exitIcon.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JButton exitBtn = new JButton(new ImageIcon(scaledIcon));

        exitBtn.setBounds(15, 15, 40, 40);
        exitBtn.setBorder(null);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setFocusPainted(false);
        exitBtn.setOpaque(false);
        exitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        exitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitBtn.setIcon(new ImageIcon(brightenImage(scaledIcon)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitBtn.setIcon(new ImageIcon(scaledIcon));
            }
        });

        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                LoginPanel.this,
                "Yakin ingin keluar dari game?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        add(exitBtn, JLayeredPane.PALETTE_LAYER);

        // =============================
        //        LOGIN CONTENT
        // =============================
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBounds(0, 0, 1920, 1080);
        add(content, JLayeredPane.DEFAULT_LAYER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Meteor Starfall", SwingConstants.CENTER);
        title.setFont(new Font("Pixel NES", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        content.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(12);
        usernameField.setBackground(new Color(20,20,20));
        usernameField.setForeground(Color.WHITE);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        content.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        content.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(12);
        passwordField.setBackground(new Color(20,20,20));
        passwordField.setForeground(Color.WHITE);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        content.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton loginBtn = createButton("Login");
        content.add(loginBtn, gbc);

        gbc.gridy = 4;
        JButton registerBtn = createButton("Register");
        content.add(registerBtn, gbc);

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> doRegister());
    }

    // =====================================================
    //   Utility untuk membuat icon lebih terang (hover)
    // =====================================================
    private Image brightenImage(Image img) {
        BufferedImage buff = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = buff.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        for (int y = 0; y < buff.getHeight(); y++) {
            for (int x = 0; x < buff.getWidth(); x++) {
                int rgba = buff.getRGB(x, y);
                Color c = new Color(rgba, true);
                buff.setRGB(x, y, c.brighter().getRGB());
            }
        }

        return buff;
    }

    // =====================================================
    //                BUTTON STYLE
    // =====================================================
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(new Color(0,150,255));
        btn.setForeground(Color.BLACK);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0,200,255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0,150,255));
            }
        });

        return btn;
    }

    // =====================================================
    //                   BACKGROUND
    // =====================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // =====================================================
    //                 LOGIN FUNCTION
    // =====================================================
    private void doLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password tidak boleh kosong!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (KoneksiDatabase.loginUser(username, password)) {
            mainApp.setCurrentUser(username);
            JOptionPane.showMessageDialog(this, "Login berhasil!");
            mainApp.showMainMenu(username);
        } else {
            JOptionPane.showMessageDialog(this,
                "Username atau password salah!",
                "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================
    //                REGISTER FUNCTION
    // =====================================================
    private void doRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Isi username dan password untuk registrasi!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (KoneksiDatabase.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registrasi berhasil!");
        } else {
            JOptionPane.showMessageDialog(this,
                "Username sudah digunakan.",
                "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }
}

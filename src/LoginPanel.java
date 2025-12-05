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

        backgroundImage = new ImageIcon(
            "assets\\bg2.png"
        ).getImage();

        exitIcon = new ImageIcon(
            "assets\\ic-backArrow.png"
        ).getImage();

        setLayout(null);
        setOpaque(false);

        setupExitKey(); // 🔥 Tambahkan ESC Handler di seluruh panel

        // =======================================================
        //                EXIT BUTTON (ICON)
        // =======================================================
        Image scaledIcon = exitIcon.getScaledInstance(40, 40, Image.SCALE_SMOOTH);

        JButton exitBtn = new JButton(new ImageIcon(scaledIcon));
        exitBtn.setBounds(20, 20, 40, 40);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setBorder(null);
        exitBtn.setFocusPainted(false);
        exitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitBtn.setOpaque(false);

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

        // ============================================
        // 🔥 KONFIRMASI EXIT SAAT KLIK TOMBOL EXIT
        // ============================================
        exitBtn.addActionListener(e -> {
            if (ExitHandler.confirmExit(LoginPanel.this)) {
                System.exit(0);
            }
        });

        add(exitBtn, JLayeredPane.PALETTE_LAYER);

        // =======================================================
        //                 GLASS CARD LOGIN BOX
        // =======================================================
        JPanel card = new JPanel(new GridBagLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                // Glass background
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);

                // Neon border
                g2.setColor(new Color(0, 220, 255, 120));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
            }
        };

        card.setOpaque(false);
        card.setBounds(getWidth() / 2 - 250, getHeight() / 2 - 200, 500, 380);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                card.setBounds(getWidth() / 2 - 250, getHeight() / 2 - 200, 500, 380);
            }
        });

        add(card, JLayeredPane.DEFAULT_LAYER);

        // =======================================================
        //                   CONTENT INSIDE CARD
        // =======================================================
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TITLE
        JLabel title = new JLabel("METEOR STARFALL", SwingConstants.CENTER);
        title.setFont(new Font("Pixel NES", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1;

        // USERNAME
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        card.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = createTextField();
        card.add(usernameField, gbc);

        // PASSWORD
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        card.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = createPasswordField();
        card.add(passwordField, gbc);

        // LOGIN BUTTON
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginBtn = createButton("Login");
        card.add(loginBtn, gbc);

        // REGISTER BUTTON
        gbc.gridy = 4;
        JButton registerBtn = createButton("Register");
        card.add(registerBtn, gbc);

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> doRegister());
    }

    // =======================================================
    //                 ESC → EXIT PROGRAM
    // =======================================================
    private void setupExitKey() {

        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitProgram");

        am.put("exitProgram", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ExitHandler.confirmExit(LoginPanel.this)) {
                    System.exit(0);
                }
            }
        });
    }

    // =======================================================
    //                 MODERN TEXT FIELD
    // =======================================================
    private JTextField createTextField() {
        JTextField field = new JTextField(12);
        field.setFont(new Font("Arial", Font.PLAIN, 16));

        field.setBackground(new Color(10, 10, 10));
        field.setOpaque(true);

        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 255), 2));

        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { field.repaint(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { field.repaint(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { field.repaint(); }
        });

        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(12);
        field.setFont(new Font("Arial", Font.PLAIN, 16));

        field.setBackground(new Color(10, 10, 10));
        field.setOpaque(true);

        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 255), 2));

        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { field.repaint(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { field.repaint(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { field.repaint(); }
        });

        return field;
    }

    // =======================================================
    //                 MODERN BUTTON
    // =======================================================
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 180, 255));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0, 230, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0, 180, 255));
            }
        });

        return btn;
    }

    // =======================================================
    //                BRIGHTEN IMAGE
    // =======================================================
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
                Color c = new Color(buff.getRGB(x, y), true);
                buff.setRGB(x, y, c.brighter().getRGB());
            }
        }
        return buff;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // =======================================================
    //                    LOGIN CHECK
    // =======================================================
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

    private void doRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Isi username dan password terlebih dahulu!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (KoneksiDatabase.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registrasi berhasil!");    
        } else {
            JOptionPane.showMessageDialog(this,
                "Username sudah digunakan!",
                "Registrasi Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }
}

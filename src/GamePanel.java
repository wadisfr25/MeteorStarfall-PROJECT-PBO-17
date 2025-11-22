import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class GamePanel extends JPanel {

    private Player player;
    private Image playerImg, meteorImg, starImg, backgroundImage;
    private ArrayList<FallingObject> objects = new ArrayList<>();
    private long startTime;
    private long pausedTime = 0;
    private long pauseStart;

    private int score = 0;
    private boolean paused = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private GameThread gameThread;

    private BufferedImage lastFrame;  // <<-- screenshot frame terakhir


    public GamePanel(Main aThis, String username) {
        setFocusable(true);
        loadImages();

        player = new Player(200, 550, playerImg);

        // Player otomatis diposisikan paling bawah
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int bottomPadding = 60;
                player.setY(getHeight() - player.getHeight() - bottomPadding);
            }
        });

        startTime = System.currentTimeMillis();

        setupKeyBindings();
        setupMouseControl();

        gameThread = new GameThread(this);
        gameThread.start();
    }


    private void loadImages() {
        backgroundImage = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\bg2.png").getImage();
        playerImg = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\rocket_128_clean.png").getImage();
        meteorImg = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\meteor_384.png").getImage();
        starImg = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\star_128_cropped.png").getImage();
    }


    // =====================================================
    //                KEY BINDINGS
    // =====================================================
    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // LEFT
        im.put(KeyStroke.getKeyStroke("A"), "leftPressed");
        im.put(KeyStroke.getKeyStroke("LEFT"), "leftPressed");
        am.put("leftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) leftPressed = true;
            }
        });

        // Release LEFT
        im.put(KeyStroke.getKeyStroke("released A"), "leftReleased");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        am.put("leftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = false;
            }
        });

        // RIGHT
        im.put(KeyStroke.getKeyStroke("D"), "rightPressed");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "rightPressed");
        am.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) rightPressed = true;
            }
        });

        // Release RIGHT
        im.put(KeyStroke.getKeyStroke("released D"), "rightReleased");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        am.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = false;
            }
        });

        // PAUSE
        im.put(KeyStroke.getKeyStroke("SPACE"), "togglePause");
        am.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                paused = !paused;

                if (paused) {
                    pauseStart = System.currentTimeMillis();
                    captureLastFrame();
                } else {
                    pausedTime += System.currentTimeMillis() - pauseStart;
                }
            }
        });
    }


    // =====================================================
    //                MOUSE CONTROL
    // =====================================================
    private void setupMouseControl() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!paused) {
                    player.moveToCursor(e.getX());
                }
            }
        });
    }


    // =====================================================
    //                GAME UPDATE LOOP
    // =====================================================
    public void updateGame() {
        

        if (paused) return;

        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight(getWidth());

        // Meteor spawn
        if (Math.random() < 0.02) {
            int spawnX = (int) (Math.random() * getWidth());
            objects.add(new Meteor(spawnX, -200, 5 + Math.random(), meteorImg));
        }

        // Star spawn
        if (Math.random() < 0.01) {
            int spawnX = (int) (Math.random() * getWidth());
            objects.add(new Star(spawnX, -200, 15 + Math.random() * 3, starImg));
        }

        // Update object
        ArrayList<FallingObject> removeList = new ArrayList<>();
        for (FallingObject obj : objects) {
            obj.update();

            if (pixelPerfectCollision(
                player.getImage(), player.getX(), player.getY(),
                obj.getImage(), obj.getX(), obj.getY())) {

                captureLastFrame();  // <<-- AMBIL SCREENSHOT

                gameThread.stopGame();
                gameOver();
                return;
            }

            if (obj.isOutOfScreen(getHeight())) {
                score++;
                removeList.add(obj);
            }
        }

        objects.removeAll(removeList);
    }


    // =====================================================
    //            FINAL FRAME SCREENSHOT
    // =====================================================
    private void captureLastFrame() {
        lastFrame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lastFrame.createGraphics();
        paint(g2);   // menggambar tampilan terakhir
        g2.dispose();
    }


    // =====================================================
    //                   GAME OVER
    // =====================================================
    private void gameOver() {

        int timePlayed = getTimePlayed();
        Main mainApp = (Main) SwingUtilities.getWindowAncestor(this);
        String username = mainApp.getCurrentUser();

        KoneksiDatabase.saveScore(username, score);

        SwingUtilities.invokeLater(() -> {
            mainApp.showGameOver(username, score, timePlayed, lastFrame);
        });
    }


    // =====================================================
    //                    TIMER
    // =====================================================
    private int getTimePlayed() {
        long now = System.currentTimeMillis();
        return (int) ((now - startTime - pausedTime) / 1000);
    }


    // =====================================================
    //                     RENDER
    // =====================================================
    @Override

public void paintComponent(Graphics g) {
    // ❗ Jangan clear layar saat paused
    if (paused && lastFrame != null) {
        g.drawImage(lastFrame, 0, 0, getWidth(), getHeight(), null);

        // Overlay gelap
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Tulisan PAUSED
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("PAUSED", getWidth()/2 - 120, getHeight()/2);

        return; // ❗ STOP, jangan render player & meteor lagi
    }

    // =========================
    // NORMAL RENDER (TIDAK PAUSE)
    // =========================
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

    // overlay gelap background
    g2.setColor(new Color(0, 0, 0, 120));
    g2.fillRect(0, 0, getWidth(), getHeight());

    // player & objek
    player.draw(g);
    for (FallingObject obj : objects) obj.draw(g);

    // UI teks
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20));
    g.drawString("Score: " + score, 10, 25);
    g.drawString("Time: " + getTimePlayed() + "s", getWidth() - 150, 25);
}



    // =====================================================
    //      PIXEL PERFECT COLLISION
    // =====================================================
    private boolean pixelPerfectCollision(Image img1, int x1, int y1,
                                          Image img2, int x2, int y2) {

        BufferedImage b1 = convertToBuffered(img1);
        BufferedImage b2 = convertToBuffered(img2);

        int overlapX = Math.max(x1, x2);
        int overlapY = Math.max(y1, y2);
        int endX = Math.min(x1 + b1.getWidth(), x2 + b2.getWidth());
        int endY = Math.min(y1 + b1.getHeight(), y2 + b2.getHeight());

        if (overlapX >= endX || overlapY >= endY) return false;

        for (int y = overlapY; y < endY; y++) {
            for (int x = overlapX; x < endX; x++) {
                int px1 = b1.getRGB(x - x1, y - y1);
                int px2 = b2.getRGB(x - x2, y - y2);

                int a1 = (px1 >> 24) & 0xff;
                int a2 = (px2 >> 24) & 0xff;

                if (a1 > 0 && a2 > 0) return true;
            }
        }
        return false;
    }


    private BufferedImage convertToBuffered(Image img) {
        BufferedImage b = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = b.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return b;
    }

    public boolean isPaused() {
        return paused;
    }
}

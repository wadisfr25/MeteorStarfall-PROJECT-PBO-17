import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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

    private BufferedImage lastFrame;  // screenshot frame terakhir
    private long invincibleStart = 0;
    private final long INVINCIBLE_DURATION = 3000; // 3 detik
    private Image heartImg;
    private Image healthImg;
    private double meteorBaseSpeed = 5.0;     
    private double meteorMaxSpeed = 17.0;     
    private Clip bgmClip;

    public GamePanel(Main aThis, String username) {
        setFocusable(true);
        loadImages();

        player = new Player(200, 550, playerImg);

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
        startBGM();

        gameThread = new GameThread(this);
        gameThread.start();
    }


    private void loadImages() {
        backgroundImage = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\bg2.png").getImage();
        playerImg = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\rocket_128_clean.png").getImage();
        meteorImg = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\meteor_384.png").getImage();
        starImg = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\star_128_cropped.png").getImage();
        heartImg = new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\heart_512.png").getImage();
        healthImg= new ImageIcon("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\heart_128.png").getImage();
    }


    // =====================================================
    // KEY BINDINGS
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
        // =====================================
        // ESC → PAUSE + EXIT PROGRAM
        // =====================================
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitProgram");
        am.put("exitProgram", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Pause dulu supaya layar freeze
                if (!paused) {
                    paused = true;
                    pauseStart = System.currentTimeMillis();
                    captureLastFrame();
                }

                // Konfirmasi keluar program
                if (ExitHandler.confirmExit(GamePanel.this)) {

                    // Stop semua thread & suara
                    gameThread.stopGame();
                    stopBGM();

                    // Keluar total dari aplikasi
                    System.exit(0);
                }
            }
        });
    }
    
    


    // =====================================================
    // MOUSE CONTROL
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
    // GAME UPDATE LOOP
    // =====================================================
    public void updateGame() {
        
        if (paused) return;

        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight(getWidth());

        // Meteor spawn
        if (Math.random() < 0.04) {
            int spawnX = (int) (Math.random() * getWidth());
            
            double speed = getCurrentMeteorSpeed() + Math.random();

            objects.add(new Meteor(spawnX, -200, speed, meteorImg));
        }

        // Star spawn
        if (Math.random() < 0.001) {
            int spawnX = (int) (Math.random() * getWidth());
            objects.add(new Star(spawnX, -200, 20 + Math.random() * 3, starImg));
        }

        ArrayList<FallingObject> removeList = new ArrayList<>();

        for (FallingObject obj : objects) {
            obj.update();

            // ============================================
            // COLLISION SYSTEM BARU (MENGGUNAKAN Collidable)
            // ============================================
            if (obj instanceof Collidable) {
                Collidable col = (Collidable) obj;

                if (col.collidesWith(player, this)) {

                    // HEALTH PICKUP
                    if (obj instanceof HealthPickup) {
                        int hp = player.getHealth();
                        if (hp < 3) player.setHealth(hp + 1);

                        removeList.add(obj);
                        continue;
                    }

                    // METEOR
                    if (obj instanceof Meteor && !player.isInvincible()) {

                        player.takeDamage();
                        
                        if (player.getHealth() <= 0) {
                            captureLastFrame();
                            gameThread.stopGame();
                            gameOver();
                            return;
                        }

                        player.setInvincible(true);
                        invincibleStart = System.currentTimeMillis();

                        player.respawn(getWidth(), getHeight());

                        removeList.add(obj);
                        continue;
                    }
                }
            }

            if (obj.isOutOfScreen(getHeight())) {
                score++;
                removeList.add(obj);
            }
        }

        // Cek invincible
        if (player.isInvincible()) {
            long now = System.currentTimeMillis();
            if (now - invincibleStart >= INVINCIBLE_DURATION) {
                player.setInvincible(false);
            }
        }

        // Health pickup spawn jarang
        if (Math.random() < 0.00015) {
            int spawnX = (int) (Math.random() * getWidth());
            objects.add(new HealthPickup(spawnX, -100, 6 + Math.random(), healthImg));
        }

        objects.removeAll(removeList);
    }


    // =====================================================
    // CAPTURE LAST FRAME
    // =====================================================
    private void captureLastFrame() {
        lastFrame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lastFrame.createGraphics();
        paint(g2);
        g2.dispose();
    }


    // =====================================================
    // GAME OVER
    // =====================================================
    private void gameOver() {
        stopBGM();
        int timePlayed = getTimePlayed();
        Main mainApp = (Main) SwingUtilities.getWindowAncestor(this);
        String username = mainApp.getCurrentUser();

        KoneksiDatabase.saveScore(username, score);

        SwingUtilities.invokeLater(() -> {
            mainApp.showGameOver(username, score, timePlayed, lastFrame);
        });
    }


    // =====================================================
    // TIMER
    // =====================================================
    private int getTimePlayed() {
        long now = System.currentTimeMillis();
        return (int) ((now - startTime - pausedTime) / 1000);
    }


    // =====================================================
    // RENDER
    // =====================================================
    @Override
    public void paintComponent(Graphics g) {

        if (paused && lastFrame != null) {
            g.drawImage(lastFrame, 0, 0, getWidth(), getHeight(), null);

            g.setColor(new Color(0, 0, 0, 90));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("PAUSED", getWidth()/2 - 120, getHeight()/2);

            return;
        }

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, getWidth(), getHeight());

        player.draw(g);
        for (FallingObject obj : objects) obj.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);
        g.drawString("Time: " + getTimePlayed() + "s", 10, 50);

        drawHealth(g);
    }


    // =====================================================
    // PIXEL PERFECT COLLISION
    // =====================================================
    public boolean pixelPerfectCollision(Image img1, int x1, int y1,
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


    private void drawHealth(Graphics g) {
        int health = player.getHealth();

        int xStart = getWidth() - (health * 35) - 20;
        int yStart = 10;

        for (int i = 0; i < health; i++) {
            g.drawImage(heartImg, xStart + (i * 35), yStart, 30, 30, null);
        }
    }


    private double getCurrentMeteorSpeed() {
        int time = getTimePlayed();
        double newSpeed = meteorBaseSpeed + (time / 30) * 2.0;
        return Math.min(newSpeed, meteorMaxSpeed);
    }


    private void startBGM() {
        try {
            File file = new File("D:\\FILE MATKUL SMT 5\\PBO\\PROJECT-PBO\\MeteorStarfall\\assets\\bgm2.wav");
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audio);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }
}

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

/**
 * GamePanel adalah panel utama tempat seluruh gameplay terjadi.
 * Panel ini menangani:
 * - Rendering background, player, objek jatuh
 * - Input keyboard & mouse
 * - Sistem pause
 * - Sistem spawn objek
 * - Collision detection (pixel-perfect)
 * - Penghitungan skor dan waktu bermain
 * - Transisi ke GameOverPanel
 * 
 * Panel ini berjalan bersama GameThread yang mengupdate game setiap 16ms (60 FPS).
 */
public class GamePanel extends JPanel {

    // ============================================================
    //                         GAME OBJECTS
    // ============================================================

    private Player player;                  // Objek player yang dikendalikan user
    private Image playerImg, meteorImg, starImg, backgroundImage, shieldImg;

    /** 
     * Menyimpan kumpulan objek jatuh (meteor, star, health, shield)
     * Menggunakan ArrayList karena jumlahnya dinamis.
     */
    private final ArrayList<FallingObject> objects = new ArrayList<>();

    // ============================================================
    //                      TIMER & PAUSE SYSTEM
    // ============================================================

    private final long startTime;    // Waktu awal game dimulai
    private long pausedTime = 0; // Total durasi game dalam keadaan pause
    private long pauseStart;   // Timestamp saat pause dimulai

    private boolean paused = false; // Status pause
    private BufferedImage lastFrame; // Screenshot terakhir saat pause (untuk efek freeze)

    // ============================================================
    //                   INPUT STATE (KEYBOARD)
    // ============================================================
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // ============================================================
    //                           SCORING
    // ============================================================

    private int score = 0; // Skor bertambah setiap meteor keluar layar

    // ============================================================
    //                     THREAD GAME UTAMA
    // ============================================================
    private final GameThread gameThread;

    // ============================================================
    //                           INVINCIBLE
    // ============================================================
    private long invincibleStart = 0;               // waktu mulai invincibility
    private final long INVINCIBLE_DURATION = 3000;  // durasi 3 detik setelah kena hit

    // ============================================================
    //                       HEALTH DISPLAY
    // ============================================================
    private Image heartImg;
    private Image healthImg;

    // ============================================================
    //       METEOR SPEED (BERTAMBAH SEIRING WAKTU)
    // ============================================================
    private final double meteorBaseSpeed = 15.0;          // kecepatan awal
    private final double meteorMaxSpeed = 30.0;           // batas maksimum

    // ============================================================
    //                BACKGROUND MUSIC (BGM PLAYER)
    // ============================================================
    private Clip bgmClip;

    // ============================================================
    //             BATAS KIRI & KANAN PEMAIN DI PANEL
    // ============================================================
    public int getLeftBound()  { return 5; }
    public int getRightBound() { return 5; }

    // ============================================================
    //                         KONSTRUKTOR
    // ============================================================

    /**
     * Konstruktor mempersiapkan seluruh sistem game:
     * - Load asset gambar
     * - Inisialisasi player
     * - Mengatur posisi player mengikuti resize layar
     * - Mengaktifkan key binding
     * - Mengaktifkan kontrol mouse
     * - Memulai background music
     * - Menjalankan GameThread untuk update berkala
     */
    public GamePanel(Main aThis, String username) {
        setFocusable(true);
        loadImages();

        // posisi awal player
        player = new Player(200, 550, playerImg);

        /**
         * Listener yang memastikan player tetap berada di bawah setelah window di-resize.
         * Panel height berubah → player turun mengikuti sisi bawah.
         */
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int bottomPadding = 60;
                player.setY(getHeight() - player.getHeight() - bottomPadding);
            }
        });

        // catat waktu mulai
        startTime = System.currentTimeMillis();

        setupKeyBindings();   // keyboard handling
        setupMouseControl();  // mouse movement
        startBGM();           // background music

        // mulai loop game
        gameThread = new GameThread(this);
        gameThread.start();
    }

    // ============================================================
    //                     LOAD GAMBAR ASSET
    // ============================================================

    /**
     * Meload seluruh asset gambar dari folder.
     * Sangat penting dilakukan di awal agar game tidak lag saat runtime.
     */

    private void loadImages() { 
        backgroundImage = new ImageIcon("assets\\bg2.png").getImage(); 
        playerImg = new ImageIcon("assets\\rocket_128.png").getImage(); 
        meteorImg = new ImageIcon("assets\\meteor_384.png").getImage(); 
        starImg = new ImageIcon("assets\\star_128_cropped.png").getImage(); 
        heartImg = new ImageIcon("assets\\heart_512.png").getImage(); 
        healthImg= new ImageIcon("assets\\heart_128.png").getImage();
        shieldImg = new ImageIcon("assets\\shield.png").getImage(); 
    }

    // ============================================================
    //                        KEY BINDINGS
    // ============================================================

    /**
     * Sistem input keyboard menggunakan Key Bindings:
     * - Lebih stabil daripada KeyListener
     * - Tetap bekerja meski fokus berpindah antar komponen
     * 
     * Key yang digunakan:
     * A/LEFT  → Gerak kiri
     * D/RIGHT → Gerak kanan
     * SPACE   → Pause game
     * ESC     → Pause + konfirmasi keluar program
     */
    private void setupKeyBindings() {

        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // ============================================================
        //                      GERAK KE KIRI
        // ============================================================
        im.put(KeyStroke.getKeyStroke("A"), "leftPressed");
        im.put(KeyStroke.getKeyStroke("LEFT"), "leftPressed");
        am.put("leftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) leftPressed = true;
            }
        });

        // Lepas tombol kiri
        im.put(KeyStroke.getKeyStroke("released A"), "leftReleased");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        am.put("leftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = false;
            }
        });

        // ============================================================
        //                      GERAK KE KANAN
        // ============================================================
        im.put(KeyStroke.getKeyStroke("D"), "rightPressed");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "rightPressed");
        am.put("rightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) rightPressed = true;
            }
        });

        im.put(KeyStroke.getKeyStroke("released D"), "rightReleased");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        am.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = false;
            }
        });

        // ============================================================
        //                      TOGGLE PAUSE
        // ============================================================

        /**
         * Tekan SPACE → pause/unpause game.
         * Ketika pause:
         * - frame terakhir dicapture untuk efek freeze
         * - waktu pause dicatat supaya timer tidak bertambah
         */
        im.put(KeyStroke.getKeyStroke("SPACE"), "togglePause");
        am.put("togglePause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                paused = !paused;

                if (paused) {
                    pauseStart = System.currentTimeMillis();
                    captureLastFrame(); // screenshot untuk freeze-screen
                } else {
                    pausedTime += System.currentTimeMillis() - pauseStart;
                }
            }
        });

        // ============================================================
        //                      EXIT (ESC BUTTON)
        // ============================================================

        /**
         * Tekan ESC:
         * - Pause layar dulu → freeze
         * - Tampilkan confirm exit
         * - Jika YES → stop thread + stop BGM + exit
         */
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitProgram");

        am.put("exitProgram", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // pause dulu supaya tampilan freeze
                if (!paused) {
                    paused = true;
                    pauseStart = System.currentTimeMillis();
                    captureLastFrame();
                }

                if (ExitHandler.confirmExit(GamePanel.this)) {
                    gameThread.stopGame();
                    stopBGM();
                    System.exit(0);
                }
            }
        });
    }

    // ============================================================
    //                      MOUSE CONTROL
    // ============================================================

    /**
     * Player bergerak mengikuti posisi mouse secara horizontal.
     * Mouse movement sangat responsif dan sering digunakan oleh pemain kasual.
     * Sistem ini hanya berjalan jika game tidak dalam keadaan pause.
     */
    private void setupMouseControl() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                if (!paused) {

                    /**
                     * Player digeser tepat ke posisi X mouse,
                     * namun tetap dibatasi oleh leftBound & rightBound.
                     */
                    player.moveToCursor(
                        e.getX(),
                        getWidth(),
                        getLeftBound(),
                        getRightBound()
                    );
                }
            }
        });
    }


    // ============================================================
    //                      GAME UPDATE LOOP
    // ============================================================

    /**
     * updateGame() adalah inti dari loop permainan.
     * Method ini dipanggil 60x per detik oleh GameThread.
     * Tanggung jawab:
     * - Mengupdate posisi player
     * - Menggerakkan semua objek jatuh
     * - Spawn meteor, star, health, shield
     * - Melakukan collision detection pixel-perfect
     * - Mengatur invincibility dan shield timeout
     * - Memproses skor jika objek keluar layar
     */
    public void updateGame() {

        // Game tidak bergerak sama sekali ketika pause
        if (paused) return;

        // ============================================================
        //                    MOVEMENT PLAYER (KEYBOARD)
        // ============================================================
        if (leftPressed) player.moveLeft(getLeftBound());
        if (rightPressed) player.moveRight(getWidth(), getRightBound());

        // ============================================================
        //                   SPAWN METEOR SECARA RANDOM
        // ============================================================

        /**
         * Spawn meteor probabilitas 9% setiap frame.
         * X posisi acak, speed meningkat sesuai durasi game.
         */
        if (Math.random() < 0.09) {

            int w = meteorImg.getWidth(null);

            // batas spawn mengikuti leftBound dan rightBound player
            int minX = getLeftBound() - w / 2;
            int maxX = getWidth() - getRightBound() - w + w / 2;

            int spawnX = minX + (int)(Math.random() * (maxX - minX));

            double speed = getCurrentMeteorSpeed() + Math.random();

            objects.add(new Meteor(spawnX, -200, speed, meteorImg));
        }

        // ============================================================
        //                   SPAWN STAR (TIDAK ADA COLLISION)
        // ============================================================

        /**
         * Star hanya sebagai visual efek, tidak memiliki collision.
         * Spawn sangat jarang.
         */
        if (Math.random() < 0.001) {

            int w = starImg.getWidth(null);
            int minX = getLeftBound() - w / 2;
            int maxX = getWidth() - getRightBound() - w + w / 2;

            int spawnX = minX + (int)(Math.random() * (maxX - minX));

            objects.add(new Star(spawnX, -200, 20 + Math.random() * 3, starImg));
        }

        // Daftar objek yang akan dihapus setelah loop
        ArrayList<FallingObject> removeList = new ArrayList<>();

        // ============================================================
        //                UPDATE POSISI & CEK COLLISION
        // ============================================================

        /**
         * Perulangan utama yang mengupdate seluruh FallingObject:
         * - Update posisi jatuh
         * - Periksa collision jika objek termasuk Collidable
         * - Proses efek collision (damage, pickup)
         * - Hapus objek yang keluar layar
         */
        for (FallingObject obj : objects) {
            obj.update();

            // ========================================================
            //                COLLISION HANDLING (INTERFACE)
            // ========================================================

            /**
             * Untuk efisiensi, hanya objek yang implements Collidable
             * yang menjalankan pengecekan tabrakan.
             */
            if (obj instanceof Collidable) {
                Collidable col = (Collidable) obj;

                // Jika terjadi tabrakan pixel-perfect atau bentuk lain
                if (col.collidesWith(player, this)) {

                    // ====================================================
                    //                    HEALTH PICKUP
                    // ====================================================
                    if (obj instanceof HealthPickup) {
                        int hp = player.getHealth();

                        // Health dibatasi maksimal 3
                        if (hp < 3) player.setHealth(hp + 1);

                        removeList.add(obj);
                        continue;
                    }

                    // ====================================================
                    //                    SHIELD PICKUP
                    // ====================================================
                    if (obj instanceof ShieldPickup) {
                        player.giveShield();
                        removeList.add(obj);
                        continue;
                    }

                    // ====================================================
                    //                    METEOR COLLISION
                    // ====================================================
                    if (obj instanceof Meteor) {

                        // Jika player punya shield → shield pecah, selamat
                        if (player.hasShield()) {
                            player.breakShield();
                            removeList.add(obj);
                            continue;
                        }

                        // Jika tidak shield dan tidak invincible
                        if (!player.isInvincible()) {

                            player.takeDamage();

                            // Jika health habis → game over
                            if (player.getHealth() <= 0) {
                                captureLastFrame();
                                gameThread.stopGame();
                                gameOver();
                                return;
                            }

                            // Jika masih hidup → aktifkan invincibility
                            player.setInvincible(true);
                            invincibleStart = System.currentTimeMillis();

                            // Reposisi player ke tengah
                            player.respawn(getWidth(), getHeight());
                            removeList.add(obj);
                            continue;
                        }
                    }
                }
            }

            // ============================================================
            //        HAPUS OBJEK KETIKA KELUAR DARI LAYAR BAWAH
            // ============================================================
            if (obj.isOutOfScreen(getHeight())) {
                score++;              // skor bertambah untuk setiap meteor lolos
                removeList.add(obj);  // objek dihapus
            }
        }

        // ============================================================
        //                HANDLE INVINCIBILITY TIMEOUT
        // ============================================================

        if (player.isInvincible()) {
            long now = System.currentTimeMillis();

            // invincibility hanya 3 detik setelah kena meteor
            if (now - invincibleStart >= INVINCIBLE_DURATION) {
                player.setInvincible(false);
            }
        }

        // ============================================================
        //               HANDLE SHIELD AUTO-EXPIRE (10 detik)
        // ============================================================

        if (player.hasShield()) {
            long now = System.currentTimeMillis();
            if (now - player.getShieldStartTime() >= 10000) {
                player.breakShield();
            }
        }

        // ============================================================
        //                     SPAWN HEALTH JARANG
        // ============================================================

        if (Math.random() < 0.00015) {

            int w = healthImg.getWidth(null);
            int minX = getLeftBound() - w / 2;
            int maxX = getWidth() - getRightBound() - w + w / 2;

            int spawnX = minX + (int)(Math.random() * (maxX - minX));

            objects.add(new HealthPickup(spawnX, -100, 6 + Math.random(), healthImg));
        }

        // ============================================================
        //                       SPAWN SHIELD LEBIH LANGKA
        // ============================================================

        if (Math.random() < 0.1) {
            int w = shieldImg.getWidth(null);

            int minX = getLeftBound() - w / 2;
            int maxX = getWidth() - getRightBound() - w + w / 2;

            int spawnX = minX + (int)(Math.random() * (maxX - minX));

            objects.add(new ShieldPickup(spawnX, -150, 7, shieldImg));
        }

        // ============================================================
        //             HAPUS SEMUA OBJEK YANG TERDAFTAR REMOVE
        // ============================================================
        objects.removeAll(removeList);
    }

    // ============================================================
    //                      SCREENSHOT LAST FRAME
    // ============================================================

    /**
     * Capture tampilan terakhir panel ke dalam BufferedImage.
     * Digunakan ketika game pause atau game over untuk efek freeze background.
     */
    private void captureLastFrame() {
        lastFrame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lastFrame.createGraphics();
        paint(g2); // render ulang satu frame
        g2.dispose();
    }

    // ============================================================
    //                         GAME OVER
    // ============================================================

    /**
     * Ketika health player habis:
     * - Stop BGM
     * - Hitung total waktu bermain
     * - Ambil Main object (parent JFrame)
     * - Simpan skor ke database
     * - Tampilkan GameOverPanel
     */
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

    // ============================================================
    //                           TIMER
    // ============================================================

    /**
     * Mengembalikan total durasi bermain dalam detik
     * (dikurangi durasi pause).
     */
    private int getTimePlayed() {
        long now = System.currentTimeMillis();
        return (int)((now - startTime - pausedTime) / 1000);
    }

    // ============================================================
    //                   RENDERING (GRAPHICS)
    // ============================================================

    /**
     * paintComponent() dipanggil setiap repaint() (±60 FPS).
     * Fungsi ini merender:
     * - background
     * - overlay gelap
     * - player
     * - semua objek jatuh
     * - skor & waktu
     * - health bar
     * 
     * Jika game dalam kondisi pause:
     * - gunakan screenshot lastFrame
     * - tampilkan tulisan "PAUSED"
     */
    @Override
    public void paintComponent(Graphics g) {

        // ================== MODE PAUSE ==================
        if (paused && lastFrame != null) {
            g.drawImage(lastFrame, 0, 0, getWidth(), getHeight(), null);

            // semi-transparan overlay
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRect(0, 0, getWidth(), getHeight());

            // teks PAUSED
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("PAUSED", getWidth()/2 - 120, getHeight()/2);

            return; // jangan render game lainnya
        }

        // ================ MODE NORMAL ===================
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // gambar background
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        // overlay gelap agar objek lebih jelas
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // render player
        player.draw(g);

        // render semua objek jatuh
        for (FallingObject obj : objects) obj.draw(g);

        // render skor & waktu
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);
        g.drawString("Time: " + getTimePlayed() + "s", 10, 50);

        // render health
        drawHealth(g);
    }

    // ============================================================
    //                 COLLISION PIXEL PERFECT
    // ============================================================

    /**
     * Melakukan tabrakan pixel-perfect antara dua gambar berbasis alpha channel.
     * Logika:
     * 1. Konversi Image → BufferedImage agar bisa dibaca pixelnya.
     * 2. Tentukan area overlap kotak bounding kedua objek.
     * 3. Loop pixel-per-pixel dalam area overlap.
     * 4. Jika alpha kedua pixel > 0 → tabrakan terjadi.
     * 
     * Metode ini sangat mahal secara komputasi,
     * namun sangat akurat untuk sprite yang tidak berbentuk persegi.
     */
    public boolean pixelPerfectCollision(Image img1, int x1, int y1,
                                         Image img2, int x2, int y2) {

        BufferedImage b1 = convertToBuffered(img1);
        BufferedImage b2 = convertToBuffered(img2);

        // hitung overlap bounding box
        int overlapX = Math.max(x1, x2);
        int overlapY = Math.max(y1, y2);
        int endX = Math.min(x1 + b1.getWidth(), x2 + b2.getWidth());
        int endY = Math.min(y1 + b1.getHeight(), y2 + b2.getHeight());

        // jika tidak overlap sama sekali
        if (overlapX >= endX || overlapY >= endY) return false;

        // loop pixel-per-pixel dalam area overlap
        for (int y = overlapY; y < endY; y++) {
            for (int x = overlapX; x < endX; x++) {

                int px1 = b1.getRGB(x - x1, y - y1);
                int px2 = b2.getRGB(x - x2, y - y2);

                int a1 = (px1 >> 24) & 0xff;
                int a2 = (px2 >> 24) & 0xff;

                // jika kedua pixel punya nilai alpha > 0 → ada tabrakan
                if (a1 > 0 && a2 > 0) return true;
            }
        }
        return false;
    }

    /**
     * Mengubah Image biasa (yang tidak bisa dibaca pixelnya)
     * menjadi BufferedImage agar bisa diakses menggunakan getRGB().
     */
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

    // ============================================================
    //                       HEALTH UI DRAWING
    // ============================================================

    private void drawHealth(Graphics g) {
        int health = player.getHealth();

        /**
         * Health digambar di pojok kanan atas.
         * Setiap heart offset 35px agar rapi.
         */
        int xStart = getWidth() - (health * 35) - 20;
        int yStart = 10;

        for (int i = 0; i < health; i++) {
            g.drawImage(heartImg, xStart + (i * 35), yStart, 30, 30, null);
        }
    }

    // ============================================================
    //                  METEOR SPEED SCALING SYSTEM
    // ============================================================

    /**
     * Kecepatan meteor meningkat setiap 30 detik.
     * Rumus:
     * baseSpeed + (timePlayed / 30) * 2
     * 
     * Dengan batas kecepatan maksimum 30.
     */
    private double getCurrentMeteorSpeed() {
        int time = getTimePlayed();
        double newSpeed = meteorBaseSpeed + (time / 30) * 2.0;
        return Math.min(newSpeed, meteorMaxSpeed);
    }

    // ============================================================
    //                    BACKGROUND MUSIC (BGM)
    // ============================================================

    /**
     * Memutar BGM loop menggunakan javax.sound.sampled.Clip
     * BGM berjalan terus-menerus selama game berjalan.
     */
    private void startBGM() {
        try {
            File file = new File("assets\\bgm2.wav");
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audio);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menghentikan BGM ketika game over atau exit.
     */
    private void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    public double getMeteorMaxSpeed() {
        return meteorMaxSpeed;
    }
}


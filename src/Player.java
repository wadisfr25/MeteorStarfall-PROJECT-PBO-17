import java.awt.*;

public class Player {

    private int x, y;
    private int width, height;
    private int speed = 7;
    private Image image;
    private int health = 3; // ❤️ HEALTH AWAL
    private boolean invincible = false; // 🛡️ MODE KEKEBALAN
    private boolean hasShield = false;
    private long shieldStartTime = 0;
    
    public int getHealth() { return health; }
    public boolean isInvincible() { return invincible; }
    public Image getImage() { return image; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void takeDamage() {
        if (!invincible) {
            health--;
        }
    }

    public void setInvincible(boolean value) {
        invincible = value;
    }

    public void respawn(int panelWidth, int panelHeight) {
        // kembali ke posisi tengah bawah
        this.x = panelWidth / 2 - width / 2;
        this.y = panelHeight - height - 60;
    }
    
    public Player(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    public void moveLeft(int leftBound) {
        x -= speed;
        if (x < leftBound) x = leftBound;
    }


    public void moveRight(int panelWidth, int rightBound) {
        x += speed;
        if (x + width > panelWidth - rightBound) {
            x = panelWidth - rightBound - width;
        }
    }


    public void draw(Graphics g) {
        if (invincible) {
            long blink = System.currentTimeMillis() % 300;
            if (blink < 150) return;
        }

        if (hasShield) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0, 200, 255, 120));
            g2.fillOval(x - 10, y - 10, width + 20, height + 20);
        }

        g.drawImage(image, x, y, null);
    }


public Rectangle getBounds() {

    // shrink hitbox untuk body rocket
    int shrinkX = width / 6;   // kiri & kanan
    int shrinkYTop = height / 8;  // atas
    int shrinkYBottom = height / 5; // bawah

    return new Rectangle(
        x + shrinkX,
        y + shrinkYTop,
        width - shrinkX * 2,
        height - shrinkYTop - shrinkYBottom
    );
}

    public void moveToCursor(int mouseX, int panelWidth, int leftBound, int rightBound) {

        // Posisi berdasarkan mouse
        int newX = mouseX - width / 2;

        // Batasi kiri
        if (newX < leftBound) {
            newX = leftBound;
        }

        // Batasi kanan
        if (newX + width > panelWidth - rightBound) {
            newX = panelWidth - rightBound - width;
        }

        this.x = newX;
    }

    public int getHeight() { return height; }

    public void setY(int y) {
        this.y = y;
    }
    public void setHealth(int h) {
        this.health = h;
    }

    public boolean hasShield() { 
    return hasShield; 
    }

    public void giveShield() {
        hasShield = true;
        shieldStartTime = System.currentTimeMillis();
    }

    public void breakShield() {
        hasShield = false;
    }
    public long getShieldStartTime() {
        return shieldStartTime;
    }


}
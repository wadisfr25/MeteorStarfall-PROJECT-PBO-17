import java.awt.*;

public class Player {

    private int x, y;
    private int width, height;
    private int speed = 7;
    private Image image;
    private int health = 3; // ❤️ HEALTH AWAL
    private boolean invincible = false; // 🛡️ MODE KEKEBALAN
    
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

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }

    public void moveRight(int panelWidth) {
        x += speed;
        if (x + width > panelWidth) x = panelWidth - width;
    }

    public void draw(Graphics g) {
        if (invincible) {
            long blink = System.currentTimeMillis() % 300;
            if (blink < 150) return; // efek berkedip saat invincible
        }
        g.drawImage(image, x, y, null);
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public void moveToCursor(int mouseX) {
    this.x = mouseX - width / 2;
    }
    public int getHeight() { return height; }

    public void setY(int y) {
        this.y = y;
    }
    public void setHealth(int h) {
        this.health = h;
    }


}

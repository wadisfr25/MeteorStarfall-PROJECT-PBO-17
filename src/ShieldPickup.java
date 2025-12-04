
import java.awt.*;

public class ShieldPickup extends FallingObject implements Collidable {

    public ShieldPickup(int x, int y, double speed, Image img) {
        super(x, y, speed, img);

        // ukuran seragam
        this.width = 128;
        this.height = 128;
    }

    @Override
    public void update() {
        y += speed;
    }

    @Override
    public boolean collidesWith(Player p, GamePanel panel) {
        Rectangle playerHitbox = p.getBounds();

        // Hitbox shield dibuat sangat kecil agar hanya body player yg bisa ambil
        int narrowWidth = width / 3;   // hanya 33% dari lebar aslinya
        int narrowHeight = height / 2; // 50% tinggi
        int offsetX = (width - narrowWidth) / 2;
        int offsetY = (height - narrowHeight) / 2;

        Rectangle shieldHitbox = new Rectangle(
            x + offsetX,
            y + offsetY,
            narrowWidth,
            narrowHeight
        );

        return playerHitbox.intersects(shieldHitbox);
    }


    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }
}

import java.awt.*;

/**
 * HealthPickup merupakan objek penyembuh.
 * Jika player menyentuh, health bertambah hingga maksimal 3.
 * Collision menggunakan pixel-perfect untuk akurasi tinggi.
 */
public class HealthPickup extends FallingObject implements Collidable {

    public HealthPickup(int x, int y, double speed, Image img) {
        super(x, y, speed, img);
    }

    /**
     * Health jatuh lurus ke bawah.
     */
    @Override
    public void update() {
        y += speed;
    }

    /**
     * Menggunakan collision pixel-perfect agar pickup terasa natural.
     */
    @Override
    public boolean collidesWith(Player p, GamePanel panel) {
        return panel.pixelPerfectCollision(
                p.getImage(), p.getX(), p.getY(),
                this.image, this.x, this.y
        );
    }
}

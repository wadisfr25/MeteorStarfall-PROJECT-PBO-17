import java.awt.*;

public class Star extends FallingObject implements Collidable {

    public Star(int x, int y, double speed, Image img) {
        super(x, y, speed, img);
    }
        @Override
    public void update() {
        y += speed;
    }
    public boolean collidesWith(Player p, GamePanel panel) {
        return false;  // Star tidak punya efek collision dalam game Anda
    }
}


import java.awt.*;

public class Meteor extends FallingObject implements Collidable {

    public Meteor(int x, int y, double speed, Image img) {
        super(x, y, speed, img);
    }

    @Override
    public void update() {
        y += speed;
    }
@Override
    public boolean collidesWith(Player p, GamePanel panel) {
        return panel.pixelPerfectCollision(
                p.getImage(), p.getX(), p.getY(),
                this.image, this.x, this.y
        );
    }
    
}

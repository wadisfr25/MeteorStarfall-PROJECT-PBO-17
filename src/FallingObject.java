import java.awt.*;

public abstract class FallingObject {
    protected int x, y;
    protected int width, height;
    protected double speed;
    protected Image image;

    public FallingObject(int x, int y, double speed, Image image) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.image = image;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }       
    public abstract void update();

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public Image getImage() { return image; }
    public int getX() { return x; }
    public int getY() { return y; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isOutOfScreen(int panelHeight) {
        return y > panelHeight;
    }
}
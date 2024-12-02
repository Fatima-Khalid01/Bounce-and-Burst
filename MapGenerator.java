import java.awt.*;
import java.util.ArrayList;

public class MapGenerator {
    public ArrayList<Balloon> balloons;
    public final int BALLOON_WIDTH = 30;
    public final int BALLOON_HEIGHT = 40;

    public MapGenerator(int rows, int cols) {
        balloons = new ArrayList<>();
        initBalloons(rows, cols);
    }

    private void initBalloons(int rows, int cols) {
        int gapX = 10;
        int gapY = 20;

        // Calculate the total width of the balloons layout
        int totalWidth = cols * BALLOON_WIDTH + (cols - 1) * gapX;
        int startX = (600 - totalWidth) / 2; // Center balloons horizontally on a 600px screen
        int startY = 50; // Starting y-position for the balloons

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = startX + col * (BALLOON_WIDTH + gapX);
                int y = startY + row * (BALLOON_HEIGHT + gapY);
                balloons.add(new Balloon(x, y, Color.YELLOW));
            }
        }
    }

    public ArrayList<Balloon> getBalloons() {
        return balloons;
    }

    public void draw(Graphics g) {
        for (Balloon balloon : balloons) {
            balloon.draw(g);
        }
    }

    public void removeBalloon(int index) {
        if (index >= 0 && index < balloons.size()) {
            balloons.remove(index);
        }
    }

    class Balloon {
        int x, y;
        Color color;

        Balloon(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, BALLOON_WIDTH, BALLOON_HEIGHT);
            g.setColor(Color.WHITE);
            g.drawLine(x + BALLOON_WIDTH / 2, y + BALLOON_HEIGHT, x + BALLOON_WIDTH / 2, y + BALLOON_HEIGHT + 10);
        }

        public Rectangle getRect() {
            return new Rectangle(x, y, BALLOON_WIDTH, BALLOON_HEIGHT); // For collision detection
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class ReverseSnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reverse Snake Game");
            ReverseGamePanel panel = new ReverseGamePanel();

            frame.add(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.startGame();
        });
    }
}

class ReverseGamePanel extends JPanel implements KeyListener {

    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 20;
    private final int DELAY = 120;

    private LinkedList<Point> snake;
    private Point food;
    private char direction = 'R';
    private boolean running = true;
    private boolean won = false;

    private Thread gameThread;

    public ReverseGamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        initGame();
    }

    private void initGame() {
        snake = new LinkedList<>();
        snake.add(new Point(200, 200));
        snake.add(new Point(180, 200));
        snake.add(new Point(160, 200));
        spawnFood();
    }

    public void startGame() {
        gameThread = new Thread(() -> {
            while (running) {
                move();
                checkFood();
                checkCollision();
                repaint();
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException ignored) {}
            }
        });
        gameThread.start();
    }

    private void spawnFood() {
        Random rand = new Random();
        food = new Point(
                rand.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE,
                rand.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE
        );
    }

    private void move() {
        Point head = new Point(snake.getFirst());

        switch (direction) {
            case 'U': head.y -= UNIT_SIZE; break;
            case 'D': head.y += UNIT_SIZE; break;
            case 'L': head.x -= UNIT_SIZE; break;
            case 'R': head.x += UNIT_SIZE; break;
        }

        snake.addFirst(head);
        snake.removeLast();
    }

    private void checkFood() {
        if (snake.getFirst().equals(food)) {
            if (snake.size() > 1) {
                snake.removeLast(); // shrink
                spawnFood();
            } else {
                won = true;
                running = false;
            }
        }
    }

    private void checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            g.setColor(Color.ORANGE);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snake.size(); i++) {
                g.setColor(i == 0 ? Color.CYAN : Color.GREEN);
                Point p = snake.get(i);
                g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            showEndScreen(g);
        }
    }

    private void showEndScreen(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(won ? Color.GREEN : Color.RED);
        String msg = won ? "YOU WIN!" : "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (WIDTH - fm.stringWidth(msg)) / 2, HEIGHT / 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
            case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
            case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
            case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TwoPacmanGame extends JPanel implements ActionListener {

    final int TILE = 25;
    final int ROWS = 20;
    final int COLS = 25;

    int[][] map = new int[ROWS][COLS];

    Pacman p1, p2;

    Timer timer;

    public TwoPacmanGame() {
        setPreferredSize(new Dimension(COLS * TILE, ROWS * TILE));
        setBackground(Color.BLACK);

        
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r == 0 || c == 0 || r == ROWS - 1 || c == COLS - 1)
                    map[r][c] = 1;  
                else if ((r + c) % 2 == 0)
                    map[r][c] = 2;  
                else
                    map[r][c] = 3;  
            }
        }

       
        p1 = new Pacman(2 * TILE, 2 * TILE, Color.YELLOW, 2);
        p2 = new Pacman((COLS - 3) * TILE, (ROWS - 3) * TILE, Color.BLUE, 3);

        timer = new Timer(30, this);
        timer.start();

        setupKeyBindings();
    }

    
    private void setupKeyBindings() {
       
        bindKey("W", 0, -1, p1);
        bindKey("S", 0, 1, p1);
        bindKey("A", -1, 0, p1);
        bindKey("D", 1, 0, p1);

        
        bindKey("UP", 0, -1, p2);
        bindKey("DOWN", 0, 1, p2);
        bindKey("LEFT", -1, 0, p2);
        bindKey("RIGHT", 1, 0, p2);
    }

    private void bindKey(String key, int dx, int dy, Pacman p) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key + p.hashCode());
        getActionMap().put(key + p.hashCode(), new AbstractAction() {
            
            public void actionPerformed(ActionEvent e) {
                p.setDirection(dx, dy);
            }
        });
    }

   
    public void actionPerformed(ActionEvent e) {
        p1.move();
        p2.move();

        eatDots(p1);
        eatDots(p2);

        checkCollision();

        repaint();
    }

    
    void eatDots(Pacman p) {
        int r = p.y / TILE;
        int c = p.x / TILE;

        if (map[r][c] == p.dotColor) {
            map[r][c] = 0;
            p.score++;
        }
    }

   
    void checkCollision() {
        if (Math.abs(p1.x - p2.x) < TILE && Math.abs(p1.y - p2.y) < TILE) {

            
            p1.freeze(15);
            p2.freeze(15);

           
            p1.score = Math.max(0, p1.score - 1);
            p2.score = Math.max(0, p2.score - 1);

            
            p1.x = 2 * TILE;
            p1.y = 2 * TILE;

            p2.x = (COLS - 3) * TILE;
            p2.y = (ROWS - 3) * TILE;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int tile = map[r][c];

                if (tile == 1) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(c * TILE, r * TILE, TILE, TILE);
                }
                if (tile == 2) {
                    g.setColor(Color.ORANGE);
                    g.fillOval(c * TILE + 10, r * TILE + 10, 6, 6);
                }
                if (tile == 3) {
                    g.setColor(Color.CYAN);
                    g.fillOval(c * TILE + 10, r * TILE + 10, 6, 6);
                }
            }
        }

        
        p1.draw(g);
        p2.draw(g);

      
        g.setColor(Color.WHITE);
        g.drawString("P1 Score: " + p1.score, 10, 15);
        g.drawString("P2 Score: " + p2.score, 120, 15);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Two Pac-Man Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TwoPacmanGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

 
    class Pacman {
        int x, y;
        int dx = 0, dy = 0;
        int speed = 3;
        int freezeFrames = 0;

        Color color;
        int dotColor;
        int score = 0;

        Pacman(int x, int y, Color color, int dotColor) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.dotColor = dotColor;
        }

        void setDirection(int dx, int dy) {
            if (freezeFrames == 0) {
                this.dx = dx;
                this.dy = dy;
            }
        }

        void move() {
            if (freezeFrames > 0) {
                freezeFrames--;
                return;
            }

            int newX = x + dx * speed;
            int newY = y + dy * speed;

            int r = newY / TILE;
            int c = newX / TILE;

            if (map[r][c] != 1) {
                x = newX;
                y = newY;
            }
        }

        void freeze(int frames) {
            freezeFrames = frames;
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, TILE, TILE);
        }
    }
}


import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class MockingBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird class
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {

        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {

        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    boolean gameStarted = false;
    double score = 0;
    int highScore = 0;

    MockingBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("./image/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./image/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./image/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./image/bottompipe.png")).getImage();

        // Bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    void startGame() {
        gameStarted = true;
        // Place pipes timer
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        String scoreText = String.valueOf((int) score);
        int textWidth = fm.stringWidth(scoreText);

        // Add shadow to text
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(scoreText, boardWidth / 2 - textWidth / 2 + 2, 52);

        g.setColor(Color.white);
        g.drawString(scoreText, boardWidth / 2 - textWidth / 2, 50);

        // Draw high score
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g.getFontMetrics();
        String highScoreText = "Best: " + highScore;
        textWidth = fm.stringWidth(highScoreText);
        g.drawString(highScoreText, boardWidth - textWidth - 10, 30);

        if (gameOver) {
            // Game over panel
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(boardWidth / 2 - 100, boardHeight / 2 - 80, 200, 160, 20, 20);

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            fm = g.getFontMetrics();
            String gameOverText = "GAME OVER";
            textWidth = fm.stringWidth(gameOverText);
            g.drawString(gameOverText, boardWidth / 2 - textWidth / 2, boardHeight / 2 - 30);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            fm = g.getFontMetrics();
            String scoreDisplay = "Score: " + (int) score;
            textWidth = fm.stringWidth(scoreDisplay);
            g.drawString(scoreDisplay, boardWidth / 2 - textWidth / 2, boardHeight / 2);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String instruction = "Press SPACE to restart";
            textWidth = fm.stringWidth(instruction);
            g.drawString(instruction, boardWidth / 2 - textWidth / 2, boardHeight / 2 + 40);
        } else if (!gameStarted) {
            // Start screen
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRoundRect(boardWidth / 2 - 150, boardHeight / 2 - 100, 300, 150, 20, 20);

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            fm = g.getFontMetrics();
            String title = "MOCKING BIRD";
            textWidth = fm.stringWidth(title);
            g.drawString(title, boardWidth / 2 - textWidth / 2, boardHeight / 2 - 40);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String instruction = "Press SPACE to start";
            textWidth = fm.stringWidth(instruction);
            g.drawString(instruction, boardWidth / 2 - textWidth / 2, boardHeight / 2 + 20);
        }
    }

    public void move() {
        if (!gameStarted || gameOver) {
            return;
        }

        // Move bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // Move and check pipes
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            // Score points when passing pipes
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                if (i % 2 == 0) { // Only count score once per pipe pair
                    score += 1;
                }
            }

            // Remove pipes that are off screen
            if (pipe.x < -pipe.width) {
                pipes.remove(i);
                continue;
            }

            // Check collision
            if (collision(bird, pipe)) {
                gameOver = true;
                if ((int) score > highScore) {
                    highScore = (int) score;
                }
            }
        }

        // Check floor collision
        if (bird.y > boardHeight - bird.height) {
            bird.y = boardHeight - bird.height;
            gameOver = true;
            if ((int) score > highScore) {
                highScore = (int) score;
            }
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width - 5
                && // Small adjustment for more forgiving hitbox
                a.x + a.width - 5 > b.x
                && a.y < b.y + b.height - 5
                && a.y + a.height - 5 > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            if (placePipeTimer != null) {
                placePipeTimer.stop();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                startGame();
            } else if (gameOver) {
                resetGame();
            } else {
                velocityY = -9; // Flap
            }
        }
    }

    void resetGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        startGame();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mocking Bird");
        MockingBird game = new MockingBird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GamePlay extends JPanel implements ActionListener, KeyListener {
    private final String username;
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private Timer timer;
    private boolean gameOver = false;
    private boolean isBallLaunched = false;
    private int score;
    private Rectangle paddle, ball;
    private long startTime;
    private int ballVelocityX = 5;
    private int ballVelocityY = -5;
    private int maxUnlockedLevel = 1; // Start with only level 1 unlocked

    private boolean win = false;
    private MapGenerator mapGenerator;
    private final Scoreboard scoreboard;

    private int currentLevel;

    // Level-specific variables
    private int ballSpeed;
    private int paddleWidth;
    private int balloonRows;

    public GamePlay(String username, Scoreboard scoreboard, int level) {
        this.username = username;
        this.scoreboard = scoreboard;
        this.currentLevel = level; // Set the current level
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();
        initGame();
    }

    private void initGame() {
        // Initialize level-specific settings
        switch (currentLevel) {
            case 1: // Easy
                ballSpeed = 3;          // Slower ball speed
                paddleWidth = 120;      // Wider paddle
                balloonRows = 3;        // Fewer rows of balloons
                break;
            case 2: // Medium
                ballSpeed = 4;          // Medium ball speed
                paddleWidth = 120;      // Medium paddle width
                balloonRows = 4;        // More rows of balloons
                break;
            case 3: // Hard
                ballSpeed = 5;          // Faster ball speed
                paddleWidth = 120;      // Narrower paddle
                balloonRows = 5;        // Maximum rows of balloons
                break;
            default:
                ballSpeed = 3;          // Default to easy
                paddleWidth = 120;
                balloonRows = 3;
                break;
        }

        // Initialize paddle and ball
        paddle = new Rectangle(SCREEN_WIDTH / 2 - paddleWidth / 2, SCREEN_HEIGHT - 50, paddleWidth, 10);
        ball = new Rectangle(SCREEN_WIDTH / 2 - 10, paddle.y - 20, 20, 20);

        // Initialize balloons
        mapGenerator = new MapGenerator(balloonRows, 7); // Rows and columns of balloons

        // Initialize timer
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(15, this);
        timer.start();

        score = 0;
        gameOver = false;
        isBallLaunched = false;
        win = false;
        startTime = System.currentTimeMillis();

        // Set ball velocity based on speed
        ballVelocityX = ballSpeed;
        ballVelocityY = -ballSpeed;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the paddle and ball
        g.setColor(Color.GREEN);
        g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
        g.setColor(Color.WHITE);
        g.fillOval(ball.x, ball.y, ball.width, ball.height);

        // Draw score and time
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 20, 30);

        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        g.drawString("Time: " + elapsedTime + "s", SCREEN_WIDTH - 120, 30);

        mapGenerator.draw(g);

        // Draw the celebratory animation
        if (win && currentLevel == 3) {
            g.setColor(Color.CYAN);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            g.drawString("HURRAY!", SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 - 50);

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (isBallLaunched) {
                moveBall();
                checkBalloonCollision();
            } else {
                ball.x = paddle.x + paddle.width / 2 - ball.width / 2;
                ball.y = paddle.y - ball.height;
            }
        }
        repaint();
    }

    private void moveBall() {
        ball.x += ballVelocityX;
        ball.y += ballVelocityY;

        if (ball.intersects(paddle)) {
            ballVelocityY = -ballVelocityY;
            ball.y = paddle.y - ball.height;
        }

        if (ball.x <= 0 || ball.x + ball.width >= SCREEN_WIDTH) {
            ballVelocityX = -ballVelocityX;
        }

        if (ball.y <= 0) {
            ballVelocityY = -ballVelocityY;
        }

        // Game over condition: ball falls below the paddle
        if (ball.y + ball.height > SCREEN_HEIGHT) {
            gameOver = true;
            timer.stop();
            showEndScreen(); // Show the dialog box instead of red "Game Over"
        }
    }

    private void checkBalloonCollision() {
        for (int i = 0; i < mapGenerator.getBalloons().size(); i++) {
            Rectangle balloonRect = mapGenerator.getBalloons().get(i).getRect();
            if (ball.intersects(balloonRect)) {
                ballVelocityY = -ballVelocityY;
                mapGenerator.removeBalloon(i);
                score += 2;
                repaint();
                break;
            }
        }

        if (mapGenerator.getBalloons().isEmpty() && !win) {
            win = true;
            timer.stop();

            // Trigger end screen with animation if it's level 3
            if (currentLevel == 3) {
                showAnimationAndCelebrate();
            } else {
                Timer delayTimer = new Timer(300, e -> showEndScreen());
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        }
    }

    private void showAnimationAndCelebrate() {
        // Start the animation when Level 3 is completed
        Timer animationTimer = new Timer(100, new ActionListener() {
            int frame = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                frame++;

                // Draw celebratory message and animations (such as confetti)
                repaint();

                // After some frames, show the "HURRAY!" message and stop the animation
                if (frame > 50) {  // Adjust the frame number to control animation duration
                    ((Timer) e.getSource()).stop();  // Stop the animation
                    showCongratulationsMessage();
                }
            }
        });
        animationTimer.setRepeats(true);
        animationTimer.start();
    }

    private void showCongratulationsMessage() {
        // Display a "HURRAY!" message with an animation
        JOptionPane.showMessageDialog(this,
                "HURRAY! You won all the levels!",
                "Congratulations",
                JOptionPane.INFORMATION_MESSAGE);

        // Show the scoreboard after animation
        showScoreboard();
    }

    private void showEndScreen() {
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        scoreboard.addScore(username, score, totalTime); // Add score to the current level

        if (win) {
            // Win dialog
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Congratulations! You won Level " + currentLevel + "!",
                    "Level Completed",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"View Scoreboard", "Exit"},
                    null
            );

            if (choice == 0) {
                showScoreboard(); // View the scoreboard
            } else if (choice == 1) {
                System.exit(0); // Exit the game
            }
        } else {
            // Loss dialog (Game Over)
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Game Over! You failed Level " + currentLevel + ".",
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Retry Level", "View Scoreboard", "Exit"},
                    null
            );

            if (choice == 0) {
                initGame(); // Retry the level
                timer.start();
            } else if (choice == 1) {
                showScoreboard(); // View the scoreboard
            } else if (choice == 2) {
                System.exit(0); // Exit the game
            }
        }
    }


    private void showScoreboard() {
        // Create and display the scoreboard window
        JFrame scoreboardFrame = new JFrame("Scoreboard" + currentLevel);
        scoreboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        scoreboardFrame.setSize(400, 300);

// Callback to restart the current level
        Runnable retryLevelCallback = () -> {
            scoreboardFrame.dispose();  // Close the scoreboard window
            initGame();                 // Reinitialize the current level
            timer.start();              // Restart the timer
        };

// Callback to move to the next level
        Runnable nextLevelCallback = () -> {
            scoreboardFrame.dispose();  // Close the scoreboard window
            if (currentLevel < 3) {     // Check if there is a next level
                currentLevel++;         // Increment the level
                initGame();             // Initialize the next level
                timer.start();          // Restart the timer for the game

            }
        };

// Assuming the level is defined and the scoreboard object is created:
        int level = 1;  // or get the current level dynamically
        JPanel scoreboardPanel = scoreboard.getScoreboardPanel(currentLevel, retryLevelCallback, nextLevelCallback);
        // Adding scoreboard panel
        scoreboardFrame.add(scoreboardPanel);

        // Ensure the window appears in the center
        scoreboardFrame.setLocationRelativeTo(null);
        scoreboardFrame.setVisible(true);
    }



    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT && paddle.x > 0) {
            paddle.x -= 15;
        } else if (keyCode == KeyEvent.VK_RIGHT && paddle.x + paddle.width < SCREEN_WIDTH) {
            paddle.x += 15;
        } else if (keyCode == KeyEvent.VK_ENTER && gameOver) {
            initGame();
            timer.start();
        } else if (keyCode == KeyEvent.VK_SPACE && !isBallLaunched) {
            isBallLaunched = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}

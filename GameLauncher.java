import javax.swing.*;
import java.awt.*;

public class GameLauncher extends JFrame {

    public GameLauncher() {
        // Set up the main game window
        setTitle("Bounce & Burst");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Add image as background
        try {
            setContentPane(new BackgroundPanel("/logo.png")); // Load image from resources
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }

        // Create a panel for the play button
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false); // Make the main panel transparent

        // Play button
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
        playButton.setPreferredSize(new Dimension(300, 100));
        playButton.setBackground(Color.YELLOW);
        playButton.setForeground(Color.BLACK);
        playButton.setFocusPainted(false);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        playButton.addActionListener(e -> {
            JFrame loginFrame = new JFrame("Player's Name");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(800, 600);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.add(new LoginScreen(this)); // Assuming LoginScreen is implemented
            loginFrame.setVisible(true);
        });

        // Add play button to the panel
        mainPanel.add(Box.createVerticalStrut(300)); // Space above the button
        mainPanel.add(playButton);

        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Custom Panel to display image as background
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            } catch (Exception e) {
                System.out.println("Error loading background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Method to start the game after login
    public void startGame(String username, int level) {
        try {
            System.out.println("Game starting for: " + username + " at Level: " + level);

            JFrame gameFrame = new JFrame("Bounce & Burst Game - Level " + level);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(600, 600);
            gameFrame.setLocationRelativeTo(null);
            gameFrame.add(new GamePlay(username, new Scoreboard(), level)); // Assuming GamePlay and Scoreboard are implemented
            gameFrame.setVisible(true);

            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error starting the game: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to show level selection
    public void showLevelSelection(String username) {
        JFrame levelFrame = new JFrame("Select Level");
        levelFrame.setSize(500, 500);
        levelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        levelFrame.setLocationRelativeTo(null);
        levelFrame.setLayout(new GridLayout(4, 1));

        JLabel instructionLabel = new JLabel("Select a Level:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        levelFrame.add(instructionLabel);

        JButton level1Button = new JButton("Level 1");
        JButton level2Button = new JButton("Level 2");
        JButton level3Button = new JButton("Level 3");

        level1Button.addActionListener(e -> {
            startGame(username, 1);
            levelFrame.dispose();
        });
        level2Button.addActionListener(e -> {
            startGame(username, 2);
            levelFrame.dispose();
        });
        level3Button.addActionListener(e -> {
            startGame(username, 3);
            levelFrame.dispose();
        });

        levelFrame.add(level1Button);
        levelFrame.add(level2Button);
        levelFrame.add(level3Button);

        levelFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new GameLauncher();
    }
}

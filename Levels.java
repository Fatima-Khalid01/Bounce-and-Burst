import javax.swing.*;
import java.awt.GridLayout;


class MainMenu extends JFrame {
    private final String username;
    private final Scoreboard scoreboard;
    private int maxUnlockedLevel;

    public MainMenu(String username, Scoreboard scoreboard, int maxUnlockedLevel) {
        this.username = username;
        this.scoreboard = scoreboard;
        this.maxUnlockedLevel = maxUnlockedLevel;
        setTitle("Bounce & Burst - Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JButton level1Button = new JButton("Level 1");
        JButton level2Button = new JButton("Level 2");
        JButton level3Button = new JButton("Level 3");

        // Add action listeners
        level1Button.addActionListener(e -> startLevel(1));
        level2Button.addActionListener(e -> checkAndStartLevel(2));
        level3Button.addActionListener(e -> checkAndStartLevel(3));

        add(level1Button);
        add(level2Button);
        add(level3Button);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startLevel(int level) {
        dispose(); // Close the menu window
        JFrame gameFrame = new JFrame("Bounce & Burst - Level " + level);
        GamePlay gamePlay = new GamePlay(username, scoreboard, level);
        gameFrame.add(gamePlay);
        gameFrame.setSize(600, 600);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }

    private void checkAndStartLevel(int level) {
        if (level <= maxUnlockedLevel) {
            startLevel(level);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Level " + level + " is locked! Complete the previous levels to unlock.",
                    "Level Locked",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
}

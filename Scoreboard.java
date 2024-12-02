import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class PlayerScore {
    private final String username;
    private final int score;
    private final long time;

    public PlayerScore(String username, int score, long time) {
        this.username = username;
        this.score = score;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Name: " + username + " | Score: " + score + " | Time: " + time + "s";
    }
}

public class Scoreboard {
    private int currentLevel = 1; // Tracks the current level

    // Add the player's score to the specific level's scoreboard
    public void addScore(String username, int score, long totalTime) {
        String fileName = getLevelFileName(currentLevel);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            // Use CSV format to align with readScores parsing
            writer.write(username + "," + score + "," + totalTime);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read the scores for a specific level
    private List<PlayerScore> readScores(int level) {
        List<PlayerScore> scores = new ArrayList<>();
        String fileName = getLevelFileName(level);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    long time = Long.parseLong(parts[2]);
                    scores.add(new PlayerScore(username, score, time));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scores.sort((a, b) -> b.getScore() - a.getScore()); // Sort scores in descending order
        return scores;
    }

    // Generate the filename for the current level's scoreboard
    private String getLevelFileName(int level) {
        return "scoreboard_level_" + level + ".txt";
    }

    // Create the panel displaying the scoreboard for a specific level
    public JPanel getScoreboardPanel(int level, Runnable retryCallback, Runnable nextLevelCallback) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(173, 216, 230)); // Light blue background color

        // Create the scoreboard content panel using GridLayout
        JPanel scorePanel = new JPanel(new GridLayout(0, 4, 5, 5)); // 4 columns: Winner, Name, Score, Time
        scorePanel.setBackground(new Color(173, 216, 230)); // Light blue background color

        // Add headers to the scoreboard
        JLabel winnerHeader = new JLabel("Winner", SwingConstants.CENTER);
        winnerHeader.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel nameHeader = new JLabel("Name", SwingConstants.CENTER);
        nameHeader.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel scoreHeader = new JLabel("Score", SwingConstants.CENTER);
        scoreHeader.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel timeHeader = new JLabel("Time Taken", SwingConstants.CENTER);
        timeHeader.setFont(new Font("Arial", Font.BOLD, 16));

        scorePanel.add(winnerHeader);
        scorePanel.add(nameHeader);
        scorePanel.add(scoreHeader);
        scorePanel.add(timeHeader);

        // Fetch and display the scores for the selected level
        List<PlayerScore> scores = readScores(level);

        for (int i = 0; i < scores.size(); i++) {
            PlayerScore ps = scores.get(i);

            // Winner column content
            JLabel winnerLabel = new JLabel("", SwingConstants.CENTER);
            winnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            if (i == 0) {
                winnerLabel.setText("1st");
                winnerLabel.setForeground(Color.magenta); // Dark green color for the first place
            } else if (i == 1) {
                winnerLabel.setText("2nd");
                winnerLabel.setForeground(Color.BLUE); // Blue color for the second place
            } else if (i == 2) {
                winnerLabel.setText("3rd");
                winnerLabel.setForeground(Color.red); // Bronze color for the third place
            }

            JLabel nameLabel = new JLabel(ps.getUsername(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            JLabel scoreLabel = new JLabel(String.valueOf(ps.getScore()), SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            JLabel timeLabel = new JLabel(formatTime(ps.getTime()), SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            scorePanel.add(winnerLabel);
            scorePanel.add(nameLabel);
            scorePanel.add(scoreLabel);
            scorePanel.add(timeLabel);
        }

        panel.add(scorePanel, BorderLayout.CENTER);

        // Add buttons for retrying or proceeding to the next level
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(173, 216, 230)); // Light blue background color
        JButton retryButton = new JButton("Retry Level");
        retryButton.addActionListener(e -> retryCallback.run());
        buttonPanel.add(retryButton);

        JButton nextLevelButton = new JButton("Next Level");
        nextLevelButton.setEnabled(level < 3); // Disable if it's the last level
        nextLevelButton.addActionListener(e -> {
            currentLevel++;
            nextLevelCallback.run();
        });
        buttonPanel.add(nextLevelButton);

        // Add "Exit" button for level 3
        if (level == 3) {
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> System.exit(0)); // Exit the program
            buttonPanel.add(exitButton);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Format the time to minutes and seconds for display
    private String formatTime(long timeInSeconds) {
        if (timeInSeconds < 60) {
            return timeInSeconds + "s";
        } else {
            long minutes = timeInSeconds / 60;
            long seconds = timeInSeconds % 60;
            return minutes + " min " + seconds + "s";
        }
    }

    // Method to show the scoreboard in a new window (for the current level)
    public void showScoreboard(int level) {
        JFrame scoreboardFrame = new JFrame("Scoreboard - Level " + level);
        scoreboardFrame.setSize(600, 400);
        scoreboardFrame.setLocationRelativeTo(null);

        // Light blue background color for the content pane
        scoreboardFrame.getContentPane().setBackground(new Color(173, 216, 230));

        Runnable restartCallback = () -> {
            scoreboardFrame.dispose();
            // Restart game logic (reload the current level)
        };

        Runnable nextLevelCallback = () -> {
            scoreboardFrame.dispose();
            // Proceed to the next level
        };

        JPanel scoreboardPanel = getScoreboardPanel(level, restartCallback, nextLevelCallback);
        scoreboardFrame.add(scoreboardPanel);

        scoreboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        scoreboardFrame.setVisible(true);
    }
}

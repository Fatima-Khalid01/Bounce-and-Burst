import javax.swing.*;
import java.awt.*;

class LoginScreen extends JPanel {
    private final JTextField nameField;

    public LoginScreen(GameLauncher gameLauncher) {
        setLayout(new GridBagLayout()); // Use GridBagLayout for more control over component placement
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Set preferred size of the panel for a larger window
        setPreferredSize(new Dimension(800, 600));

        // Create title label
        JLabel titleLabel = new JLabel("Enter Your Name: ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size for title
        titleLabel.setHorizontalAlignment(JLabel.CENTER); // Center the title
        titleLabel.setForeground(Color.YELLOW); // Set the color of the label to yellow

        // Create username field
        nameField = new JTextField(20); // Limit the number of characters
        nameField.setFont(new Font("Arial", Font.PLAIN, 16)); // Increase font size for text field

        // Create next button
        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 16)); // Increase font size for button

        // Positioning components in the GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Make title span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset gridwidth for other components
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST; // Align labels to the right

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align text fields to the left
        add(nameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(nextButton, gbc);

        // Action listener for the next button
        nextButton.addActionListener(e -> {
            String username = nameField.getText().trim();
            if (!username.isEmpty()) {
                gameLauncher.showLevelSelection(username); // Show level selection screen
                SwingUtilities.getWindowAncestor(this).dispose(); // Close the login screen
            } else {
                JOptionPane.showMessageDialog(this, "Please enter your name.");
            }
        });
    }

    // Custom paintComponent to add the background image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Load and draw the background image
        Image backgroundImage = getImage("/enter.png"); // Use relative path for portability
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Scale the image to fit the panel
        }
    }

    // Utility method to load images using class resources
    private Image getImage(String imagePath) {
        try {
            return new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            return null;
        }
    }
}

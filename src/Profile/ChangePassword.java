package Profile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Data.DatabaseManager;

public class ChangePassword extends JPanel {

    private static final long serialVersionUID = 1L;
    private String username;
    private String tableName;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private int wrongAttempts = 0;

    public ChangePassword(String tableName, String username) {
        this.username = username;
        this.tableName = tableName;

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        DatabaseManager databaseManager = new DatabaseManager(); // Assuming you have a DatabaseManager class
        // You may also need to establish a database connection here.

        // Remove the username label here
        // JLabel nameLabel = new JLabel("Name: " + fullName);
        // add(nameLabel);

        // Add a password change section
        JLabel changePasswordLabel = new JLabel("Change Password:");
        JPasswordField oldPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        JButton changePasswordButton = new JButton("Change Password");

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle password change logic here
                String oldPassword = new String(oldPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (wrongAttempts >= 5) {
                    JOptionPane.showMessageDialog(null, "You have exceeded the maximum number of wrong attempts. Logging out.");
                    logout();
                    return;
                }

                if (databaseManager.checkPassword(tableName, username, oldPassword)) {
                    // Check if the new password and confirm password match
                    if (newPassword.equals(confirmPassword)) {
                        // Update the password in the database
                        if (databaseManager.updatePassword(tableName, username, newPassword)) {
                            JOptionPane.showMessageDialog(null, "Password changed successfully!");
                            showLogoutPanel();
                        } else {
                            JOptionPane.showMessageDialog(null, "Password change failed.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "New password and confirm password do not match.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect old password.");
                    wrongAttempts++;
                }
            }
        });

        // Create a panel for the password change form
        JPanel passwordChangePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Add components to the passwordChangePanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        passwordChangePanel.add(changePasswordLabel, gbc);

        gbc.gridy++;
        passwordChangePanel.add(new JLabel("Old Password:"), gbc);
        gbc.gridy++;
        passwordChangePanel.add(oldPasswordField, gbc);

        gbc.gridy++;
        passwordChangePanel.add(new JLabel("New Password:"), gbc);
        gbc.gridy++;
        passwordChangePanel.add(newPasswordField, gbc);

        gbc.gridy++;
        passwordChangePanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridy++;
        passwordChangePanel.add(confirmPasswordField, gbc);

        gbc.gridy++;
        passwordChangePanel.add(changePasswordButton, gbc);

        // Add the passwordChangePanel to the cardPanel
        cardPanel.add(passwordChangePanel, "passwordChange");

        // Create a logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Add the logoutButton to a panel
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(logoutButton);

        // Add the logoutPanel to the cardPanel
        cardPanel.add(logoutPanel, "logout");

        // Show the password change panel initially
        showPasswordChangePanel();

        // Add the cardPanel to the main panel
        add(cardPanel);
    }

    private void showPasswordChangePanel() {
        cardLayout.show(cardPanel, "passwordChange");
    }

    private void showLogoutPanel() {
        cardLayout.show(cardPanel, "logout");
    }

    private void logout() {
        System.exit(0);
    }

    public static void createAndShowMainFrame(ChangePassword changePassword) {
        JFrame frame = new JFrame("Change Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(changePassword);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
}

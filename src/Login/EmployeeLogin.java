package Login;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Main.Application;
import Main.Employee;
import Data.DatabaseManager;

public class EmployeeLogin {
    private static int loginAttempts = 0;
    public static JPanel createLoginPanel(JFrame mainFrame) {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.insets = new Insets(10, 10, 10, 10);

        JLabel customer = new JLabel("Employee Login");

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton goBackButton = new JButton("Go Back"); // Add a "Go Back" button
        JButton forgetPasswordButton = new JButton("Forget Password"); // Add a "Forget Password" button
        
        goBackButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                Application.createAndShowUI();
                mainFrame.dispose();
            });
        });

        forgetPasswordButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> showForgotPasswordDialog(mainFrame));
            JOptionPane.showMessageDialog(mainFrame, "Redirecting to Forget Password page.");
        });

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection connection = DatabaseManager.getConnection()) {

                Statement statement = connection.createStatement();
                String query = "SELECT * FROM employee WHERE username = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    mainFrame.dispose();
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Employee frame = new Employee(username, "profile_pictures/" + username + ".jpg");
                            frame.setVisible(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    loginAttempts++;
                    if (loginAttempts >= 3) {
                        int option = JOptionPane.showConfirmDialog(mainFrame,
                                "Invalid username or password. Do you want to reset your password?",
                                "Forget Password", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            SwingUtilities.invokeLater(() -> showForgotPasswordDialog(mainFrame));
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Invalid username or password.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error connecting to the database.");
            }
        });

        loginGbc.gridx = 1;
        loginGbc.gridy = 0;
        loginGbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(customer, loginGbc);

        loginGbc.gridx = 0;
        loginGbc.gridy = 1;
        loginPanel.add(usernameLabel, loginGbc);

        loginGbc.gridx = 1;
        loginGbc.gridy = 1;
        loginPanel.add(usernameField, loginGbc);

        loginGbc.gridx = 0;
        loginGbc.gridy = 2;
        loginPanel.add(passwordLabel, loginGbc);

        loginGbc.gridx = 1;
        loginGbc.gridy = 2;
        loginPanel.add(passwordField, loginGbc);

        loginGbc.gridx = 1;
        loginGbc.gridy = 3;
        loginGbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(loginButton, loginGbc);

        loginGbc.gridx = 1;
        loginGbc.gridy = 3;
        loginGbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(goBackButton, loginGbc);

        loginGbc.gridx = 1;
        loginGbc.gridy = 4;
        loginGbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(forgetPasswordButton, loginGbc);

        return loginPanel;
    }

    public static void showForgotPasswordDialog(JFrame parentFrame) {
        JTextField usernameField = new JTextField(20);
        JPasswordField newPasswordField = new JPasswordField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel,
                "Forgot Password", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String newPassword = new String(newPasswordField.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Username cannot be empty.",
                        "Password Reset", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection connection = DatabaseManager.getConnection()) {

                Statement statement = connection.createStatement();
                // Update the password in the database
                String updateQuery = "UPDATE user SET password = ? WHERE username = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, newPassword);
                updateStatement.setString(2, username);
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(parentFrame, "Password updated successfully!",
                            "Password Reset", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Username not found.",
                            "Password Reset", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error connecting to the database.",
                        "Password Reset", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

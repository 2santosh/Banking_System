package Customer;

import javax.swing.*;
import Data.DatabaseManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class LoanForm extends JFrame {
    private JTextField accountNoField;
    private JTextField loanDateField;
    private JTextField loanAmountField;
    private JTextArea statementTextArea;
    private JButton submitButton;

    public LoanForm() {
        setTitle("Loan Application Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(6, 2));

        JLabel accountNoLabel = new JLabel("Account Number:");
        accountNoField = new JTextField();
        JLabel loanDateLabel = new JLabel("Loan Date:");
        loanDateField = new JTextField();
        JLabel loanAmountLabel = new JLabel("Loan Amount:");
        loanAmountField = new JTextField();
        JLabel statementLabel = new JLabel("Statement:");
        statementTextArea = new JTextArea();

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String accountNo = accountNoField.getText();
                    String loanDate = loanDateField.getText();
                    double loanAmount = Double.parseDouble(loanAmountField.getText());
                    String statement = statementTextArea.getText();

                    if (isValidAccount(accountNo)) {
                        // Get the username based on the account number (You need to implement this method)
                        String username = getUsernameFromAccount(accountNo);

                        if (username != null) {
                            // Save loan application data to the username+loan table
                            saveLoanApplication(username, loanDate, loanAmount, statement);
                            JOptionPane.showMessageDialog(null, "Loan Application Submitted Successfully!");
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Account Number not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Account Number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numeric values.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(accountNoLabel);
        mainPanel.add(accountNoField);
        mainPanel.add(loanDateLabel);
        mainPanel.add(loanDateField);
        mainPanel.add(loanAmountLabel);
        mainPanel.add(loanAmountField);
        mainPanel.add(statementLabel);
        mainPanel.add(new JScrollPane(statementTextArea));
        mainPanel.add(new JLabel()); // Empty label for spacing
        mainPanel.add(submitButton);

        add(mainPanel);
        setVisible(true);
    }

    private boolean isValidAccount(String accountNo) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM user_accounts WHERE account_no = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, accountNo);
            ResultSet resultSet = preparedStatement.executeQuery();

            // If a row is found, the account number is valid
            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the database error appropriately
            return false;
        }
    }

    private String getUsernameFromAccount(String accountNo) {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT username FROM accounts WHERE account_no = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, accountNo);
            ResultSet resultSet = preparedStatement.executeQuery();

            // If a row is found, return the associated username
            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
                return null; // Account not found
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the database error appropriately
            return null;
        }
    }

    private void saveLoanApplication(String username, String loanDate, double loanAmount, String statement) {
        try (Connection connection = DatabaseManager.getConnection()) {
            // Prepare and execute an SQL query to insert the loan application data into the username+loan table
            String insertQuery = "INSERT INTO " + username + "_loan (date_time, loan_amount, statement) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, loanDate);
            preparedStatement.setDouble(2, loanAmount);
            preparedStatement.setString(3, statement);

            int rowsInserted = preparedStatement.executeUpdate();

            // Close the database connection
            preparedStatement.close();

            if (rowsInserted > 0) {
                // Loan application saved successfully
            } else {
                JOptionPane.showMessageDialog(null, "Failed to submit loan application.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while submitting the loan application.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

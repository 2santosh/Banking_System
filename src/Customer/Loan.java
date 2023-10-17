package Customer;

import javax.swing.*;
import Data.DatabaseManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Loan extends JFrame {
    private JTextField loanAmountField;
    private JTextArea statementArea;
    private JButton submitButton;
    private String username; // Store the username

    public Loan(String username) {
        this.username = username; // Set the username

        setTitle("Loan Application Form - User: " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2));

        JLabel loanAmountLabel = new JLabel("Loan Amount:");
        loanAmountField = new JTextField();
        JLabel statementLabel = new JLabel("Statement:");
        statementArea = new JTextArea();
        JScrollPane statementScrollPane = new JScrollPane(statementArea);
        
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get data from the form fields
                    double loanAmount = Double.parseDouble(loanAmountField.getText());
                    String statement = statementArea.getText();

                    if (!isValidLoanAmount(loanAmount)) {
                        JOptionPane.showMessageDialog(null, "Invalid loan amount. Please enter a valid numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Get the current date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currentDate = new Date();
                    String date_time = dateFormat.format(currentDate);

                    Connection connection = DatabaseManager.getConnection();

                    // Prepare and execute an SQL query to insert the loan application data into the user-specific loan table
                    String insertQuery = "INSERT INTO " + username + "_loan (date_time, loan_amount, statement, approve) VALUES (?, ?, ?, false)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setTimestamp(1, Timestamp.valueOf(date_time));
                    preparedStatement.setDouble(2, loanAmount);
                    preparedStatement.setString(3, statement);

                    int rowsInserted = preparedStatement.executeUpdate();

                    // Close the database connection and show a success message
                    preparedStatement.close();
                    connection.close();

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Loan Application Submitted Successfully!");
                        // Dispose of (close) the loan application form while keeping other frames open
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to submit loan application.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numeric values.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred while submitting the loan application.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add components to the main panel
        mainPanel.add(loanAmountLabel);
        mainPanel.add(loanAmountField);
        mainPanel.add(statementLabel);
        mainPanel.add(statementScrollPane);
        mainPanel.add(new JLabel()); // Empty label for spacing
        mainPanel.add(submitButton);

        add(mainPanel);
        setVisible(true);
    }

    private boolean isValidLoanAmount(double loanAmount) {
        // You can implement your loan amount validation logic here
        // For example, you may want to check if the loan amount is within a certain range
        return loanAmount >= 0; // Placeholder logic (non-negative loan amount)
    }
}

package Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Data.DatabaseManager;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class eLoan extends JPanel {

    private JTable loanApplicationTable;
    private DefaultTableModel tableModel;

    public eLoan() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a table model
        String[] columnNames = {"account_no", "username", "fullname", "account_amount", "loan_amount", "date", "statement", "Approve"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // Create a table and set the model
        loanApplicationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loanApplicationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch and display data for all users
        loadLoanData();

        // Create the "Apply Loan" button
        JButton applyLoanButton = new JButton("Apply Loan");
        applyLoanButton.addActionListener(e -> {
            // Show the loan application form here
            showLoanApplicationForm();
        });

        // Add the "Apply Loan" button to the panel
        add(applyLoanButton, BorderLayout.SOUTH);
    }

    private void loadLoanData() {
        tableModel.setRowCount(0); // Clear existing data
        try (Connection connection = DatabaseManager.getConnection()) {
            String userQuery = "SELECT username FROM customer"; // Replace "users" with your user table name
            PreparedStatement userStatement = connection.prepareStatement(userQuery);
            ResultSet userResult = userStatement.executeQuery();

            while (userResult.next()) {
                String username = userResult.getString("username");
                loadUserData(username);
            }

            userResult.close();
            userStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database.");
        }
    }

    private void loadUserData(String username) {
        try (Connection connection = DatabaseManager.getConnection()) {
            // Fetch data from the "account" table
            String accountQuery = "SELECT account_no, username, fullname, amount FROM account WHERE username = ?";
            PreparedStatement accountStatement = connection.prepareStatement(accountQuery);
            accountStatement.setString(1, username);
            ResultSet accountResult = accountStatement.executeQuery();

            // Fetch data from the "username+loan" table
            String loanQuery = "SELECT loan_amount, date, statement FROM " + username + "_loan"; // Adjust table name
            PreparedStatement loanStatement = connection.prepareStatement(loanQuery);
            ResultSet loanResult = loanStatement.executeQuery();

            while (accountResult.next() && loanResult.next()) {
                String accountNo = accountResult.getString("account_no");
                String fullName = accountResult.getString("fullname");
                double accountAmount = accountResult.getDouble("amount");
                double loanAmount = loanResult.getDouble("loan_amount");
                Date date = loanResult.getTimestamp("date");
                String statement = loanResult.getString("statement");

                Object[] rowData = {accountNo, username, fullName, accountAmount, loanAmount, date, statement, "Approve"};
                tableModel.addRow(rowData);
            }

            // Close the result sets and statements
            accountResult.close();
            loanResult.close();
            accountStatement.close();
            loanStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading loan data.");
        }
    }

    private void showLoanApplicationForm() {
        // Show the loan application form without a specific user (you can adjust this)
    	LoanApplicationForm loanForm = new LoanApplicationForm(getName());
        JFrame frame = new JFrame("Loan Application Form");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(loanForm);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("eLoan System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new eLoan());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

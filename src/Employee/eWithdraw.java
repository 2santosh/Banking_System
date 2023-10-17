package Employee;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import Data.DatabaseManager;

public class eWithdraw extends javax.swing.JInternalFrame {
    private JTextField account_noField;
    private JTextField fullNameField;
    private JTextField phoneNumberField;
    private JTextField chequeNumberField;
    private JTextField withdrawalFullNameField;
    private JTextField amountField; // Added field
    private JButton submitButton;
    private JButton accountDetailsButton;

    public eWithdraw() {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        this.setBorder(null);

        // Create and initialize components
        account_noField = new JTextField(20);
        fullNameField = new JTextField(20);
        phoneNumberField = new JTextField(20);
        chequeNumberField = new JTextField(20);
        withdrawalFullNameField = new JTextField(20);
        amountField = new JTextField(20); // Added field
        submitButton = new JButton("Submit");
        accountDetailsButton = new JButton("Account Details");

        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitActionPerformed(e);
            }
        });

        // Add action listener to the account details button
        accountDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountDetailsActionPerformed(e);
            }
        });

        // Create labels for account holder details
        JLabel accountNumberLabel = new JLabel("Account Number:");
        JLabel fullNameLabel = new JLabel("Full Name:");

        // Create labels for withdrawal details
        JLabel title = new JLabel("Withdrawal Details");
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JLabel chequeNumberLabel = new JLabel("Cheque Number:");
        JLabel withdrawalFullNameLabel = new JLabel("Withdrawal Full Name:");
        JLabel amountLabel = new JLabel("Amount"); // Added label

        // Create and set up a panel with GridBagLayout for the content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add account holder details components
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(accountNumberLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(account_noField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(fullNameLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(fullNameField, gbc);

        // Add the account details button
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        contentPanel.add(accountDetailsButton, gbc);

        // Create and set up a panel to hold the content panel with a titled border
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Account Holder Details"));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add the main panel to the content pane of the internal frame
        getContentPane().add(mainPanel, BorderLayout.NORTH);

        // Create and set up a panel with GridBagLayout for withdrawal details
        JPanel withdrawalDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcWithdrawal = new GridBagConstraints();
        gbcWithdrawal.fill = GridBagConstraints.HORIZONTAL;
        gbcWithdrawal.insets = new Insets(5, 5, 5, 5);

        // Add title for withdrawal details
        gbcWithdrawal.gridx = 0;
        gbcWithdrawal.gridy = 0;
        gbcWithdrawal.gridwidth = 2;
        withdrawalDetailsPanel.add(title, gbcWithdrawal);

        // Add withdrawal details components
        gbcWithdrawal.gridx = 0;
        gbcWithdrawal.gridy = 1;
        gbcWithdrawal.gridwidth = 1;
        withdrawalDetailsPanel.add(phoneNumberLabel, gbcWithdrawal);
        gbcWithdrawal.gridx = 1;
        withdrawalDetailsPanel.add(phoneNumberField, gbcWithdrawal);

        gbcWithdrawal.gridx = 0;
        gbcWithdrawal.gridy = 2;
        withdrawalDetailsPanel.add(chequeNumberLabel, gbcWithdrawal);
        gbcWithdrawal.gridx = 1;
        withdrawalDetailsPanel.add(chequeNumberField, gbcWithdrawal);

        gbcWithdrawal.gridx = 0;
        gbcWithdrawal.gridy = 3;
        withdrawalDetailsPanel.add(withdrawalFullNameLabel, gbcWithdrawal);
        gbcWithdrawal.gridx = 1;
        withdrawalDetailsPanel.add(withdrawalFullNameField, gbcWithdrawal);

        // Add the new amount components
        gbcWithdrawal.gridx = 0;
        gbcWithdrawal.gridy = 4;
        withdrawalDetailsPanel.add(amountLabel, gbcWithdrawal);
        gbcWithdrawal.gridx = 1;
        withdrawalDetailsPanel.add(amountField, gbcWithdrawal);

        // Create and set up a panel to hold withdrawal details with a titled border
        JPanel withdrawalDetailsMainPanel = new JPanel(new BorderLayout());
        withdrawalDetailsMainPanel.setBorder(BorderFactory.createTitledBorder("Withdrawal Details"));

        // Add the withdrawal details panel to the content pane of the internal frame
        withdrawalDetailsMainPanel.add(withdrawalDetailsPanel, BorderLayout.CENTER);
        getContentPane().add(withdrawalDetailsMainPanel, BorderLayout.CENTER);

        // Add the submit button
        getContentPane().add(submitButton, BorderLayout.SOUTH);

        pack();
    }

        private void submitActionPerformed(ActionEvent evt) {
        	double account_no = Double.parseDouble(account_noField.getText());
            String fullName = fullNameField.getText();
            String phoneNumber = phoneNumberField.getText();
            String chequeNumber = chequeNumberField.getText();
            String withdrawalFullName = withdrawalFullNameField.getText();
            double amount = Double.parseDouble(amountField.getText());

            // Get the current date and time
            java.util.Date utilDate = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());

            try (Connection connection = DatabaseManager.getConnection()) {
                // Check if the withdrawal amount is available in the account
                String checkBalanceQuery = "SELECT * FROM account WHERE account_no = ?";
                PreparedStatement checkBalanceStatement = connection.prepareStatement(checkBalanceQuery);
                checkBalanceStatement.setDouble(1, account_no);
                ResultSet balanceResult = checkBalanceStatement.executeQuery();

                if (balanceResult.next()) {
                    double currentBalance = balanceResult.getDouble("amount");
                    String username = balanceResult.getString("username");
                    double account_no1 = balanceResult.getDouble("account_no");
                    // Construct the table name dynamically
                    String withdrawalTableName = username + "_withdraw";

                    if (currentBalance >= amount) {
                        // Deduct the amount from the account
                        String updateBalanceQuery = "UPDATE account SET amount = ? WHERE account_no = ?";
                        PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceQuery);
                        updateBalanceStatement.setDouble(1, currentBalance - amount);
                        updateBalanceStatement.setDouble(2, account_no);
                        updateBalanceStatement.executeUpdate();

                        // Insert withdrawal details into the dynamically generated table
                        String insertWithdrawalQuery = "INSERT INTO " + withdrawalTableName + " (date_time, cheque_no, amount, withdraw_name, withdraw_phone) " +
                                "VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement insertWithdrawalStatement = connection.prepareStatement(insertWithdrawalQuery);
                        insertWithdrawalStatement.setTimestamp(1, timestamp);
                        insertWithdrawalStatement.setString(2, chequeNumber);
                        insertWithdrawalStatement.setDouble(3, amount);
                        insertWithdrawalStatement.setString(4, withdrawalFullName);
                        insertWithdrawalStatement.setString(5, phoneNumber);

                        int rowsAffected = insertWithdrawalStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Withdrawal details saved successfully.");
                            clearFields();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to save withdrawal details.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient balance in the account.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Account not found.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }


    // Helper method to clear input fields
    private void clearFields() {
    	account_noField.setText("");
        fullNameField.setText("");
        phoneNumberField.setText("");
        chequeNumberField.setText("");
        withdrawalFullNameField.setText("");
        amountField.setText("");
    }

    private void accountDetailsActionPerformed(ActionEvent evt) {
        String accountNumber = account_noField.getText();

        try (Connection connection = DatabaseManager.getConnection()) {
            // Query the database to fetch account details
            String accountDetailsQuery = "SELECT * FROM account WHERE account_no = ?";
            PreparedStatement accountDetailsStatement = connection.prepareStatement(accountDetailsQuery);
            accountDetailsStatement.setString(1, accountNumber);
            ResultSet accountDetailsResult = accountDetailsStatement.executeQuery();

            if (accountDetailsResult.next()) {
                String username = accountDetailsResult.getString("username");
                double accountBalance = accountDetailsResult.getDouble("amount");

                // Construct the table name dynamically
             

                // Query data from the dynamically generated table
                String querySQL = "SELECT * FROM account";
                PreparedStatement queryStatement = connection.prepareStatement(querySQL);
                ResultSet withdrawalResultSet = queryStatement.executeQuery();

                // Create a StringBuilder to store withdrawal details
                StringBuilder withdrawalDetails = new StringBuilder();

                // Display account details and withdrawal details in a message dialog
                String accountDetailsMessage = "Account Holder Name: " + username + "\n" +
                                                "Account Balance: $" + accountBalance + "\n\n" +
                                                "Withdrawal Details:\n" + withdrawalDetails.toString();

                JOptionPane.showMessageDialog(
                    this,
                    accountDetailsMessage,
                    "Account Details",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

}

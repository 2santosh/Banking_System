package Employee;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import Data.DatabaseManager;

public class eDeposit extends javax.swing.JInternalFrame {
    private JTextField account_noField;
    private JTextField fullNameField;
    private JTextField phoneNumberField;
    private JTextField chequeNumberField;
    private JTextField depositFullNameField;
    private JTextField amountField;
    private JButton submitButton;
    private JButton accountDetailsButton;

    public eDeposit() {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        this.setBorder(null);

        // Create and initialize components
        account_noField = new JTextField(20);
        fullNameField = new JTextField(20);
        phoneNumberField = new JTextField(20);
        chequeNumberField = new JTextField(20);
        depositFullNameField = new JTextField(20);
        amountField = new JTextField(20);
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

        // Create labels for deposit details
        JLabel title = new JLabel("Deposit Details");
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JLabel chequeNumberLabel = new JLabel("Cheque Number:");
        JLabel depositFullNameLabel = new JLabel("Deposit Full Name:");
        JLabel amountLabel = new JLabel("Amount");

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

        // Create and set up a panel with GridBagLayout for deposit details
        JPanel depositDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcDeposit = new GridBagConstraints();
        gbcDeposit.fill = GridBagConstraints.HORIZONTAL;
        gbcDeposit.insets = new Insets(5, 5, 5, 5);

        // Add title for deposit details
        gbcDeposit.gridx = 0;
        gbcDeposit.gridy = 0;
        gbcDeposit.gridwidth = 2;
        depositDetailsPanel.add(title, gbcDeposit);

        // Add deposit details components
        gbcDeposit.gridx = 0;
        gbcDeposit.gridy = 1;
        gbcDeposit.gridwidth = 1;
        depositDetailsPanel.add(phoneNumberLabel, gbcDeposit);
        gbcDeposit.gridx = 1;
        depositDetailsPanel.add(phoneNumberField, gbcDeposit);

        gbcDeposit.gridx = 0;
        gbcDeposit.gridy = 2;
        depositDetailsPanel.add(chequeNumberLabel, gbcDeposit);
        gbcDeposit.gridx = 1;
        depositDetailsPanel.add(chequeNumberField, gbcDeposit);

        gbcDeposit.gridx = 0;
        gbcDeposit.gridy = 3;
        depositDetailsPanel.add(depositFullNameLabel, gbcDeposit);
        gbcDeposit.gridx = 1;
        depositDetailsPanel.add(depositFullNameField, gbcDeposit);

        // Add the new amount components
        gbcDeposit.gridx = 0;
        gbcDeposit.gridy = 4;
        depositDetailsPanel.add(amountLabel, gbcDeposit);
        gbcDeposit.gridx = 1;
        depositDetailsPanel.add(amountField, gbcDeposit);

        // Create and set up a panel to hold deposit details with a titled border
        JPanel depositDetailsMainPanel = new JPanel(new BorderLayout());
        depositDetailsMainPanel.setBorder(BorderFactory.createTitledBorder("Deposit Details"));

        // Add the deposit details panel to the content pane of the internal frame
        depositDetailsMainPanel.add(depositDetailsPanel, BorderLayout.CENTER);
        getContentPane().add(depositDetailsMainPanel, BorderLayout.CENTER);

        // Add the submit button
        getContentPane().add(submitButton, BorderLayout.SOUTH);

        pack();
    }

    private void submitActionPerformed(ActionEvent evt) {
        double account_no = Double.parseDouble(account_noField.getText());
        String fullName = fullNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String chequeNumber = chequeNumberField.getText();
        String depositFullName = depositFullNameField.getText();
        double amount = Double.parseDouble(amountField.getText());

        // Get the current date and time
        java.util.Date utilDate = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());

        try (Connection connection = DatabaseManager.getConnection()) {
            // Fetch the username from the account table using the account number
            String fetchUsernameQuery = "SELECT username FROM account WHERE account_no = ?";
            PreparedStatement fetchUsernameStatement = connection.prepareStatement(fetchUsernameQuery);
            fetchUsernameStatement.setDouble(1, account_no);
            ResultSet usernameResult = fetchUsernameStatement.executeQuery();

            if (usernameResult.next()) {
                String username = usernameResult.getString("username");

                // Update the account balance with the deposit amount
                String updateBalanceQuery = "UPDATE account SET amount = amount + ? WHERE account_no = ?";
                PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceQuery);
                updateBalanceStatement.setDouble(1, amount);
                updateBalanceStatement.setDouble(2, account_no);
                int rowsAffected = updateBalanceStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Construct the deposit table name dynamically
                    String depositTableName = username + "_deposit";

                    // Insert deposit details into the dynamically generated table
                    String insertDepositQuery = "INSERT INTO " + depositTableName + " (date_time, cheque_no, amount, deposit_name, deposit_phone) " +
                            "VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement insertDepositStatement = connection.prepareStatement(insertDepositQuery);
                    insertDepositStatement.setTimestamp(1, timestamp);
                    insertDepositStatement.setString(2, chequeNumber);
                    insertDepositStatement.setDouble(3, amount);
                    insertDepositStatement.setString(4, depositFullName);
                    insertDepositStatement.setString(5, phoneNumber);

                    int depositRowsAffected = insertDepositStatement.executeUpdate();

                    if (depositRowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Deposit details saved successfully.");
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save deposit details.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Account not found.");
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
        depositFullNameField.setText("");
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

                // Query data from the deposit details table (you need to create this table)
                String depositDetailsQuery = "SELECT * FROM account WHERE account_no = ?";
                PreparedStatement depositDetailsStatement = connection.prepareStatement(depositDetailsQuery);
                depositDetailsStatement.setString(1, accountNumber);
                StringBuilder depositDetails = new StringBuilder();
              
                String accountDetailsMessage = "Account Holder Name: " + username + "\n" +
                        "Account Balance: $" + accountBalance + "\n\n" +
                        "Deposit Details:\n" + depositDetails.toString();

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

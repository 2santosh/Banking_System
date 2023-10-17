package Customer;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class Transfer extends javax.swing.JInternalFrame {
    private JTextField senderAccountNumberField;
    private JTextField senderFullNameField;
    private JTextField receiverAccountNumberField;
    private JTextField receiverFullNameField;
    private JTextField amountField;
    private JButton submitButton;
    private String username;
    private Connection con1;

    public Transfer(String senderFullName) {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        this.setBorder(null);
        this.username = senderFullName;
        
        // Create and initialize components for "Sender Details" section
        senderAccountNumberField = new JTextField(20);
        senderAccountNumberField.setEditable(false);

        senderFullNameField = new JTextField(20);
        senderFullNameField.setText(senderFullName);
        senderFullNameField.setEditable(false);
        
        // Create and initialize components for "Receiver Details" section
        receiverAccountNumberField = new JTextField(20);
        receiverFullNameField = new JTextField(20);
        
        // Create and initialize components for "Amount" section
        amountField = new JTextField(20);

        // Create and initialize the submit button
        submitButton = new JButton("Transfer");
        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferActionPerformed(e);
            }
        });

        // Create labels for receiver details
        JLabel receiverAccountNumberLabel = new JLabel("Receiver Account Number:");
        JLabel receiverFullNameLabel = new JLabel("Receiver Full Name:");

        // Create labels for amount section
        JLabel amountLabel = new JLabel("Amount to Transfer:");

        // Create and set up a panel with GridBagLayout for the content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add components for "Sender Details" section
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Sender Details"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(senderAccountNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(senderFullNameField, gbc);
        
        // Add components for "Amount" section
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(amountLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(amountField, gbc);
        
        // Create and set up a panel for "Receiver Details" section
        JPanel receiverDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcReceiver = new GridBagConstraints();
        gbcReceiver.fill = GridBagConstraints.HORIZONTAL;
        gbcReceiver.insets = new Insets(5, 5, 5, 5);

        // Add components for "Receiver Details" section
        gbcReceiver.gridx = 0;
        gbcReceiver.gridy = 0;
        receiverDetailsPanel.add(receiverAccountNumberLabel, gbcReceiver);
        gbcReceiver.gridx = 1;
        receiverDetailsPanel.add(receiverAccountNumberField, gbcReceiver);

        gbcReceiver.gridx = 0;
        gbcReceiver.gridy = 1;
        receiverDetailsPanel.add(receiverFullNameLabel, gbcReceiver);
        gbcReceiver.gridx = 1;
        receiverDetailsPanel.add(receiverFullNameField, gbcReceiver);

        // Create and set up a panel to hold both sections
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Transfer Details"));
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        mainPanel.add(receiverDetailsPanel, BorderLayout.CENTER);

        // Add the main panel to the content pane of the internal frame
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Add the submit button
        getContentPane().add(submitButton, BorderLayout.SOUTH);
        try {
            String dbURL = "jdbc:mysql://localhost:3306/bank";
            String dbUser = "root";
            String dbPassword = "root";

            con1 = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            // Log a message to confirm successful connection
            System.out.println("Database connection established successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database");
        }

        pack();
    }

    private void transferActionPerformed(ActionEvent evt) {
        String senderAccountNumber = senderAccountNumberField.getText();
        String senderFullName = senderFullNameField.getText();
        String receiverAccountNumber = receiverAccountNumberField.getText();
        String receiverFullName = receiverFullNameField.getText();
        double transferAmount = Double.parseDouble(amountField.getText());

        try {
            con1.setAutoCommit(false); // Disable auto-commit

            // Check if the sender's account has enough balance
            String senderBalanceQuery = "SELECT amount FROM account WHERE account_no = ?";
            PreparedStatement senderBalanceStatement = con1.prepareStatement(senderBalanceQuery);
            senderBalanceStatement.setString(1, senderAccountNumber);
            ResultSet senderBalanceResult = senderBalanceStatement.executeQuery();

            if (senderBalanceResult.next()) {
                double senderBalance = senderBalanceResult.getDouble("amount");

                if (senderBalance >= transferAmount) {
                    // Deduct the transfer amount from the sender's account
                    double newSenderBalance = senderBalance - transferAmount;
                    String updateSenderQuery = "UPDATE account SET amount = ? WHERE account_no = ?";
                    PreparedStatement updateSenderStatement = con1.prepareStatement(updateSenderQuery);
                    updateSenderStatement.setDouble(1, newSenderBalance);
                    updateSenderStatement.setString(2, senderAccountNumber);
                    updateSenderStatement.executeUpdate();

                    // Update the receiver's account
                    String receiverBalanceQuery = "SELECT amount FROM account WHERE account_no = ?";
                    PreparedStatement receiverBalanceStatement = con1.prepareStatement(receiverBalanceQuery);
                    receiverBalanceStatement.setString(1, receiverAccountNumber);
                    ResultSet receiverBalanceResult = receiverBalanceStatement.executeQuery();

                    if (receiverBalanceResult.next()) {
                        double receiverBalance = receiverBalanceResult.getDouble("amount");
                        double newReceiverBalance = receiverBalance + transferAmount;

                        String updateReceiverQuery = "UPDATE account SET amount = ? WHERE account_no = ?";
                        PreparedStatement updateReceiverStatement = con1.prepareStatement(updateReceiverQuery);
                        updateReceiverStatement.setDouble(1, newReceiverBalance);
                        updateReceiverStatement.setString(2, receiverAccountNumber);
                        updateReceiverStatement.executeUpdate();

                        // Record the transaction for both sender and receiver
                        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                        String senderTransfer = senderFullName + "_transfer";
                        String receiverTransfer = receiverFullName + "_transfer";

                        String senderTransactionQuery = "INSERT INTO " + senderTransfer
                                + "(date_time, sender_account_no, sender_full_name, receiver_account_no, receiver_full_name, amount) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement senderTransactionStatement = con1.prepareStatement(senderTransactionQuery);
                        senderTransactionStatement.setTimestamp(1, currentTimestamp);
                        senderTransactionStatement.setString(2, senderAccountNumber);
                        senderTransactionStatement.setString(3, senderFullName);
                        senderTransactionStatement.setString(4, receiverAccountNumber);
                        senderTransactionStatement.setString(5, receiverFullName);
                        senderTransactionStatement.setDouble(6, transferAmount);
                        senderTransactionStatement.executeUpdate();

                        String receiverTransactionQuery = "INSERT INTO " + receiverTransfer
                                + "(date_time, receiver_account_no, receiver_full_name, sender_account_no, sender_full_name, amount) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement receiverTransactionStatement = con1.prepareStatement(receiverTransactionQuery);
                        receiverTransactionStatement.setTimestamp(1, currentTimestamp);
                        receiverTransactionStatement.setString(2, receiverAccountNumber);
                        receiverTransactionStatement.setString(3, receiverFullName);
                        receiverTransactionStatement.setString(4, senderAccountNumber);
                        receiverTransactionStatement.setString(5, senderFullName);
                        receiverTransactionStatement.setDouble(6, transferAmount);
                        receiverTransactionStatement.executeUpdate();

                        con1.commit(); // Commit the transaction
                        JOptionPane.showMessageDialog(this, "Transfer successful!");
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Receiver Account Not Found");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance in Sender Account");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sender Account Not Found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during the transfer process: " + e.getMessage());
            try {
                con1.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            try {
                con1.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException setAutoCommitException) {
                setAutoCommitException.printStackTrace();
            }
        }
    }

    // Helper method to clear input fields
    private void clearFields() {
        senderAccountNumberField.setText("");
        receiverAccountNumberField.setText("");
        receiverFullNameField.setText("");
        amountField.setText("");
    }
}

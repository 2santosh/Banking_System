package Employee;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class eTransfer extends javax.swing.JInternalFrame {
    private JTextField senderAccountNumberField;
    private JTextField senderFullNameField;
    private JTextField senderChequeNumberField;
    private JTextField receiverAccountNumberField;
    private JTextField receiverFullNameField;
    private JTextField amountField;
    private JButton submitButton;
    private JButton senderAccountDetailsButton;

    private Connection con1;

    public eTransfer() {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        this.setBorder(null);

        // Create and initialize components
        senderAccountNumberField = new JTextField(20);
        senderFullNameField = new JTextField(20);
        senderChequeNumberField = new JTextField(20);
        receiverAccountNumberField = new JTextField(20);
        receiverFullNameField = new JTextField(20);
        amountField = new JTextField(20);
        submitButton = new JButton("Transfer");

        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferActionPerformed(e);
            }
        });

        // Create a button for sender account details
        senderAccountDetailsButton = new JButton("Sender Account Details");

        // Add action listener to the sender account details button
        senderAccountDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSenderAccountDetails();
            }
        });

        // Create labels for sender details
        JLabel senderAccountNumberLabel = new JLabel("Sender Account Number:");
        JLabel senderFullNameLabel = new JLabel("Sender Full Name:");
        JLabel senderChequeNumberLabel = new JLabel("Sender Cheque Number:");

        // Create labels for receiver details
        JLabel receiverAccountNumberLabel = new JLabel("Receiver Account Number:");
        JLabel receiverFullNameLabel = new JLabel("Receiver Full Name:");
        JLabel amountLabel = new JLabel("Amount");

        // Create and set up a panel with GridBagLayout for sender details
        JPanel senderPanel = new JPanel(new GridBagLayout());
        senderPanel.setBorder(BorderFactory.createTitledBorder("Sender Details"));
        GridBagConstraints senderGBC = new GridBagConstraints();
        senderGBC.fill = GridBagConstraints.HORIZONTAL;
        senderGBC.insets = new Insets(5, 5, 5, 5);

        // Add sender components
        senderGBC.gridx = 0;
        senderGBC.gridy = 0;
        senderPanel.add(senderAccountNumberLabel, senderGBC);
        senderGBC.gridx = 1;
        senderPanel.add(senderAccountNumberField, senderGBC);

        senderGBC.gridx = 0;
        senderGBC.gridy = 1;
        senderPanel.add(senderFullNameLabel, senderGBC);
        senderGBC.gridx = 1;
        senderPanel.add(senderFullNameField, senderGBC);

        senderGBC.gridx = 0;
        senderGBC.gridy = 2;
        senderPanel.add(senderChequeNumberLabel, senderGBC);
        senderGBC.gridx = 1;
        senderPanel.add(senderChequeNumberField, senderGBC);

        // Add the "Sender Account Details" button within the sender panel
        senderGBC.gridx = 2;
        senderGBC.gridy = 2;
        senderGBC.gridwidth = 1; // Span two columns
        senderPanel.add(senderAccountDetailsButton, senderGBC);

        // Create and set up a panel with GridBagLayout for receiver details
        JPanel receiverPanel = new JPanel(new GridBagLayout());
        receiverPanel.setBorder(BorderFactory.createTitledBorder("Receiver Details"));
        GridBagConstraints receiverGBC = new GridBagConstraints();
        receiverGBC.fill = GridBagConstraints.HORIZONTAL;
        receiverGBC.insets = new Insets(5, 5, 5, 5);

        // Add receiver components
        receiverGBC.gridx = 0;
        receiverGBC.gridy = 0;
        receiverPanel.add(receiverAccountNumberLabel, receiverGBC);
        receiverGBC.gridx = 1;
        receiverPanel.add(receiverAccountNumberField, receiverGBC);

        receiverGBC.gridx = 0;
        receiverGBC.gridy = 1;
        receiverPanel.add(receiverFullNameLabel, receiverGBC);
        receiverGBC.gridx = 1;
        receiverPanel.add(receiverFullNameField, receiverGBC);

        receiverGBC.gridx = 0;
        receiverGBC.gridy = 2;
        receiverPanel.add(amountLabel, receiverGBC);
        receiverGBC.gridx = 1;
        receiverPanel.add(amountField, receiverGBC);

        // Create and set up a panel to hold both sender and receiver sections
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(senderPanel, BorderLayout.NORTH);
        mainPanel.add(receiverPanel, BorderLayout.CENTER);

        // Add the main panel to the content pane of the internal frame
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Add the submit button
        getContentPane().add(submitButton, BorderLayout.SOUTH);

        // Initialize the database connection here using JDBC
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

            // Find the username of the sender using the sender's account number
            String senderUsernameQuery = "SELECT username FROM account WHERE account_no = ?";
            PreparedStatement senderUsernameStatement = con1.prepareStatement(senderUsernameQuery);
            senderUsernameStatement.setString(1, senderAccountNumber);
            ResultSet senderUsernameResult = senderUsernameStatement.executeQuery();

            if (senderUsernameResult.next()) {
                String senderUsername = senderUsernameResult.getString("username");

                // Find the username of the receiver using the receiver's account number
                String receiverUsernameQuery = "SELECT username FROM account WHERE account_no = ?";
                PreparedStatement receiverUsernameStatement = con1.prepareStatement(receiverUsernameQuery);
                receiverUsernameStatement.setString(1, receiverAccountNumber);
                ResultSet receiverUsernameResult = receiverUsernameStatement.executeQuery();

                if (receiverUsernameResult.next()) {
                    String receiverUsername = receiverUsernameResult.getString("username");

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
                                String senderTransfer = senderUsername + "_transfer";
                                String receiverTransfer = receiverUsername + "_transfer";

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
                } else {
                    JOptionPane.showMessageDialog(this, "Receiver Account Not Found");
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



    private void showSenderAccountDetails() {
        String senderAccountNumber = senderAccountNumberField.getText();
        try {
            // Prepare a SQL query to retrieve sender account details
            String query = "SELECT * FROM account WHERE account_no = ?";
            PreparedStatement preparedStatement = con1.prepareStatement(query);
            preparedStatement.setString(1, senderAccountNumber);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve sender account details from the result set
                double balance = resultSet.getDouble("amount");

                // Display sender account balance in a dialog
                String accountDetails = "Balance: $" + balance;
                JOptionPane.showMessageDialog(this, "Sender Account Details:\n" + accountDetails);
            } else {
                // If no matching account is found
                JOptionPane.showMessageDialog(this, "Sender Account Not Found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving sender account details: " + e.getMessage());
        }
    }

    private void clearFields() {
        senderAccountNumberField.setText("");
        senderFullNameField.setText("");
        senderChequeNumberField.setText("");
        receiverAccountNumberField.setText("");
        receiverFullNameField.setText("");
        amountField.setText("");
    }
}

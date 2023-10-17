package Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import Data.DatabaseManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class eStatement extends JPanel {
    private JTable bankStatementTable;
    private DefaultTableModel bankStatementModel;
    private JTextField searchField;
    private JButton searchButton;
    private TableRowSorter<DefaultTableModel> sorter;
    private Connection connection;

    public eStatement() {
        setLayout(new BorderLayout());

        String[] bankStatementColumnNames = {"Account No", "Username", "Full Name", "Account Balance"};
        bankStatementModel = new DefaultTableModel(bankStatementColumnNames, 0);
        bankStatementTable = new JTable(bankStatementModel);
        JScrollPane bankStatementScrollPane = new JScrollPane(bankStatementTable);
        bankStatementScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(bankStatementScrollPane, BorderLayout.CENTER);

        sorter = new TableRowSorter<>(bankStatementModel);
        bankStatementTable.setRowSorter(sorter);

        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.X_AXIS));
        bottomButtonPanel.add(Box.createHorizontalGlue());
        bottomButtonPanel.add(searchPanel);
        bottomButtonPanel.add(Box.createHorizontalStrut(10));

        add(searchPanel, BorderLayout.NORTH);
        add(bottomButtonPanel, BorderLayout.SOUTH);

        initializeDatabaseConnection();
        populateAccountData();
        addRowSelectionListener();
    }

    private void initializeDatabaseConnection() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/bank";
            String dbUser = "root";
            String dbPassword = "root";

            connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateAccountData() {
        // Query the database to populate the bankStatementModel with account data
        // Replace 'your_query' with the actual SQL query to fetch account data.
        String accountQuery = "SELECT account_no, username, fullname, amount FROM account";

        try (PreparedStatement statement = connection.prepareStatement(accountQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String accountNo = resultSet.getString("account_no");
                String username = resultSet.getString("username");
                String fullName = resultSet.getString("fullname");
                double accountBalance = resultSet.getDouble("amount");

                bankStatementModel.addRow(new Object[]{accountNo, username, fullName, accountBalance});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load account data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRowSelectionListener() {
        bankStatementTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = bankStatementTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        showCustomerDetails(selectedRow);
                    }
                }
            }
        });
    }
    private void showCustomerDetails(int selectedRow) {
        String username = bankStatementTable.getValueAt(selectedRow, 1).toString(); // Get the username from the selected row

        String[] transactionTypes = {"deposit", "withdraw", "statement", "loan", "transfer", "report"};

        for (String transactionType : transactionTypes) {
            String tableName = username + "_" + transactionType; // Construct the table name dynamically
            try (Connection connection = DatabaseManager.getConnection()) {
                String query = "SELECT * FROM " + tableName;
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Create a dialog to display the transaction details
                    JDialog detailsDialog = new JDialog();
                    detailsDialog.setTitle(transactionType + " Details");
                    detailsDialog.setLayout(new GridLayout(0, 2));

                    // Add common labels and values
                    int id = resultSet.getInt("id");
                    Timestamp dateTime = resultSet.getTimestamp("date_time");
                    int accountNo = resultSet.getInt("account_no");

                    addDetailLabelAndValue(detailsDialog, "ID:", String.valueOf(id));
                    addDetailLabelAndValue(detailsDialog, "Date and Time:", dateTime.toString());
                    addDetailLabelAndValue(detailsDialog, "Account No:", String.valueOf(accountNo));

                    // Add transaction-specific details
                    if (transactionType.equals("deposit")) {
                        String chequeNo = resultSet.getString("cheque_no");
                        double amount = resultSet.getDouble("amount");
                        String depositName = resultSet.getString("deposit_name");
                        String depositPhone = resultSet.getString("deposit_phone");

                        addDetailLabelAndValue(detailsDialog, "Cheque No:", chequeNo);
                        addDetailLabelAndValue(detailsDialog, "Amount:", String.valueOf(amount));
                        addDetailLabelAndValue(detailsDialog, "Deposit Name:", depositName);
                        addDetailLabelAndValue(detailsDialog, "Deposit Phone:", depositPhone);
                    } else if (transactionType.equals("withdraw")) {
                        String chequeNo = resultSet.getString("cheque_no");
                        double amount = resultSet.getDouble("amount");
                        String withdrawName = resultSet.getString("withdraw_name");
                        String withdrawPhone = resultSet.getString("withdraw_phone");

                        addDetailLabelAndValue(detailsDialog, "Cheque No:", chequeNo);
                        addDetailLabelAndValue(detailsDialog, "Amount:", String.valueOf(amount));
                        addDetailLabelAndValue(detailsDialog, "Withdraw Name:", withdrawName);
                        addDetailLabelAndValue(detailsDialog, "Withdraw Phone:", withdrawPhone);
                    } else if (transactionType.equals("transfer")) {
                        String receiverAccountNo = resultSet.getString("receiver_account_no");
                        String receiverFullName = resultSet.getString("receiver_full_name");
                        double amount = resultSet.getDouble("amount");

                        addDetailLabelAndValue(detailsDialog, "Receiver Account No:", receiverAccountNo);
                        addDetailLabelAndValue(detailsDialog, "Receiver Full Name:", receiverFullName);
                        addDetailLabelAndValue(detailsDialog, "Amount:", String.valueOf(amount));
                    } else if (transactionType.equals("loan")) {
                        double loanAmount = resultSet.getDouble("loan_amount");
                        String statement = resultSet.getString("statement");
                        boolean approve = resultSet.getBoolean("approve");

                        addDetailLabelAndValue(detailsDialog, "Loan Amount:", String.valueOf(loanAmount));
                        addDetailLabelAndValue(detailsDialog, "Statement:", statement);
                        addDetailLabelAndValue(detailsDialog, "Approved:", String.valueOf(approve));
                    }

                    detailsDialog.pack();
                    detailsDialog.setLocationRelativeTo(this);
                    detailsDialog.setVisible(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while retrieving transaction details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void addDetailLabelAndValue(Container container, String label, String value) {
        JLabel detailLabel = new JLabel(label);
        JLabel detailValue = new JLabel(value);
        container.add(detailLabel);
        container.add(detailValue);
    }

    private void performSearch() {
        String usernameQuery = searchField.getText();
        if (usernameQuery.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(usernameQuery, 1));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("eStatement");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new eStatement());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

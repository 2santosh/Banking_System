package Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import Data.DatabaseManager;

public class Statement extends JPanel {
    private JTable bankStatementTable;
    private DefaultTableModel bankStatementModel;
    private JTextField searchField;
    private JButton searchButton;

    private Connection connection;
    private String username;

    public Statement(String username) {
        this.username = username; // Store the received username
        setLayout(new BorderLayout());

        String[] bankStatementColumnNames = {"Account No", "Username", "Full Name", "Deposit Amount", "Withdraw Amount", "Transfer Amount"};
        bankStatementModel = new DefaultTableModel(bankStatementColumnNames, 0);
        bankStatementTable = new JTable(bankStatementModel);
        JScrollPane bankStatementScrollPane = new JScrollPane(bankStatementTable);
        bankStatementScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(bankStatementScrollPane, BorderLayout.CENTER);

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
        loadAccountDataFromDatabase();
    }

    private void initializeDatabaseConnection() {
        try {
            connection = DatabaseManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAccountDataFromDatabase() {
        if (connection != null) {
            String depositTable = username + "_deposit";
            String withdrawTable = username + "_withdraw";
            String transferTable = username + "_transfer";
            try {
            	String query = "SELECT a.account_no, a.username, a.fullname, d.amount AS deposit_amount, " +
                        "w.amount AS withdraw_amount, t.amount AS transfer_amount " +
                        "FROM account a " +
                        "LEFT JOIN " + depositTable + " d ON a.account_no = d.account_no " +
                        "LEFT JOIN " + withdrawTable + " w ON a.account_no = w.account_no " +
                        "LEFT JOIN " + transferTable + " t ON a.account_no = t.account_no";


                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                bankStatementModel.setRowCount(0);

                while (resultSet.next()) {
                    String accountNo = resultSet.getString("account_no");
                    String username = resultSet.getString("username");
                    String fullName = resultSet.getString("fullname");
                    double depositAmount = resultSet.getDouble("deposit_amount");
                    double withdrawAmount = resultSet.getDouble("withdraw_amount");
                    double transferAmount = resultSet.getDouble("transfer_amount");

                    Object[] rowData = {accountNo, username, fullName, depositAmount, withdrawAmount, transferAmount};
                    bankStatementModel.addRow(rowData);
                }

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load account data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Database connection is not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String usernameQuery = searchField.getText();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) bankStatementTable.getRowSorter();

        // Create a RowFilter to match the username
        RowFilter<DefaultTableModel, Object> rowFilter = new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                // Replace 1 with the column index where the username is located
                String username = entry.getStringValue(1); // Assuming the username is in column 1
                return username.contains(usernameQuery);
            }
        };

        // Set the RowFilter to the TableRowSorter
        sorter.setRowFilter(rowFilter);
    }
}

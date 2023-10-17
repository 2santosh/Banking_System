package Manager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

import Data.DatabaseManager;

public class CustomerDetails extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton editButton;
    private JTextField searchField;
    private JButton searchButton;

    public CustomerDetails() {
        // Set the layout manager for this panel
        setLayout(new BorderLayout());

        String[] columnNames = {"id", "Username", "Full Name", "Phone", "Email", "Bank Account", "Total Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // Create the table and set the table model
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create "View" button
        viewButton = new JButton("View");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCustomerDetails();
            }
        });

        // Create "Edit" button
        editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCustomerDetails();
            }
        });

        // Create search field
        searchField = new JTextField(20);

        // Create search button
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(new JLabel("Search by Username:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load customer data from the database
        loadCustomerDataFromDatabase();
    }

    private void loadCustomerDataFromDatabase() {
        // Create an instance of DatabaseManager to handle database operations
        DatabaseManager dbManager = new DatabaseManager();

        try {
            // Connect to the database
            Connection connection = dbManager.getConnection();

            // Execute a SQL query to retrieve customer data by joining the 'customer' and 'account' tables
            String query = "SELECT c.id, c.username, c.name, c.phone, c.email, a.account_no, a.amount " +
                    "FROM customer c " +
                    "JOIN account a ON c.username = a.username";


            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Clear the existing table data
            tableModel.setRowCount(0);

            // Populate the table model with data from the database
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String fullName = resultSet.getString("name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String bankAccount = resultSet.getString("account_no");
                double totalAmount = resultSet.getDouble("amount");

                Object[] rowData = {id, username, fullName, phone, email, bankAccount, totalAmount};
                tableModel.addRow(rowData);
            }

            // Close database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load customer data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewCustomerDetails() {
        // Display all customer details in a dialog (customize this as needed)
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            Vector<Object> rowData = (Vector<Object>) tableModel.getDataVector().elementAt(selectedRow);

            StringBuilder details = new StringBuilder();
            details.append("SN: ").append(rowData.get(0)).append("\n");
            details.append("Username: ").append(rowData.get(1)).append("\n");
            details.append("Full Name: ").append(rowData.get(2)).append("\n");
            details.append("Phone: ").append(rowData.get(3)).append("\n");
            details.append("Email: ").append(rowData.get(4)).append("\n");
            details.append("Bank Account: ").append(rowData.get(5)).append("\n");
            details.append("Total Amount: ").append(rowData.get(6)).append("\n");

            JOptionPane.showMessageDialog(this, details.toString(), "Customer Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to view.");
        }
    }

    private void editCustomerDetails() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the selected customer's data from the table
            Vector<Object> rowData = (Vector<Object>) tableModel.getDataVector().elementAt(selectedRow);

            // Create an edit dialog
            JDialog editDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Customer Details");
            editDialog.setLayout(new GridLayout(8, 2));

            JTextField usernameField = new JTextField(rowData.get(1).toString());
            JTextField fullNameField = new JTextField(rowData.get(2).toString());
            JTextField phoneField = new JTextField(rowData.get(3).toString());
            JTextField emailField = new JTextField(rowData.get(4).toString());
            JTextField accountField = new JTextField(rowData.get(5).toString());
            JTextField totalAmountField = new JTextField(rowData.get(6).toString());
            JTextField loanField = new JTextField(rowData.get(7).toString());

            editDialog.add(new JLabel("Username:"));
            editDialog.add(usernameField);
            editDialog.add(new JLabel("Full Name:"));
            editDialog.add(fullNameField);
            editDialog.add(new JLabel("Phone:"));
            editDialog.add(phoneField);
            editDialog.add(new JLabel("Email:"));
            editDialog.add(emailField);
            editDialog.add(new JLabel("Bank Account:"));
            editDialog.add(accountField);
            editDialog.add(new JLabel("Total Amount:"));
            editDialog.add(totalAmountField);
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Update the customer data in the database
                    int sn = (int) rowData.get(0);
                    String newUsername = usernameField.getText();
                    String newFullName = fullNameField.getText();
                    String newPhone = phoneField.getText();
                    String newEmail = emailField.getText();
                    String newAccount = accountField.getText();
                    double newTotalAmount = Double.parseDouble(totalAmountField.getText());
                    

                    // Create an instance of DatabaseManager to handle database operations
                    DatabaseManager dbManager = new DatabaseManager();

                    try {
                        // Connect to the database
                        Connection connection = dbManager.getConnection();

                        // Update the customer data in the database
                        String updateQuery = "UPDATE customer SET username=?, name=?, phone=?, email=?, amount=? WHERE id=?";
                        
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, newUsername);
                        updateStatement.setString(2, newFullName);
                        updateStatement.setString(3, newPhone);
                        updateStatement.setString(4, newEmail);
                        updateStatement.setDouble(5, newTotalAmount);
                              updateStatement.setInt(7, sn);

                        int rowsUpdated = updateStatement.executeUpdate();

                        // Check if the update was successful
                        if (rowsUpdated > 0) {
                            // Update the customer data in the table
                            rowData.set(1, newUsername);
                            rowData.set(2, newFullName);
                            rowData.set(3, newPhone);
                            rowData.set(4, newEmail);
                            rowData.set(5, newAccount);
                            rowData.set(6, newTotalAmount);
                  

                            tableModel.fireTableDataChanged();
                            editDialog.dispose();
                            JOptionPane.showMessageDialog(CustomerDetails.this, "Customer details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(CustomerDetails.this, "Failed to update customer details.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        // Close database resources
                        updateStatement.close();
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(CustomerDetails.this, "An error occurred while updating customer details.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            editDialog.add(saveButton);
            editDialog.pack();
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.");
        }
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) customerTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }
}

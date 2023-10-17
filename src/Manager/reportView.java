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

public class reportView extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton editButton;
    private JButton reportButton;
    private JButton complaintButton;
    private JTextField searchField;
    private JButton searchButton;
    private int selectedUserID; // Stores the selected user's ID
    private int selectedEmployeeID; // Stores the selected employee's ID

    public reportView() {
        // Set the layout manager for this panel
        setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        String[] columnNames = {"SN", "Username", "Full Name", "Phone", "Email", "Bank Account", "Total Amount", "Loan"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JPanel panelWithBorder = new JPanel(new BorderLayout());
        panelWithBorder.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        // Create the table and set the table model
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border from the scroll pane
        add(scrollPane, BorderLayout.CENTER); // Add the table directly to the panel

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

        // Create "Report" button
        reportButton = new JButton("Report");
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitReport();
            }
        });

        // Create "Complaint" button
        complaintButton = new JButton("Complaint");
        complaintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitComplaint();
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
        buttonPanel.add(reportButton);
        buttonPanel.add(complaintButton);
        buttonPanel.add(new JLabel("Search by Username:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        add(buttonPanel, BorderLayout.SOUTH);
        // Add a black border at the bottom
        add(panelWithBorder, BorderLayout.CENTER);

        // Load customer data from the database
        loadCustomerDataFromDatabase();
    }

    private void loadCustomerDataFromDatabase() {
        // Create an instance of DatabaseManager to handle database operations
        DatabaseManager dbManager = new DatabaseManager();

        try {
            // Connect to the database
            Connection connection = dbManager.getConnection();

            // Execute a SQL query to retrieve customer data
            String query = "SELECT * FROM user";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Clear the existing table data
            tableModel.setRowCount(0);

            // Populate the table model with data from the database
            while (resultSet.next()) {
                int sn = resultSet.getInt("sn");
                String username = resultSet.getString("username");
                String fullName = resultSet.getString("full_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String bankAccount = resultSet.getString("bank_account");
                double totalAmount = resultSet.getDouble("total_amount");
                double loan = resultSet.getDouble("loan");

                Object[] rowData = {sn, username, fullName, phone, email, bankAccount, totalAmount, loan};
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
            details.append("Loan: ").append(rowData.get(7)).append("\n");

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
            editDialog.add(new JLabel("Loan:"));
            editDialog.add(loanField);

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
                    double newLoan = Double.parseDouble(loanField.getText());

                    // Create an instance of DatabaseManager to handle database operations
                    DatabaseManager dbManager = new DatabaseManager();

                    try {
                        // Connect to the database
                        Connection connection = dbManager.getConnection();

                        // Update the customer data in the database
                        String updateQuery = "UPDATE user SET username=?, full_name=?, phone=?, email=?, bank_account=?, total_amount=?, loan=? WHERE sn=?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, newUsername);
                        updateStatement.setString(2, newFullName);
                        updateStatement.setString(3, newPhone);
                        updateStatement.setString(4, newEmail);
                        updateStatement.setString(5, newAccount);
                        updateStatement.setDouble(6, newTotalAmount);
                        updateStatement.setDouble(7, newLoan);
                        updateStatement.setInt(8, sn);

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
                            rowData.set(7, newLoan);

                            tableModel.fireTableDataChanged();
                            editDialog.dispose();
                            JOptionPane.showMessageDialog(reportView.this, "Customer details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(reportView.this, "Failed to update customer details.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        // Close database resources
                        updateStatement.close();
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(reportView.this, "An error occurred while updating customer details.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void submitReport() {
        // Logic to submit a report to the database
        String title = JOptionPane.showInputDialog(this, "Enter Report Title:");
        String description = JOptionPane.showInputDialog(this, "Enter Report Description:");
        // You can add more fields like date, etc.

        // Insert the report into the 'user_reports' table
        DatabaseManager dbManager = new DatabaseManager();
        try {
            Connection connection = dbManager.getConnection();
            String insertQuery = "INSERT INTO user_reports (user_id, title, description) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, selectedUserID); // Replace with the actual user ID
            insertStatement.setString(2, title);
            insertStatement.setString(3, description);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Report submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit report.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            insertStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while submitting the report.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitComplaint() {
        // Logic to submit a complaint to the database
        String title = JOptionPane.showInputDialog(this, "Enter Complaint Title:");
        String description = JOptionPane.showInputDialog(this, "Enter Complaint Description:");
        // You can add more fields like date, etc.

        // Insert the complaint into the 'employee_complaints' table
        DatabaseManager dbManager = new DatabaseManager();
        try {
            Connection connection = dbManager.getConnection();
            String insertQuery = "INSERT INTO employee_complaints (employee_id, title, description) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, selectedEmployeeID); // Replace with the actual employee ID
            insertStatement.setString(2, title);
            insertStatement.setString(3, description);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Complaint submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit complaint.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            insertStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while submitting the complaint.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) customerTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }
}

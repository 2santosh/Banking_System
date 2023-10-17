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

public class EmployeeDetails extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton editButton;
    private JTextField searchField;
    private JButton searchButton;

    public EmployeeDetails() {
        // Set the layout manager for this panel
        setLayout(new BorderLayout());

        String[] columnNames = {"Name", "Username", "Email", "Password", "Gender", "DOB", "Address", "Phone", "Education", "Experience"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // Create the table and set the table model
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create "View" button
        viewButton = new JButton("View");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewEmployeeDetails();
            }
        });

        // Create "Edit" button
        editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editEmployeeDetails();
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

        // Load employee data from the database
        loadEmployeeDataFromDatabase();
    }

    private void loadEmployeeDataFromDatabase() {
        // Create an instance of DatabaseManager to handle database operations
        DatabaseManager dbManager = new DatabaseManager();

        try {
            // Connect to the database
            Connection connection = dbManager.getConnection();

            // Execute a SQL query to retrieve employee data
            String query = "SELECT * FROM employee";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Clear the existing table data
            tableModel.setRowCount(0);

            // Populate the table model with data from the database
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String gender = resultSet.getString("gender");
                String dob = resultSet.getString("dob");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");
                String education = resultSet.getString("education");
                String experience = resultSet.getString("experience");

                Object[] rowData = {name, username, email, password, gender, dob, address, phone, education, experience};
                tableModel.addRow(rowData);
            }

            // Close database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load employee data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewEmployeeDetails() {
        // Display all employee details in a dialog (customize this as needed)
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            Vector<Object> rowData = (Vector<Object>) tableModel.getDataVector().elementAt(selectedRow);

            StringBuilder details = new StringBuilder();
            details.append("Name: ").append(rowData.get(0)).append("\n");
            details.append("Username: ").append(rowData.get(1)).append("\n");
            details.append("Email: ").append(rowData.get(2)).append("\n");
            details.append("Password: ").append(rowData.get(3)).append("\n");
            details.append("Gender: ").append(rowData.get(4)).append("\n");
            details.append("DOB: ").append(rowData.get(5)).append("\n");
            details.append("Address: ").append(rowData.get(6)).append("\n");
            details.append("Phone: ").append(rowData.get(7)).append("\n");
            details.append("Education: ").append(rowData.get(8)).append("\n");
            details.append("Experience: ").append(rowData.get(9)).append("\n");

            JOptionPane.showMessageDialog(this, details.toString(), "Employee Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to view.");
        }
    }

    private void editEmployeeDetails() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the selected employee's data from the table
            Vector<Object> rowData = (Vector<Object>) tableModel.getDataVector().elementAt(selectedRow);

            // Create an edit dialog
            JDialog editDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Employee Details");
            editDialog.setLayout(new GridLayout(10, 2));

            JTextField nameField = new JTextField(rowData.get(0).toString());
            JTextField usernameField = new JTextField(rowData.get(1).toString());
            JTextField emailField = new JTextField(rowData.get(2).toString());
            JTextField passwordField = new JTextField(rowData.get(3).toString());
            JTextField genderField = new JTextField(rowData.get(4).toString());
            JTextField dobField = new JTextField(rowData.get(5).toString());
            JTextField addressField = new JTextField(rowData.get(6).toString());
            JTextField phoneField = new JTextField(rowData.get(7).toString());
            JTextField educationField = new JTextField(rowData.get(8).toString());
            JTextField experienceField = new JTextField(rowData.get(9).toString());

            editDialog.add(new JLabel("Name:"));
            editDialog.add(nameField);
            editDialog.add(new JLabel("Username:"));
            editDialog.add(usernameField);
            editDialog.add(new JLabel("Email:"));
            editDialog.add(emailField);
            editDialog.add(new JLabel("Password:"));
            editDialog.add(passwordField);
            editDialog.add(new JLabel("Gender:"));
            editDialog.add(genderField);
            editDialog.add(new JLabel("DOB:"));
            editDialog.add(dobField);
            editDialog.add(new JLabel("Address:"));
            editDialog.add(addressField);
            editDialog.add(new JLabel("Phone:"));
            editDialog.add(phoneField);
            editDialog.add(new JLabel("Education:"));
            editDialog.add(educationField);
            editDialog.add(new JLabel("Experience:"));
            editDialog.add(experienceField);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Update the employee data in the database
                    String originalUsername = rowData.get(1).toString();
                    String newName = nameField.getText();
                    String newUsername = usernameField.getText();
                    String newEmail = emailField.getText();
                    String newPassword = passwordField.getText();
                    String newGender = genderField.getText();
                    String newDob = dobField.getText();
                    String newAddress = addressField.getText();
                    String newPhone = phoneField.getText();
                    String newEducation = educationField.getText();
                    String newExperience = experienceField.getText();

                    // Create an instance of DatabaseManager to handle database operations
                    DatabaseManager dbManager = new DatabaseManager();

                    try {
                        // Connect to the database
                        Connection connection = dbManager.getConnection();

                        // Update the employee data in the database
                        String updateQuery = "UPDATE employee SET name=?, username=?, email=?, password=?, gender=?, dob=?, address=?, phone=?, education=?, experience=? WHERE username=?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, newName);
                        updateStatement.setString(2, newUsername);
                        updateStatement.setString(3, newEmail);
                        updateStatement.setString(4, newPassword);
                        updateStatement.setString(5, newGender);
                        updateStatement.setString(6, newDob);
                        updateStatement.setString(7, newAddress);
                        updateStatement.setString(8, newPhone);
                        updateStatement.setString(9, newEducation);
                        updateStatement.setString(10, newExperience);
                        updateStatement.setString(11, originalUsername);

                        int rowsUpdated = updateStatement.executeUpdate();

                        // Check if the update was successful
                        if (rowsUpdated > 0) {
                            // Update the employee data in the table
                            rowData.set(0, newName);
                            rowData.set(1, newUsername);
                            rowData.set(2, newEmail);
                            rowData.set(3, newPassword);
                            rowData.set(4, newGender);
                            rowData.set(5, newDob);
                            rowData.set(6, newAddress);
                            rowData.set(7, newPhone);
                            rowData.set(8, newEducation);
                            rowData.set(9, newExperience);

                            tableModel.fireTableDataChanged();
                            editDialog.dispose();
                            JOptionPane.showMessageDialog(EmployeeDetails.this, "Employee details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(EmployeeDetails.this, "Failed to update employee details.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        // Close database resources
                        updateStatement.close();
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(EmployeeDetails.this, "An error occurred while updating employee details.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            editDialog.add(saveButton);
            editDialog.pack();
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
        }
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) employeeTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }
}

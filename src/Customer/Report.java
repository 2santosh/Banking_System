package Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

import Data.DatabaseManager;

public class Report extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton reportButton;
    private JTextField searchField;
    private JButton searchButton;
    private int selectedUserID; // Stores the selected user's ID
    private String username;

    public Report(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        String[] columnNames = {"Report ID", "Report Text", "Report Date", "Report Solution"};
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
                viewReportDetails();
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
        buttonPanel.add(reportButton);
        buttonPanel.add(new JLabel("Search by Report Number:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        add(buttonPanel, BorderLayout.SOUTH);
        add(panelWithBorder, BorderLayout.CENTER);

        // Load report data from the database
        loadReportDataFromDatabase();
    }

    private void loadReportDataFromDatabase() {
        // Create an instance of DatabaseManager to handle database operations
        DatabaseManager dbManager = new DatabaseManager();

        try {
            // Connect to the database
            Connection connection = dbManager.getConnection();
            String reports = username+"_report"; // Update table name

            // Execute a SQL query to retrieve report data
            String query = "SELECT id, report_text, date_time, report_solution FROM " + reports;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Clear the existing table data
            tableModel.setRowCount(0);

            // Populate the table model with data from the database
            while (resultSet.next()) {
                int reportId = resultSet.getInt("id");
                String reportText = resultSet.getString("report_text");
                Timestamp reportDate = resultSet.getTimestamp("date_time");
                String reportSolution = resultSet.getString("report_solution");

                Object[] rowData = {reportId, reportText, reportDate, reportSolution};
                tableModel.addRow(rowData);
            }

            // Close database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load report data from the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewReportDetails() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            Vector<Object> rowData = (Vector<Object>) tableModel.getDataVector().elementAt(selectedRow);

            StringBuilder details = new StringBuilder();
            details.append("Report ID: ").append(rowData.get(0)).append("\n");
            details.append("Report Text: ").append(rowData.get(1)).append("\n");
            details.append("Report Date: ").append(rowData.get(2)).append("\n");
            details.append("Report Solution: ").append(rowData.get(3)).append("\n");

            JOptionPane.showMessageDialog(this, details.toString(), "Report Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a report to view.");
        }
    }

    private void submitReport() {
        String reports = username + "_report"; // Update table name
        String reportText = JOptionPane.showInputDialog(this, "Enter Report Text:");
        String reportSolution = JOptionPane.showInputDialog(this, "Enter Report Solution");

        // Insert the report into the 'user_reports' table
        DatabaseManager dbManager = new DatabaseManager();
        try {
            Connection connection = dbManager.getConnection();
            String insertQuery = "INSERT INTO " + reports + " (report_text, report_solution) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, reportText);
            insertStatement.setString(2, reportSolution);
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

    private void performSearch() {
        String query = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) customerTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }
}

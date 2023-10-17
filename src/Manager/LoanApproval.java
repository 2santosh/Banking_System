package Manager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import Data.DatabaseManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoanApproval extends JPanel {

    private JTable loanApplicationTable;
    private DefaultTableModel tableModel;

    public LoanApproval() {
        setLayout(new BorderLayout());

        // Create a table model
        String[] columnNames = {"Applicant Name", "Application ID", "Loan Amount", "Status", "Approve"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // Create a table and set the model
        loanApplicationTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing only for the "Approve" column
                return column == 4;
            }
        };

        // Create a custom renderer and editor for the "Approve" column
        loanApplicationTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        loanApplicationTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(loanApplicationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch loan applications from the database using the DatabaseManager
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM loan_applications"; // Update the table name
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String applicantName = resultSet.getString("applicant_name");
                String applicationID = resultSet.getString("application_id");
                double loanAmount = resultSet.getDouble("loan_amount");
                String status = resultSet.getString("status");

                // Add data to the table model
                Object[] rowData = {applicantName, applicationID, loanAmount, status, "Approve"};
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database.");
        }
    }

    // Custom button renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Custom button editor
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }

            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }
}

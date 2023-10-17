package Employee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class LoanApplicationForm extends JFrame {
    private JTextField accountNoField;
    private JTextField loanAmountField;
    private JTextField statementField;
    private JCheckBox approveCheckBox;
    private JButton submitButton;

    public LoanApplicationForm(String username) {
        setTitle("Loan Application Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(6, 2));

        JLabel accountNoLabel = new JLabel("Account No:");
        accountNoField = new JTextField();
        JLabel loanAmountLabel = new JLabel("Loan Amount:");
        loanAmountField = new JTextField();
        JLabel statementLabel = new JLabel("Statement:");
        statementField = new JTextField();
        submitButton = new JButton("Submit");

        mainPanel.add(accountNoLabel);
        mainPanel.add(accountNoField);
        mainPanel.add(loanAmountLabel);
        mainPanel.add(loanAmountField);
        mainPanel.add(statementLabel);
        mainPanel.add(statementField);
        mainPanel.add(new JLabel()); // Empty label for spacing
        mainPanel.add(submitButton);

        add(mainPanel);
        setVisible(true);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountNo = Integer.parseInt(accountNoField.getText());
                    double loanAmount = Double.parseDouble(loanAmountField.getText());
                    String statement = statementField.getText();
                    boolean approved = approveCheckBox.isSelected();
                    Date dateTime = new Date(); // Current date and time

                    String dbUrl = "jdbc:mysql://localhost:3306/bank";
                    String dbUser = "root";
                    String dbPassword = "root";

                    Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                    String insertQuery = "INSERT INTO username_loan (date_time, loan_amount, statement, approve, account_no) " +
                            "VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setTimestamp(1, new java.sql.Timestamp(dateTime.getTime()));
                    preparedStatement.setDouble(2, loanAmount);
                    preparedStatement.setString(3, statement);
                    preparedStatement.setBoolean(4, approved);
                    preparedStatement.setInt(5, accountNo);

                    int rowsInserted = preparedStatement.executeUpdate();

                    preparedStatement.close();
                    connection.close();

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Loan Application Submitted Successfully!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to submit loan application.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred while submitting the loan application.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

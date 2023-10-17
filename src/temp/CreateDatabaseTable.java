package temp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateDatabaseTable {
    private String username;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public CreateDatabaseTable(String username) {
        this.username = username;
    }

    public void initializeAccountTable() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD)) {
            createTransactionTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTransactionTables(Connection connection) {
        String[] transactionTypes = {"deposit", "withdraw", "transfer", "loan", "report"};

        for (String transactionType : transactionTypes) {
            String tableName = username + "_" + transactionType;
            String createTableSQL = ""; // Initialize the SQL statement

            if (transactionType.equals("deposit") || transactionType.equals("withdraw")) {
                createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "date_time DATETIME NOT NULL," +
                        "cheque_no VARCHAR(255) NOT NULL," +
                        "amount DECIMAL(10, 2) NOT NULL," +
                        "deposit_name VARCHAR(255) NOT NULL," +
                        "deposit_phone VARCHAR(15)," +
                        "account_no INT," + // Add a reference to the customer table
                        "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                        ")";
            } else if (transactionType.equals("withdraw")) {
                createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "date_time DATETIME NOT NULL," +
                        "cheque_no VARCHAR(255) NOT NULL," +
                        "amount DECIMAL(10, 2) NOT NULL," +
                        "withdraw_name VARCHAR(255) NOT NULL," +
                        "withdraw_phone VARCHAR(15)," +
                        "account_no INT," +
                        "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                        ")";

            } else if (transactionType.equals("transfer")) {
                createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "date_time DATETIME NOT NULL," +
                        "receiver_account_no VARCHAR(255) NOT NULL," +
                        "receiver_full_name VARCHAR(255) NOT NULL," +
                        "amount DECIMAL(10, 2) NOT NULL," +
                        "account_no INT," + // Add a reference to the customer table
                        "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                        ")";
            } else if (transactionType.equals("loan")) {
                createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "date_time DATETIME NOT NULL," +
                        "loan_amount DECIMAL(10, 2) NOT NULL," +
                        "statement TEXT," +
                        "approve BOOLEAN NOT NULL," +
                        "account_no INT," + // Add a reference to the customer table
                        "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                        ")";
            } else if (transactionType.equals("report")) {
                createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(200) NOT NULL," +
                        "date_time DATETIME NOT NULL," +
                        "report_text TEXT NOT NULL," +
                        "account_no INT," + // Add a reference to the customer table
                        "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                        ")";
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableSQL);
                System.out.println("Table '" + tableName + "' created successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}



package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random; // Add the import for Random

public class AccountTableManager {

    public AccountTableManager(String username, String fullname, Float amount) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/bank";
        String dbUsername = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, password)) {
            if (!isTableExists(connection, "account")) {
                createAccountTable(connection);
            } else {
                System.out.println("'account' table already exists.");
            }

            // Insert data into the 'account' table
            insertDataIntoAccountTable(connection, username, fullname, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTableExists(Connection connection, String tableName) throws SQLException {
        // Check if the table exists in the database
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT 1 FROM information_schema.tables WHERE table_name = ?")) {
            preparedStatement.setString(1, tableName);
            return preparedStatement.executeQuery().next();
        }
    }

    public static void createAccountTable(Connection connection) throws SQLException {
        // Create the 'account' table with 'account_no' as the primary key
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE account (" +
                    "account_no INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50)," +
                    "fullname VARCHAR(100)," +
                    "check_number INT," +
                    "Amount FLOAT NOT NULL" +
                    ")";
            statement.execute(createTableSQL);
            
            // Set the initial value for 'account_no' auto-increment
            String setInitialValueSQL = "ALTER TABLE account AUTO_INCREMENT = 12010010";
            statement.execute(setInitialValueSQL);
            
            System.out.println("'account' table created.");
        }
    }

    public static void insertDataIntoAccountTable(Connection connection, String username, String fullName, float amount) throws SQLException {
        // Generate a random 5-digit check_number
        Random rand = new Random();
        int randomCheckNumber = rand.nextInt(100000); // Generates a random integer up to 5 digits

        // Insert data into the 'account' table
        String insertSQL = "INSERT INTO account (username, fullname, check_number, Amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, fullName);
            preparedStatement.setInt(3, randomCheckNumber); // Set the random check_number
            preparedStatement.setFloat(4, amount);
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Data inserted into 'account' table.");
            }
        }
    }
}

package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class OtherTableManager {
    private final String username;

    public OtherTableManager(String username) {
        this.username = username;
        createTables();
    }

    private void createTables() {
        String jdbcURL = "jdbc:mysql://localhost:3306/"; // Replace with your MySQL server details
        String dbUsername = "root";
        String dbPassword = "root";
        String databaseName = "bank";

        try (Connection connection = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword)) {
            Statement statement = connection.createStatement();

            // Create the database if it doesn't exist
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            statement.executeUpdate(createDatabaseSQL);

            // Switch to the newly created database
            String useDatabaseSQL = "USE " + databaseName;
            statement.executeUpdate(useDatabaseSQL);

            // Define transaction types
            String[] transactionTypes = {"deposit", "withdraw", "transfer", "loan", "report"};

            // Create tables for each transaction type
            for (String transactionType : transactionTypes) {
                String tableName = username + "_" + transactionType;

                String createTableSQL = null;


                if (transactionType.equals("deposit") || transactionType.equals("deposit")) {
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
                            "sender_account_no VARCHAR(255) NOT NULL," +
                            "sender_full_name VARCHAR(255) NOT NULL," +
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
                            "report_solution VARCHAR(200)," + 
                            "account_no INT," + 
                            "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                            ")";
                }

                if (createTableSQL != null) {
                    statement.executeUpdate(createTableSQL);
                    System.out.println("Table '" + tableName + "' created successfully.");
                }
            }

            // Create account details table
            String accountTableName = "accounts";
            ResultSet tables = connection.getMetaData().getTables(null, null, accountTableName, null);
            boolean accountTableExists = tables.next();

            if (!accountTableExists) {
                // Create the user's account table if it doesn't exist
                String createAccountTableSQL = "CREATE TABLE IF NOT EXISTS " + accountTableName + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "account_no INT NOT NULL," +
                        "user_name VARCHAR(266) NOT NULL," +
                        "full_name VARCHAR(255) NOT NULL," +
                        "account_balance DECIMAL(10, 2) NOT NULL," +
                        "cheque_number INT UNIQUE" +
                        ")";
                statement.executeUpdate(createAccountTableSQL);
                System.out.println("Account table created successfully.");

                // Insert data from the customer table
                insertDataFromCustomerTable(connection, statement, accountTableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDataFromCustomerTable(Connection connection, Statement statement, String accountTableName) throws SQLException {
        // Replace this query with the actual query to retrieve data from the customer table based on the username
        String selectCustomerDataSQL = "SELECT * FROM account WHERE username = '" + username + "'";
        ResultSet resultSet = statement.executeQuery(selectCustomerDataSQL);

        if (resultSet.next()) {
            // Retrieve customer data
            int accountNo = resultSet.getInt("account_no");
            String fullName = resultSet.getString("fullname"); // Fixed column name
            double accountBalance = resultSet.getDouble("amount"); // Fixed column name

            // Insert the data into the account table
            String insertAccountDataSQL = "INSERT INTO " + accountTableName + " (account_no, username, fullname, amount) VALUES (?, ?, ?, ?)";
            try (java.sql.PreparedStatement preparedStatement = connection.prepareStatement(insertAccountDataSQL)) {
                preparedStatement.setInt(1, accountNo);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, fullName);
                preparedStatement.setDouble(4, accountBalance);
                preparedStatement.executeUpdate();
                System.out.println("Data inserted into the account table.");
            }
        }
    }
}


package Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
    }

    public boolean checkPassword(String tableName, String username, String passwordToCheck) {
        try (Connection connection = getConnection()) {
            String query = "SELECT password FROM " + tableName + " WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(passwordToCheck);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


	public boolean updatePassword(String tableName, String username, String newPassword) {
	    try (Connection connection = getConnection()) {
	        String query = "UPDATE " + tableName + " SET password = ? WHERE username = ?";
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, newPassword);
	        preparedStatement.setString(2, username);

	        int rowsUpdated = preparedStatement.executeUpdate();
	        return rowsUpdated > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public String retrieveUserInfo(String tableName, String username) {
	    try (Connection connection = getConnection()) {
	        String query = "SELECT full_name FROM " + tableName + " WHERE username = ?";
	        PreparedStatement preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, username);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            return resultSet.getString("full_name");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

}

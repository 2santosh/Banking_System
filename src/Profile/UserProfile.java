package Profile;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Data.DatabaseManager;

public class UserProfile extends JPanel {

    private static final long serialVersionUID = 1L;
    private String username;
    private String tableName;

    public UserProfile(String tableName, String username) {
        this.tableName = tableName;
        this.username = username;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fetch user details from the database based on the table name and username
        fetchUserDetails(tableName, username);
    }

    private void fetchUserDetails(String tableName, String username) {

    	try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE username = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve user details from the ResultSet and display them as JLabels
                String fullName = resultSet.getString("full_name");
                String dob = resultSet.getString("dob");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String permanentAddress = resultSet.getString("permanent_address");
                String temporaryAddress = resultSet.getString("temporary_address");
                String fatherName = resultSet.getString("father_name");
                String motherName = resultSet.getString("mother_name");
                String maritalStatus = resultSet.getString("marital_status");
                String education = resultSet.getString("education");
                String jobJoinYear = resultSet.getString("job_join_year");

                JLabel nameLabel = new JLabel("Name: " + fullName);
                JLabel usernameLabel = new JLabel("Username: " + username);
                JLabel dobLabel = new JLabel("Date of Birth: " + dob);
                JLabel emailLabel = new JLabel("Email: " + email);
                JLabel phoneLabel = new JLabel("Phone: " + phone);
                JLabel permanentAddressLabel = new JLabel("Permanent Address: " + permanentAddress);
                JLabel temporaryAddressLabel = new JLabel("Temporary Address: " + temporaryAddress);
                JLabel fatherNameLabel = new JLabel("Father's Name: " + fatherName);
                JLabel motherNameLabel = new JLabel("Mother's Name: " + motherName);
                JLabel maritalStatusLabel = new JLabel("Marital Status: " + maritalStatus);
                JLabel educationLabel = new JLabel("Education: " + education);
                JLabel jobJoinYearLabel = new JLabel("Job Join Year: " + jobJoinYear);

                // Add the JLabels to the panel
                add(nameLabel);
                add(usernameLabel);
                add(dobLabel);
                add(emailLabel);
                add(phoneLabel);
                add(permanentAddressLabel);
                add(temporaryAddressLabel);
                add(fatherNameLabel);
                add(motherNameLabel);
                add(maritalStatusLabel);
                add(educationLabel);
                add(jobJoinYearLabel);
                // Add more profile details as needed
            } else {
                JLabel notFoundLabel = new JLabel("User not found");
                add(notFoundLabel);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

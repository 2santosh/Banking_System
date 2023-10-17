package Register;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import Data.DatabaseManager;
import Main.Application;
import temp.CreateDatabaseTable;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ManagerRegister {
    private static JFrame frame;
    private JPanel panel;
    private JLabel nameLabel, emailLabel, passwordLabel, confirmPasswordLabel, usernameLabel, genderLabel, dobLabel, addressLabel, phoneLabel, experienceLabel, imageLabel, educationLabel, degreeLabel, schoolLabel, collegeLabel, universityLabel, passOutDateLabel;
    private JTextField nameField, emailField, usernameField, addressField, experienceField, imageField, degreeField, schoolField, collegeField, universityField;
    private JPasswordField passwordField, confirmPasswordField;
    private JFormattedTextField dobField, passOutDateField; // Use JFormattedTextField for Date of Birth and Pass Out Date
    private JRadioButton maleRadioButton, femaleRadioButton, otherRadioButton;
    private ButtonGroup genderGroup;
    private JButton registerButton, uploadFileButton, addEducationButton, addExperienceButton;
    private JTextArea educationTextArea, experienceTextArea;
    private JTextField phoneField; // Changed to JTextField for numeric input
    private List<String> educationList; // Store edaucation as strings
    private List<String> experienceList; // Store experience as strings
    private String uploadedFilePath;

    private JLabel organizationLabel, periodLabel, fromLabel, toLabel, resignationReasonLabel;
    private JTextField organizationField, fromField, toField,  resignationReasonField;

    public ManagerRegister() {
        frame = new JFrame("Manager Registration Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setResizable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel = new JPanel();
        panel.setLayout(null);
        panel = new JPanel();
        panel.setLayout(null);

        
        
        
        
     // Set the title of the form
        JLabel titleLabel = new JLabel("MANAGER REGISTER FORM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set the font style to bold and size
        titleLabel.setHorizontalAlignment(JLabel.CENTER); // Center-align the title
        frame.add(titleLabel, BorderLayout.NORTH); // Add title label to the top of the frame

       
        panel = new JPanel();
        panel.setLayout(null);
        nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 20, 100, 20);
        nameField = new JTextField();
        nameField.setBounds(160, 20, 200, 20);

        // Add input validation for name (allow only alphabetic characters and spaces)
        nameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isLetter(c) || c == ' ' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });

        emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 50, 100, 20);
        emailField = new JTextField();
        emailField.setBounds(160, 50, 200, 20);
        // Add input validation for email format
        emailField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String email = emailField.getText();
                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                    emailField.requestFocus();
                }
            }
        });

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 80, 100, 20);
        passwordField = new JPasswordField();
        passwordField.setBounds(160, 80, 200, 20);

        confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(30, 110, 130, 20);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(160, 110, 200, 20);

        // Replace personalLabel and personalField with usernameLabel and usernameField
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 140, 100, 20);
        usernameField = new JTextField();
        usernameField.setBounds(160, 140, 200, 20);
        usernameField.setEditable(false); // Set the field as non-editable

        emailField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    updateUsernameFromEmail();
                });
            }
        });

        genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(30, 170, 100, 20);
        maleRadioButton = new JRadioButton("Male");
        maleRadioButton.setBounds(160, 170, 80, 20);
        femaleRadioButton = new JRadioButton("Female");
        femaleRadioButton.setBounds(240, 170, 80, 20);
        otherRadioButton = new JRadioButton("Other");
        otherRadioButton.setBounds(320, 170, 80, 20);
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);
        genderGroup.add(otherRadioButton);

        dobLabel = new JLabel("Date of Birth :");
        dobLabel.setBounds(30, 200, 180, 20);
        dobField = new JFormattedTextField(createDateFormat());
        dobField.setBounds(160, 200, 140, 20);

        // Add input validation for date of birth format
        dobField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String dob = dobField.getText();
                if (!isValidDateFormat(dob)) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid date of birth (e.g., yyyy-MM-dd).", "Error", JOptionPane.ERROR_MESSAGE);
                    dobField.requestFocus();
                }
            }
        });

        addressLabel = new JLabel("Address:");
        addressLabel.setBounds(30, 230, 100, 20);
        addressField = new JTextField();
        addressField.setBounds(160, 230, 200, 20);
        // Add input validation for address (allow alphanumeric characters, spaces, and common punctuation)
        addressField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isLetterOrDigit(c) || Character.isSpaceChar(c) || c == ',' || c == '.' || c == '-' || c == '/' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });

        phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(30, 260, 100, 20);
        phoneField = new JTextField();
        phoneField.setBounds(160, 260, 200, 20);

        // Add input validation for phone number (only numeric)
        phoneField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });

        // Add focus listener to check phone number length
        phoneField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String phoneNumber = phoneField.getText();
                if (phoneNumber.length() != 10) {
                    JOptionPane.showMessageDialog(frame, "Phone number must be exactly 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                    phoneField.requestFocus();
                }
            }
        });

        educationLabel = new JLabel("Education:");
        educationLabel.setBounds(30, 290, 100, 20);

        degreeLabel = new JLabel("Degree:");
        degreeLabel.setBounds(30, 320, 100, 20);
        degreeField = new JTextField();
        degreeField.setBounds(160, 320, 200, 20);

        schoolLabel = new JLabel("School:");
        schoolLabel.setBounds(30, 350, 100, 20);
        schoolField = new JTextField();
        schoolField.setBounds(160, 350, 200, 20);

        collegeLabel = new JLabel("College:");
        collegeLabel.setBounds(30, 380, 100, 20);
        collegeField = new JTextField();
        collegeField.setBounds(160, 380, 200, 20);

        universityLabel = new JLabel("University:");
        universityLabel.setBounds(30, 410, 100, 20);
        universityField = new JTextField();
        universityField.setBounds(160, 410, 200, 20);

        passOutDateLabel = new JLabel("Pass Out Date (yyyy-MM-dd):");
        passOutDateLabel.setBounds(30, 440, 200, 40);
        passOutDateField = new JFormattedTextField(createDateFormat());
        passOutDateField.setBounds(240, 440, 120, 20);

        // Add input validation for pass out date format
        passOutDateField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '-' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });

        // Add input validation for pass out date format
        passOutDateField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String passOutDate = passOutDateField.getText();
                if (!isValidDateFormat(passOutDate)) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid pass out date (e.g., yyyy-MM-dd).", "Error", JOptionPane.ERROR_MESSAGE);
                    passOutDateField.requestFocus();
                }
            }
        });

        addEducationButton = new JButton("Add Education");
        addEducationButton.setBounds(450, 440, 130, 20);
        addEducationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String degree = degreeField.getText();
                String school = schoolField.getText();
                String college = collegeField.getText();
                String university = universityField.getText();
                String passOutDate = passOutDateField.getText();

                if (!degree.isEmpty() && !school.isEmpty() && !college.isEmpty() && !university.isEmpty() && !passOutDate.isEmpty()) {
                    String education = "Degree: " + degree
                            + ", School: " + school
                            + ", College: " + college
                            + ", University: " + university
                            + ", Pass Out Date: " + passOutDate;
                    educationList.add(education);
                    updateEducationTextArea();
                    degreeField.setText("");
                    schoolField.setText("");
                    collegeField.setText("");
                    universityField.setText("");
                    passOutDateField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all education fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        educationList = new ArrayList<>();
        educationTextArea = new JTextArea();
        educationTextArea.setEditable(false);
        educationTextArea.setBounds(160, 470, 420, 40);

        experienceLabel = new JLabel("Experience:");
        experienceLabel.setBounds(30, 520, 100, 20);
        experienceField = new JTextField();
        experienceField.setBounds(160, 650, 0, 0);
        experienceField.setEditable(false);

        organizationLabel = new JLabel("Organization:");
        organizationLabel.setBounds(30, 540, 100, 20);
        organizationField = new JTextField();
        organizationField.setBounds(160, 540, 200, 20);

        periodLabel = new JLabel("Period:");
        periodLabel.setBounds(30, 570, 100, 20);
        fromLabel = new JLabel("From:");
        fromLabel.setBounds(160, 570, 50, 20);
        fromField = new JTextField();
        fromField.setBounds(210, 570, 80, 20);
        toLabel = new JLabel("To:");
        toLabel.setBounds(300, 570, 30, 20);
        toField = new JTextField();
        toField.setBounds(330, 570, 80, 20);

        resignationReasonLabel = new JLabel("Reason for Resignation:");
        resignationReasonLabel.setBounds(30, 600, 150, 20);
        resignationReasonField = new JTextField();
        resignationReasonField.setBounds(180, 600, 230, 20);

        addExperienceButton = new JButton("Add Experience");
        addExperienceButton.setBounds(450, 600, 130, 20);
        
        addExperienceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String organization = organizationField.getText();
                String period = fromField.getText() + " - " + toField.getText();
                String separationReason =  resignationReasonField.getText();

                if (!organization.isEmpty() && !period.isEmpty() && !separationReason.isEmpty()) {
                    String experience = "Organization: " + organization
                            + ", Period: " + period
                            + ", Separation Reason: " + separationReason;
                    experienceList.add(experience);
                    updateExperienceTextArea();
                    organizationField.setText("");
                    fromField.setText("");
                    toField.setText("");
                    resignationReasonField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill in all experience fields.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        experienceList = new ArrayList<>();
        experienceTextArea = new JTextArea();
        experienceTextArea.setEditable(false);
        experienceTextArea.setBounds(160, 630, 420, 40);

        imageLabel = new JLabel("Image:");
        imageLabel.setBounds(400, 60, 100, 20);
        imageField = new JTextField();
        imageField.setBounds(450, 60, 100, 20);

        uploadFileButton = new JButton("Upload File");
        uploadFileButton.setBounds(450, 80, 100, 20);
        uploadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    long fileSize = selectedFile.length();
                    long maxSizeBytes = 600 * 1024; // 600KB in bytes

                    if (fileSize > maxSizeBytes) {
                        JOptionPane.showMessageDialog(frame, "Image size exceeds 600KB. Please choose a smaller image.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        uploadedFilePath = selectedFile.getAbsolutePath();
                        imageField.setText(uploadedFilePath);
                    }
                }
            }
        });

        registerButton = new JButton("Register");
        registerButton.setBounds(250, 550, 100, 30);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	createTable();
            	String name = nameField.getText();
            	String email = emailField.getText();               
                String password = String.valueOf(passwordField.getPassword());
                String username = usernameField.getText();
                String gender = maleRadioButton.isSelected() ? "Male" : (femaleRadioButton.isSelected() ? "Female" : "Other");
                String dob = dobField.getText();
                String address = addressField.getText();
                String phone = phoneField.getText();
                String education = educationTextArea.getText();
                String experience = experienceTextArea.getText();
                String imageFilePath = imageField.getText();
                
                String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(frame, "Passwords do not match. Please re-enter your password.", "Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                } else {
                    // Check if the username already exists in the database
                    try (Connection conn = DatabaseManager.getConnection()) {
                        String checkUsernameQuery = "SELECT COUNT(*) FROM manager WHERE username = ?";
                        PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameQuery);
                        checkUsernameStmt.setString(1, username);
                        ResultSet resultSet = checkUsernameStmt.executeQuery();

                        if (resultSet.next() && resultSet.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(frame, "Username already exists. Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String sql = "INSERT INTO manager (name, username, email, password, gender, dob, address, phone, education, experience, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setString(1, name);
                            stmt.setString(2, username);
                            stmt.setString(3, email);
                            stmt.setString(4, password);
                            stmt.setString(5, gender);
                            stmt.setString(6, dob);
                            stmt.setString(7, address);
                            stmt.setString(8, phone);
                            stmt.setString(9, education);
                            stmt.setString(10, experience);
                            stmt.setString(11, imageFilePath);

                            int rowsInserted = stmt.executeUpdate();

                            if (rowsInserted > 0) {
                                JOptionPane.showMessageDialog(frame, "Registration Successful! Data inserted into the database.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                SwingUtilities.invokeLater(() -> {
                                    Application.createAndShowUI();
                                    frame.dispose();
                                });
                            } else {
                                JOptionPane.showMessageDialog(frame, "Registration failed. No rows were inserted into the database.", "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            stmt.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error occurred while inserting data into the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Add components to the panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(genderLabel);
        panel.add(maleRadioButton);
        panel.add(femaleRadioButton);
        panel.add(otherRadioButton);
        panel.add(dobLabel);
        panel.add(dobField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(educationLabel);
        panel.add(degreeLabel);
        panel.add(degreeField);
        panel.add(schoolLabel);
        panel.add(schoolField);
        panel.add(collegeLabel);
        panel.add(collegeField);
        panel.add(universityLabel);
        panel.add(universityField);
        panel.add(passOutDateLabel);
        panel.add(passOutDateField);
        panel.add(addEducationButton);
        panel.add(educationTextArea);
        panel.add(experienceLabel);
        panel.add(experienceField);
        panel.add(organizationLabel);
        panel.add(organizationField);
        panel.add(periodLabel);
        panel.add(fromLabel);
        panel.add(fromField);
        panel.add(toLabel);
        panel.add(toField);
        panel.add(resignationReasonLabel);
        panel.add(resignationReasonField);
        panel.add(addExperienceButton);
        panel.add(experienceTextArea);
        panel.add(imageLabel);
        panel.add(imageField);
        panel.add(uploadFileButton);
        panel.add(registerButton);
        scrollPane.setViewportView(panel);
        frame.add(panel);
        frame.setVisible(true);
    }
    private void createTable() {
   	 try (Connection connection = DatabaseManager.getConnection()) {
   	     Statement statement = connection.createStatement();
   	     String createTableSQL = "CREATE TABLE manager ("
   	             + "id INT PRIMARY KEY AUTO_INCREMENT, "
   	             + "name VARCHAR(255), "
   	             + "username VARCHAR(255), "
   	             + "email VARCHAR(255), "
   	             + "password VARCHAR(255), "
   	             + "gender VARCHAR(10), "
   	             + "dob VARCHAR(10), "
   	             + "address VARCHAR(255), "
   	             + "phone VARCHAR(10), "
   	             + "education TEXT, "
   	             + "experience TEXT, " // Added the 'experience' field
   	             + "image VARCHAR(255)"
   	             + ")";
   	     statement.executeUpdate(createTableSQL);
   	 } catch (SQLException e) {
   	     e.printStackTrace();
   	     System.out.println("Error: Failed to create the database table.");
   	 }
   	}
    private MaskFormatter createDateFormat() {
        MaskFormatter dateFormatter = null;
        try {
            dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatter;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidDateFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void updateUsernameFromEmail() {
        String email = emailField.getText();
        if (!email.isEmpty()) {
            int atIndex = email.indexOf('@');
            if (atIndex != -1) {
                String username = email.substring(0, atIndex);
                usernameField.setText(username);
            }
        }
    }

    private void updateEducationTextArea() {
        StringBuilder sb = new StringBuilder();
        for (String education : educationList) {
            sb.append(education).append("\n");
        }
        educationTextArea.setText(sb.toString());
    }

    private void updateExperienceTextArea() {
        StringBuilder sb = new StringBuilder();
        for (String experience : experienceList) {
            sb.append(experience).append("\n");
        }
        experienceTextArea.setText(sb.toString());
    }
    
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(new Runnable() {
        	  public void run() {
            	frame = new JFrame("Manager Registration Form");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 800);
                frame.setResizable(false); // Prevent resizing
                // Create an instance of the EmployeeRegister class
                new ManagerRegister();
                frame.setVisible(true); // Moved setVisible here to ensure it's set after adding components

            }
        });
    }

}

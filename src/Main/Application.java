package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import Login.CustomerLogin;
import Login.EmployeeLogin;
import Login.ManagerLogin;
import Register.CustomerRegister;
import Register.EmployeeRegister;
import Register.ManagerRegister;

public class Application {
    private static final String COMPANY_NAME = "My Banking System";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowUI();
        });
    }

    private static ImageIcon resizeImageIconWithTransparentBackground(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // Use ARGB for transparent background
        Graphics2D g2d = resizedImage.createGraphics();

        // Clear the background to transparent
        resizedImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();

        return new ImageIcon(resizedImage);
    }

    private static void addLogoToPanel(JPanel panel) {
        ImageIcon logoImage = new ImageIcon("logo.png");
        int desiredImageWidth = 200;
        int desiredImageHeight = 200;
        ImageIcon resizedLogoImage = null;

        try {
            resizedLogoImage = resizeImageIconWithTransparentBackground(logoImage, desiredImageWidth, desiredImageHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JLabel logoLabel = new JLabel(resizedLogoImage);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(logoLabel);
    }

    public static void createAndShowUI() {
        JFrame frame = new JFrame(COMPANY_NAME + " - Login"); // Customize the frame title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JPanel panel5 = new JPanel();

        panel1.setBackground(Color.RED);
        panel2.setBackground(Color.BLACK);
        panel3.setBackground(Color.BLACK);
        panel4.setBackground(Color.BLACK);
        panel5.setBackground(Color.LIGHT_GRAY);

        panel1.setPreferredSize(new Dimension(20, 20));
        panel2.setPreferredSize(new Dimension(5, 5));
        panel3.setPreferredSize(new Dimension(5, 5));
        panel4.setPreferredSize(new Dimension(5, 5));
        panel5.setPreferredSize(new Dimension(100, 100));

        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.WEST);
        frame.add(panel3, BorderLayout.EAST);
        frame.add(panel4, BorderLayout.SOUTH);
        frame.add(panel5, BorderLayout.CENTER);

        // Create a new panel for buttons
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Logo Panel
        gbc.gridy = 0;
        gbc.weighty = 100.0;
        JPanel imagePanel = new JPanel(new GridBagLayout());
        addLogoToPanel(imagePanel);
        mainPanel.add(imagePanel, gbc);

        frame.add(mainPanel);
        gbc.gridy = 1;
        JPanel bankInfoPanel = new JPanel(new GridLayout(4, 1));

        // Create labels
        JLabel bankNameLabel = new JLabel("ASB Bank");
        JLabel bankAddressLabel = new JLabel("123 Main Street, City, Country");
        JLabel emailLabel = new JLabel("Email: info@abcbank.com");
        JLabel phoneLabel = new JLabel("Phone: 123-456-7890");

        // Set font to bold
        Font boldFont = new Font(bankNameLabel.getFont().getName(), Font.BOLD, bankNameLabel.getFont().getSize());
        bankNameLabel.setFont(boldFont);
        bankAddressLabel.setFont(boldFont);
        emailLabel.setFont(boldFont);
        phoneLabel.setFont(boldFont);

        // Set JLabel alignment to center
        bankNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bankAddressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        phoneLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bankInfoPanel.add(bankNameLabel);
        bankInfoPanel.add(bankAddressLabel);
        bankInfoPanel.add(emailLabel);
        bankInfoPanel.add(phoneLabel);

        mainPanel.add(bankInfoPanel, gbc);

        // Buttons Panel
        gbc.gridy = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, gbc);

        frame.add(mainPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Customer", "Employee", "Manager"};
                int choice = JOptionPane.showOptionDialog(frame, "Select user type:", "Login",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);
                if (choice == 0) {
                    mainPanel.removeAll();
                    JPanel logoAndLoginPanel = new JPanel(new BorderLayout());
                    logoAndLoginPanel.add(imagePanel, BorderLayout.NORTH);
                    JPanel loginPanel = new CustomerLogin().createLoginPanel(frame);
                    logoAndLoginPanel.add(loginPanel, BorderLayout.CENTER);
                    loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                    mainPanel.add(logoAndLoginPanel, gbc);
                    frame.revalidate();
                    frame.repaint();
                } else if (choice == 1) {
                    mainPanel.removeAll();
                    JPanel logoAndLoginPanel = new JPanel(new BorderLayout());
                    logoAndLoginPanel.add(imagePanel, BorderLayout.NORTH);
                    JPanel loginPanel = new EmployeeLogin().createLoginPanel(frame);
                    logoAndLoginPanel.add(loginPanel, BorderLayout.CENTER);
                    loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                    mainPanel.add(logoAndLoginPanel, gbc);
                    frame.revalidate();
                    frame.repaint();
                } else if (choice == 2) {
                    mainPanel.removeAll();
                    JPanel logoAndLoginPanel = new JPanel(new BorderLayout());
                    logoAndLoginPanel.add(imagePanel, BorderLayout.NORTH);
                    JPanel loginPanel = new ManagerLogin().createLoginPanel(frame);
                    logoAndLoginPanel.add(loginPanel, BorderLayout.CENTER);
                    loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                    mainPanel.add(logoAndLoginPanel, gbc);
                    frame.revalidate();
                    frame.repaint();
                }
            }
        });

        // Add ActionListener for the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Customer", "Employee", "Manager"};
                int choice = JOptionPane.showOptionDialog(frame, "Select user type:", "Register",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);

                if (choice == 0) {
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                        	frame.dispose();
                            new CustomerRegister();
                        }
                    });
                } else if (choice == 1) {
                	
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                        	frame.dispose();
                            new EmployeeRegister();
                        }
                    });
                } else if (choice == 2) {
                	
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                        	frame.dispose();
                            new ManagerRegister();
                            
                        }
                    });
                }
            }
        });

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}

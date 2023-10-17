package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

import Customer.Loan;
import Customer.LoanForm;
import Customer.Report;
import Customer.Statement;
import Customer.Transfer;
import Profile.ChangePassword;
import Profile.UserProfile;

public class Customer extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel headerPanel;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JPanel userProfilePicturePanel;
    private String profilePicturePath = "profile_pictures/default_profile.jpg";
    private JLabel timeLabel;
    private JLabel noticeLabel;
    private JScrollPane scrollPane;
    private UserProfile userProfile;
    private ChangePassword changePassword;
    private CardLayout cardLayout;
    private String username;

    public Customer(String loggedInUser, String profilePicturePath) {
        this.username = loggedInUser;
        this.profilePicturePath = profilePicturePath;
        setTitle("Banking System");
        setSize(800, 600);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // Create header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Add your bank logo image here
        ImageIcon logoIcon = new ImageIcon("logo.jpg");
        int logoWidth = 150;
        int logoHeight = (int) ((double) logoIcon.getIconHeight() / logoIcon.getIconWidth() * logoWidth);
        ImageIcon resizedLogoIcon = new ImageIcon(logoIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH));
        JLabel logoLabel = new JLabel(resizedLogoIcon);
        headerPanel.add(logoLabel, BorderLayout.WEST);

        JLabel companyNameLabel = new JLabel("Bank");
        companyNameLabel.setForeground(Color.BLACK);
        companyNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(companyNameLabel, BorderLayout.CENTER);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setOpaque(false);
        headerPanel.add(userInfoPanel, BorderLayout.EAST);

        // Create a panel for the user profile picture in a circular form
        userProfilePicturePanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Dimension arcs = new Dimension(120, 120);
                int width = getWidth();
                int height = getHeight();
                Graphics2D graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setColor(getBackground());
                graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
                graphics.setColor(getForeground());
                graphics.setStroke(new BasicStroke(4));
                graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            }
        };
        userProfilePicturePanel.setPreferredSize(new Dimension(120, 120));
        userProfilePicturePanel.setBackground(Color.WHITE);

        // Load and display the user's profile picture
        loadUserProfilePicture(userProfilePicturePanel);

        // Add the user profile picture panel to the userInfoPanel
        userInfoPanel.add(userProfilePicturePanel);

        JLabel userInfoLabel = new JLabel("Logged in as: " + loggedInUser);
        userInfoLabel.setForeground(Color.BLACK);
        userInfoPanel.add(userInfoLabel);

        // Create instances of user profile, change password, and about us panels
        userProfile = new UserProfile("user", loggedInUser);
        changePassword = new ChangePassword("user", loggedInUser);

        // Create left panel for menu items
        leftPanel = new JPanel();
        leftPanel.setBackground(Color.GRAY);
        leftPanel.setPreferredSize(new Dimension(120, 0));
        contentPane.add(leftPanel, BorderLayout.WEST);
        leftPanel.setLayout(new GridLayout(0, 1));

        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem viewProfile = new JMenuItem("View Profile");
        JMenuItem changePasswordMenuItem = new JMenuItem("Change Password");

        viewProfile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUserProfile();
            }
        });

        changePasswordMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showChangePasswordContent();
            }
        });

        userMenu.add(viewProfile);
        userMenu.add(changePasswordMenuItem);

        userProfilePicturePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                userMenu.show(userProfilePicturePanel, e.getX(), e.getY());
            }
        });

        addButtonToMenu("Home");
        addButtonToMenu("Transfer");
        addButtonToMenu("Loan Apply");
        addButtonToMenu("Statement");
        addButtonToMenu("Report");
        addButtonToMenu("Logout");

        centerPanel = new JPanel();
        cardLayout = new CardLayout();
        centerPanel.setLayout(cardLayout);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        showDefaultContent();
    }

    private void showDefaultContent() {
        centerPanel.removeAll();
        setTitle("Banking System");

        timeLabel = new JLabel();
        Font timeFont = new Font("SansSerif", Font.BOLD, 24);
        timeLabel.setFont(timeFont);
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(timeLabel, BorderLayout.NORTH);

        noticeLabel = new JLabel();
        noticeLabel.setHorizontalAlignment(JLabel.CENTER);
        noticeLabel.setFont(timeFont);
        int noticeLabelHeight = 10;
        noticeLabel.setPreferredSize(new Dimension(10, noticeLabelHeight));
        centerPanel.add(noticeLabel, BorderLayout.CENTER);

        scrollPane = new JScrollPane(noticeLabel);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JTextPane aboutBankPane = new JTextPane();
        aboutBankPane.setContentType("text/html");
        aboutBankPane.setText("<html><center><h1>About Our Bank</h1></center>"
                + "<p>Our bank, XYZ Bank, is a leading financial institution dedicated to providing exceptional banking services to our customers. With a strong commitment to financial excellence and customer satisfaction, we offer a wide range of services, including savings and checking accounts, loans, mortgages, and investment opportunities. Our mission is to empower our customers to achieve their financial goals and secure their future.</p>"
                + "<p>At XYZ Bank, we pride ourselves on our dedication to integrity, innovation, and community engagement. With a team of experienced professionals and cutting-edge technology, we aim to deliver convenient and reliable banking solutions. Join us on a journey towards financial success!</p></html>");
        aboutBankPane.setEditable(false);
        aboutBankPane.setBackground(null);
        centerPanel.add(aboutBankPane, BorderLayout.CENTER);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String currentTime = dateFormat.format(new Date());
                timeLabel.setText("Current Time: " + currentTime);
            }
        });
        timer.start();

        String noticeText = "Interest Rate: 3.5%   Loan Rate: 5.0%";
        noticeLabel.setText(noticeText);
        Timer slideTimer = new Timer(0, new ActionListener() {
            private int xPos = scrollPane.getWidth();

            @Override
            public void actionPerformed(ActionEvent e) {
                noticeLabel.setBounds(xPos, 0, noticeLabel.getPreferredSize().width, noticeLabel.getPreferredSize().height);
                xPos--;
                if (xPos + noticeLabel.getWidth() < 10) {
                    xPos = scrollPane.getWidth();
                }
            }
        });

        slideTimer.start();

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void showUserProfile() {
        centerPanel.removeAll();
        cardLayout.show(centerPanel, "Profile");
        centerPanel.add(userProfile, "Profile");
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void showChangePasswordContent() {
        centerPanel.removeAll();
        centerPanel.add(changePassword);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void addButtonToMenu(String buttonText, ActionListener actionListener) {
        JButton button = new JButton(buttonText);
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);
        leftPanel.add(button);
    }

    private void addButtonToMenu(String buttonText) {
        addButtonToMenu(buttonText, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMenuItemContent(buttonText);
            }
        });
    }

    private void showMenuItemContent(String menuItem) {
        centerPanel.removeAll();
        if (menuItem.equals("Home")) {
            showDefaultContent();
        } else if (menuItem.equals("Transfer")) {
            centerPanel.removeAll();
            Transfer transferPanel = new Transfer(username);
            centerPanel.add(transferPanel, BorderLayout.CENTER);
            transferPanel.setVisible(true);
        } else if (menuItem.equals("Loan Apply")) {
            centerPanel.removeAll();
            Loan loanPanel = new Loan(username);
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(loanPanel, BorderLayout.CENTER);
            loanPanel.setVisible(true);
        } else if (menuItem.equals("Statement")) {
            centerPanel.removeAll();
            Statement statementPanel = new Statement(username);
            centerPanel.add(statementPanel);
            statementPanel.setVisible(true);
        } else if (menuItem.equals("Report")) {
            centerPanel.removeAll();
            Report reportViewPanel = new Report(username);
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(reportViewPanel, BorderLayout.CENTER);
            reportViewPanel.setVisible(true);
        } else if (menuItem.equals("Logout")) {
            SwingUtilities.invokeLater(() -> {
                dispose();
                Application.createAndShowUI();
            });
        }

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void loadUserProfilePicture(JPanel panel) {
        try {
            BufferedImage img = ImageIO.read(new File(profilePicturePath));
            ImageIcon profilePictureIcon = new ImageIcon(img);

            BufferedImage circularProfilePicture = new BufferedImage(
                    panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = circularProfilePicture.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Ellipse2D.Double clip = new Ellipse2D.Double(0, 0, panel.getWidth(), panel.getHeight());
            g2d.setClip(clip);

            profilePictureIcon.paintIcon(null, g2d, 0, 0);

            g2d.dispose();

            ImageIcon circularProfilePictureIcon = new ImageIcon(circularProfilePicture);

            JLabel profilePictureLabel = new JLabel(circularProfilePictureIcon);

            panel.removeAll();
            panel.add(profilePictureLabel);

            panel.revalidate();
            panel.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

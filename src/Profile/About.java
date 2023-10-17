package Profile;

import javax.swing.*;
import java.awt.*;

public class About extends JPanel {
    public About() {
        setLayout(new BorderLayout());

        // Create a JTextArea to display bank information and rules
        JTextArea aboutText = new JTextArea();
        aboutText.setEditable(false);
        aboutText.setWrapStyleWord(true);
        aboutText.setLineWrap(true);
        aboutText.setFont(new Font("Arial", Font.PLAIN, 14));

        // Replace the following text with your bank's information and rules
        String bankInfoAndRules = "Welcome to Our Bank!\n\n"
                + "About Our Bank:\n"
                + "Our bank has been serving customers for many years with dedication and commitment.\n\n"
                + "Rules and Regulations:\n"
                + "1. Maintain the confidentiality of your account information.\n"
                + "2. Always keep your ATM card and PIN secure.\n"
                + "3. Contact customer support for any account-related inquiries.\n";

        aboutText.setText(bankInfoAndRules);

        // Create a JScrollPane to display the text if it exceeds the panel size
        JScrollPane scrollPane = new JScrollPane(aboutText);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("About Our Bank");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new About());
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

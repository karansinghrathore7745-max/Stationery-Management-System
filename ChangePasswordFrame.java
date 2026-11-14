import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ChangePasswordFrame extends JFrame {
    private JTextField userField;
    private JPasswordField oldPassField, newPassField;
    private JButton updateButton, backButton;

    public ChangePasswordFrame() {
        setTitle("Change Password - Stationery Management System");
        setSize(520, 430);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 244, 248));

        // Top Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(30, 58, 138));
        headerPanel.setBounds(0, 0, 520, 75);
        add(headerPanel);

        JLabel title = new JLabel("CHANGE PASSWORD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(40, 22, 440, 30);
        headerPanel.add(title);

        // Form Inputs
        JLabel uLabel = new JLabel("Username:");
        uLabel.setFont(new Font("Arial", Font.BOLD, 14));
        uLabel.setBounds(65, 105, 120, 30);
        add(uLabel);

        userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setBounds(195, 105, 250, 32);
        add(userField);

        JLabel oLabel = new JLabel("Old Password:");
        oLabel.setFont(new Font("Arial", Font.BOLD, 14));
        oLabel.setBounds(65, 160, 120, 30);
        add(oLabel);

        oldPassField = new JPasswordField();
        oldPassField.setFont(new Font("Arial", Font.PLAIN, 14));
        oldPassField.setBounds(195, 160, 250, 32);
        add(oldPassField);

        JLabel nLabel = new JLabel("New Password:");
        nLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nLabel.setBounds(65, 215, 120, 30);
        add(nLabel);

        newPassField = new JPasswordField();
        newPassField.setFont(new Font("Arial", Font.PLAIN, 14));
        newPassField.setBounds(195, 215, 250, 32);
        add(newPassField);

        // Buttons
        updateButton = new JButton("Update Password");
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setBackground(new Color(37, 99, 235));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBounds(65, 285, 180, 40);
        add(updateButton);

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(100, 116, 139));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBounds(265, 285, 180, 40);
        add(backButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText().trim();
                String oldP = new String(oldPassField.getPassword()).trim();
                String newP = new String(newPassField.getPassword()).trim();

                if (user.isEmpty() || oldP.isEmpty() || newP.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields!");
                    return;
                }

                try {
                    Connection conn = DBConnection.getConnection();
                    PreparedStatement pst = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ? AND password = ?");
                    pst.setString(1, newP);
                    pst.setString(2, user);
                    pst.setString(3, oldP);
                    int rows = pst.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Password Updated Successfully!");
                        dispose();
                        new LoginFrame();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Username or Old Password!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
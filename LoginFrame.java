import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton signUpButton;
    private JButton forgotPassButton;

    public LoginFrame() {
        setTitle("Login - Stationery Management System");
        setSize(520, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 244, 248));

        // Top Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(30, 58, 138));
        headerPanel.setBounds(0, 0, 520, 75);
        add(headerPanel);

        JLabel titleLabel = new JLabel("SYSTEM LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(40, 15, 440, 25);
        headerPanel.add(titleLabel);

        JLabel subLabel = new JLabel("Stationery Management System", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subLabel.setForeground(new Color(219, 234, 254));
        subLabel.setBounds(40, 42, 440, 20);
        headerPanel.add(subLabel);

        // Form Inputs
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setBounds(75, 110, 100, 30);
        add(userLabel);

        userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setBounds(185, 110, 250, 32);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setBounds(75, 165, 100, 30);
        add(passLabel);

        passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBounds(185, 165, 250, 32);
        add(passField);

        // Buttons
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setBackground(new Color(37, 99, 235));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBounds(75, 230, 170, 40);
        add(loginButton);

        signUpButton = new JButton("Create Account");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 15));
        signUpButton.setBackground(new Color(5, 150, 105));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setBounds(265, 230, 170, 40);
        add(signUpButton);

        forgotPassButton = new JButton("Change Password");
        forgotPassButton.setFont(new Font("Arial", Font.BOLD, 14));
        forgotPassButton.setBackground(new Color(71, 85, 105));
        forgotPassButton.setForeground(Color.WHITE);
        forgotPassButton.setFocusPainted(false);
        forgotPassButton.setBounds(75, 290, 360, 38);
        add(forgotPassButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                try {
                    Connection conn = DBConnection.getConnection();
                    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, username);
                    pst.setString(2, password);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        dispose();
                        new DashboardFrame();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Username or Password!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterFrame();
            }
        });

        forgotPassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ChangePasswordFrame();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
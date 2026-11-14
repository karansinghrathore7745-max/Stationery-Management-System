import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;

public class DashboardFrame extends JFrame {
    private JButton inventoryButton;
    private JButton issueButton;
    private JButton logoutButton;
    private JLabel remainingStockLabel;
    private JLabel issuedStockLabel;

    public DashboardFrame() {
        setTitle("Stationery Management System - Dashboard");
        setSize(760, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 244, 248));

        // Top Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(30, 58, 138));
        headerPanel.setBounds(0, 0, 760, 80);
        add(headerPanel);

        JLabel welcomeLabel = new JLabel("STATIONERY MANAGEMENT SYSTEM", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(50, 15, 660, 30);
        headerPanel.add(welcomeLabel);

        JLabel subLabel = new JLabel("Admin Control Panel & Live Stock Overview", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subLabel.setForeground(new Color(219, 234, 254));
        subLabel.setBounds(50, 45, 660, 20);
        headerPanel.add(subLabel);

        // 1. Remaining Stock Card
        remainingStockLabel = new JLabel("Remaining Stock: 0", SwingConstants.CENTER);
        remainingStockLabel.setOpaque(true);
        remainingStockLabel.setBackground(new Color(220, 252, 231));
        remainingStockLabel.setBorder(BorderFactory.createLineBorder(new Color(22, 163, 74), 2));
        remainingStockLabel.setBounds(60, 110, 300, 105);
        add(remainingStockLabel);

        // 2. Total Issued Card
        issuedStockLabel = new JLabel("Total Issued: 0", SwingConstants.CENTER);
        issuedStockLabel.setOpaque(true);
        issuedStockLabel.setBackground(new Color(254, 243, 199));
        issuedStockLabel.setBorder(BorderFactory.createLineBorder(new Color(217, 119, 6), 2));
        issuedStockLabel.setBounds(400, 110, 300, 105);
        add(issuedStockLabel);

        // Action Buttons
        inventoryButton = new JButton("Manage Inventory");
        inventoryButton.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryButton.setBackground(new Color(37, 99, 235));
        inventoryButton.setForeground(Color.WHITE);
        inventoryButton.setFocusPainted(false);
        inventoryButton.setBounds(155, 245, 450, 52);
        add(inventoryButton);

        issueButton = new JButton("Issue Items");
        issueButton.setFont(new Font("Arial", Font.BOLD, 16));
        issueButton.setBackground(new Color(5, 150, 105));
        issueButton.setForeground(Color.WHITE);
        issueButton.setFocusPainted(false);
        issueButton.setBounds(155, 315, 450, 52);
        add(issueButton);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 15));
        logoutButton.setBackground(new Color(220, 38, 38));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBounds(155, 385, 450, 48);
        add(logoutButton);

        inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new InventoryFrame();
            }
        });

        issueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new IssueFrame();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });

        loadDashboardStats();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadDashboardStats() {
        try {
            Connection conn = DBConnection.getConnection();

            ResultSet rs1 = conn.createStatement().executeQuery("SELECT SUM(quantity) FROM inventory");
            if (rs1.next()) {
                int rem = rs1.getInt(1);
                remainingStockLabel.setText("<html><center><span style='font-size:13px; color:#166534;'>REMAINING STOCK</span><br><span style='font-size:26px; color:#14532d;'><b>" + rem + "</b></span></center></html>");
            }

            ResultSet rs2 = conn.createStatement().executeQuery("SELECT SUM(quantity_issued) FROM issued_items");
            if (rs2.next()) {
                int iss = rs2.getInt(1);
                issuedStockLabel.setText("<html><center><span style='font-size:13px; color:#92400e;'>TOTAL ISSUED ITEMS</span><br><span style='font-size:26px; color:#78350f;'><b>" + iss + "</b></span></center></html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DashboardFrame();
    }
}
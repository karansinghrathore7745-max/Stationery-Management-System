import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardFrame extends JFrame {

    private JLabel lblRemainingStock;
    private JLabel lblTotalIssued;

    public DashboardFrame() {
        initUI();
    }

    public DashboardFrame(String username) {
        initUI();
    }

    private void initUI() {
        setTitle("Stationery Management System - Dashboard");
        setSize(680, 490);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Top Royal Blue Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(15, 42, 110));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("STATIONERY MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Admin Control Panel & Live Stock Overview");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(203, 213, 225));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        headerPanel.add(lblSubtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(241, 245, 249));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(22, 45, 25, 45));

        // Live KPI Cards Row
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 22, 0));
        cardsPanel.setBackground(new Color(241, 245, 249));
        cardsPanel.setMaximumSize(new Dimension(600, 105));

        // Card 1: Remaining Stock
        JPanel cardStock = new JPanel();
        cardStock.setBackground(new Color(236, 253, 245));
        cardStock.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(16, 185, 129), 2, true),
                new EmptyBorder(12, 15, 12, 15)
        ));
        cardStock.setLayout(new BoxLayout(cardStock, BoxLayout.Y_AXIS));

        JLabel lblStockTitle = new JLabel("REMAINING STOCK");
        lblStockTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblStockTitle.setForeground(new Color(6, 95, 70));
        lblStockTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblRemainingStock = new JLabel("0");
        lblRemainingStock.setFont(new Font("Arial", Font.BOLD, 28));
        lblRemainingStock.setForeground(new Color(4, 120, 87));
        lblRemainingStock.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardStock.add(lblStockTitle);
        cardStock.add(Box.createRigidArea(new Dimension(0, 8)));
        cardStock.add(lblRemainingStock);

        // Card 2: Total Issued Items
        JPanel cardIssued = new JPanel();
        cardIssued.setBackground(new Color(255, 251, 235));
        cardIssued.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(245, 158, 11), 2, true),
                new EmptyBorder(12, 15, 12, 15)
        ));
        cardIssued.setLayout(new BoxLayout(cardIssued, BoxLayout.Y_AXIS));

        JLabel lblIssuedTitle = new JLabel("TOTAL ISSUED ITEMS");
        lblIssuedTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblIssuedTitle.setForeground(new Color(146, 64, 14));
        lblIssuedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTotalIssued = new JLabel("0");
        lblTotalIssued.setFont(new Font("Arial", Font.BOLD, 28));
        lblTotalIssued.setForeground(new Color(180, 83, 9));
        lblTotalIssued.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardIssued.add(lblIssuedTitle);
        cardIssued.add(Box.createRigidArea(new Dimension(0, 8)));
        cardIssued.add(lblTotalIssued);

        cardsPanel.add(cardStock);
        cardsPanel.add(cardIssued);
        centerPanel.add(cardsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 26)));

        // Action Buttons
        JButton btnManageInventory = createMenuButton("Manage Inventory", new Color(29, 78, 216));
        JButton btnIssueItems = createMenuButton("Issue Items", new Color(22, 163, 74));
        JButton btnLogout = createMenuButton("Logout", new Color(220, 38, 38));

        centerPanel.add(btnManageInventory);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        centerPanel.add(btnIssueItems);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        centerPanel.add(btnLogout);

        add(centerPanel, BorderLayout.CENTER);

        // Load Live Stats from MySQL
        loadLiveCounts();

        // Button Actions (Safe & Visible)
        btnManageInventory.addActionListener(e -> {
            try {
                InventoryFrame inv = new InventoryFrame();
                inv.setVisible(true);
                dispose();
            } catch (Throwable ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening Inventory: " + ex.getMessage());
            }
        });

        btnIssueItems.addActionListener(e -> {
            try {
                IssueFrame issue = new IssueFrame();
                issue.setVisible(true);
                dispose();
            } catch (Throwable ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening Issue Items: " + ex.getMessage());
            }
        });

        btnLogout.addActionListener(e -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                dispose();
            } catch (Throwable ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during Logout: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private JButton createMenuButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(600, 46));
        btn.setPreferredSize(new Dimension(600, 46));
        return btn;
    }

    private void loadLiveCounts() {
        try (Connection conn = DBConnection.getConnection()) {
            // Remaining Stock Sum
            try (PreparedStatement pst1 = conn.prepareStatement("SELECT COALESCE(SUM(quantity), 0) FROM inventory");
                 ResultSet rs1 = pst1.executeQuery()) {
                if (rs1.next()) {
                    lblRemainingStock.setText(String.valueOf(rs1.getInt(1)));
                }
            }

            // Total Issued Items Sum
            try (PreparedStatement pst2 = conn.prepareStatement("SELECT COALESCE(SUM(quantity_issued), 0) FROM issued_items");
                 ResultSet rs2 = pst2.executeQuery()) {
                if (rs2.next()) {
                    lblTotalIssued.setText(String.valueOf(rs2.getInt(1)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}

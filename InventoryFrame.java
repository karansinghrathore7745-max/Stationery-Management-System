import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addButton, updateStockButton, deleteButton, searchButton, refreshButton, backButton;

    public InventoryFrame() {
        setTitle("Stationery Inventory Management");
        setSize(820, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 244, 248));

        // Top Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(30, 58, 138));
        headerPanel.setBounds(0, 0, 820, 70);
        add(headerPanel);

        JLabel title = new JLabel("STATIONERY INVENTORY STOCK", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(50, 20, 720, 30);
        headerPanel.add(title);

        // Table
        String[] columns = {"Item ID", "Item Name", "Category", "Quantity", "Price (Rs)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(219, 234, 254));

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(25, 90, 770, 300);
        add(sp);

        // Colorful Buttons (6 Buttons cleanly spaced)
        addButton = new JButton("Add Item");
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setBackground(new Color(5, 150, 105)); // Green
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBounds(25, 415, 115, 40);
        add(addButton);

        updateStockButton = new JButton("Update Stock");
        updateStockButton.setFont(new Font("Arial", Font.BOLD, 13));
        updateStockButton.setBackground(new Color(124, 58, 237)); // Purple
        updateStockButton.setForeground(Color.WHITE);
        updateStockButton.setFocusPainted(false);
        updateStockButton.setBounds(150, 415, 135, 40);
        add(updateStockButton);

        deleteButton = new JButton("Delete Item");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 13));
        deleteButton.setBackground(new Color(220, 38, 38)); // Red
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBounds(295, 415, 125, 40);
        add(deleteButton);

        searchButton = new JButton("Search Item");
        searchButton.setFont(new Font("Arial", Font.BOLD, 13));
        searchButton.setBackground(new Color(37, 99, 235)); // Blue
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBounds(430, 415, 125, 40);
        add(searchButton);

        refreshButton = new JButton("Show All");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 13));
        refreshButton.setBackground(new Color(217, 119, 6)); // Amber
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBounds(565, 415, 105, 40);
        add(refreshButton);

        backButton = new JButton("Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 13));
        backButton.setBackground(new Color(71, 85, 105)); // Slate Gray
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBounds(680, 415, 115, 40);
        add(backButton);

        // 1. Add Item Action
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField nameField = new JTextField();
                JTextField catField = new JTextField();
                JTextField qtyField = new JTextField();
                JTextField priceField = new JTextField();

                Object[] message = {
                        "Item Name:", nameField,
                        "Category:", catField,
                        "Quantity:", qtyField,
                        "Price per Unit:", priceField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Add New Item", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String name = nameField.getText();
                        String category = catField.getText();
                        int qty = Integer.parseInt(qtyField.getText());
                        double price = Double.parseDouble(priceField.getText());

                        Connection conn = DBConnection.getConnection();
                        PreparedStatement pst = conn.prepareStatement("INSERT INTO inventory (item_name, category, quantity, price_per_unit) VALUES (?, ?, ?, ?)");
                        pst.setString(1, name);
                        pst.setString(2, category);
                        pst.setInt(3, qty);
                        pst.setDouble(4, price);
                        pst.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Item Added Successfully!");
                        loadInventoryData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        // 2. Update Stock Action (Quantity zyada ya kam karne ke liye)
        updateStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField idField = new JTextField();
                // Agar user ne table me koi row select ki hai toh ID apne aap bhar jayegi
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                }

                String[] actions = {"Add Stock (+)", "Reduce Stock (-)", "Set Exact Quantity (=)"};
                JComboBox<String> actionBox = new JComboBox<>(actions);
                JTextField qtyField = new JTextField();

                Object[] message = {
                        "Item ID:", idField,
                        "Select Action:", actionBox,
                        "Quantity:", qtyField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update Item Stock", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        int itemId = Integer.parseInt(idField.getText().trim());
                        int inputQty = Integer.parseInt(qtyField.getText().trim());
                        String selectedAction = actionBox.getSelectedItem().toString();

                        if (inputQty < 0) {
                            JOptionPane.showMessageDialog(null, "Quantity negative nahi ho sakti!");
                            return;
                        }

                        Connection conn = DBConnection.getConnection();
                        PreparedStatement checkPst = conn.prepareStatement("SELECT item_name, quantity FROM inventory WHERE item_id = ?");
                        checkPst.setInt(1, itemId);
                        ResultSet rs = checkPst.executeQuery();

                        if (rs.next()) {
                            String itemName = rs.getString("item_name");
                            int currentQty = rs.getInt("quantity");
                            int updatedQty = currentQty;

                            if (selectedAction.equals("Add Stock (+)")) {
                                updatedQty = currentQty + inputQty;
                            } else if (selectedAction.equals("Reduce Stock (-)")) {
                                if (inputQty > currentQty) {
                                    JOptionPane.showMessageDialog(null, "Error! Sirf " + currentQty + " stock available hai, usse zyada kam nahi kar sakte.");
                                    return;
                                }
                                updatedQty = currentQty - inputQty;
                            } else {
                                updatedQty = inputQty;
                            }

                            PreparedStatement updatePst = conn.prepareStatement("UPDATE inventory SET quantity = ? WHERE item_id = ?");
                            updatePst.setInt(1, updatedQty);
                            updatePst.setInt(2, itemId);
                            updatePst.executeUpdate();

                            JOptionPane.showMessageDialog(null, "Stock Updated!\n" + itemName + ": " + currentQty + " -> " + updatedQty);
                            loadInventoryData();
                        } else {
                            JOptionPane.showMessageDialog(null, "Item ID not found!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter valid numbers for Item ID and Quantity!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        // 3. Delete Item Action
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idStr = JOptionPane.showInputDialog(null, "Enter Item ID to Delete:");
                if (idStr != null && !idStr.trim().isEmpty()) {
                    try {
                        int itemId = Integer.parseInt(idStr.trim());
                        Connection conn = DBConnection.getConnection();
                        PreparedStatement pst = conn.prepareStatement("DELETE FROM inventory WHERE item_id = ?");
                        pst.setInt(1, itemId);
                        int rows = pst.executeUpdate();

                        if (rows > 0) JOptionPane.showMessageDialog(null, "Item Deleted!");
                        else JOptionPane.showMessageDialog(null, "ID not found!");

                        loadInventoryData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        // 4. Search Item Action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = JOptionPane.showInputDialog(null, "Enter Item Name to Search:");
                if (keyword != null && !keyword.trim().isEmpty()) {
                    tableModel.setRowCount(0);
                    try {
                        Connection conn = DBConnection.getConnection();
                        PreparedStatement pst = conn.prepareStatement("SELECT * FROM inventory WHERE item_name LIKE ?");
                        pst.setString(1, "%" + keyword.trim() + "%");
                        ResultSet rs = pst.executeQuery();

                        while (rs.next()) {
                            Object[] row = {rs.getInt("item_id"), rs.getString("item_name"), rs.getString("category"), rs.getInt("quantity"), rs.getDouble("price_per_unit")};
                            tableModel.addRow(row);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // 5. Show All Action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadInventoryData();
            }
        });

        // 6. Back to Dashboard
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DashboardFrame();
            }
        });

        loadInventoryData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadInventoryData() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM inventory");
            while (rs.next()) {
                Object[] row = {rs.getInt("item_id"), rs.getString("item_name"), rs.getString("category"), rs.getInt("quantity"), rs.getDouble("price_per_unit")};
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new InventoryFrame();
    }
}
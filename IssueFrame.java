import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IssueFrame extends JFrame {
    private JTextField customerField, qtyField;
    private JComboBox<String> itemDropdown;
    private JTable issueTable;
    private DefaultTableModel issueTableModel;
    private JLabel grandTotalLabel;
    private JButton addToListButton, confirmIssueButton, backButton;
    private double grandTotal = 0.0;

    public IssueFrame() {
        setTitle("Issue Stationery Items - Billing");
        setSize(820, 540);
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

        JLabel title = new JLabel("ISSUE STATIONERY ITEMS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(50, 20, 720, 30);
        headerPanel.add(title);

        // Customer Name
        JLabel custLabel = new JLabel("Customer / Student Name:");
        custLabel.setFont(new Font("Arial", Font.BOLD, 14));
        custLabel.setBounds(35, 90, 190, 30);
        add(custLabel);

        customerField = new JTextField();
        customerField.setFont(new Font("Arial", Font.PLAIN, 14));
        customerField.setBounds(230, 90, 250, 30);
        add(customerField);

        // Select Item Dropdown
        JLabel itemLabel = new JLabel("Select Item:");
        itemLabel.setFont(new Font("Arial", Font.BOLD, 14));
        itemLabel.setBounds(35, 135, 95, 30);
        add(itemLabel);

        itemDropdown = new JComboBox<>();
        itemDropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        itemDropdown.setBounds(130, 135, 350, 30);
        add(itemDropdown);

        // Quantity Input
        JLabel qtyLabel = new JLabel("Qty:");
        qtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        qtyLabel.setBounds(495, 135, 35, 30);
        add(qtyLabel);

        qtyField = new JTextField();
        qtyField.setFont(new Font("Arial", Font.PLAIN, 14));
        qtyField.setBounds(535, 135, 75, 30);
        add(qtyField);

        // Add to List Button
        addToListButton = new JButton("Add to List");
        addToListButton.setFont(new Font("Arial", Font.BOLD, 14));
        addToListButton.setBackground(new Color(37, 99, 235));
        addToListButton.setForeground(Color.WHITE);
        addToListButton.setFocusPainted(false);
        addToListButton.setBounds(625, 135, 155, 30);
        add(addToListButton);

        // Issue Items Table
        String[] cols = {"Item ID", "Item Name", "Rate (Rs)", "Qty", "Total (Rs)"};
        issueTableModel = new DefaultTableModel(cols, 0);
        issueTable = new JTable(issueTableModel);
        issueTable.setFont(new Font("Arial", Font.PLAIN, 14));
        issueTable.setRowHeight(26);
        issueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        issueTable.getTableHeader().setBackground(new Color(219, 234, 254));

        JScrollPane sp = new JScrollPane(issueTable);
        sp.setBounds(35, 185, 745, 225);
        add(sp);

        // Grand Total Label
        grandTotalLabel = new JLabel("Grand Total: Rs. 0.0");
        grandTotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        grandTotalLabel.setForeground(new Color(22, 101, 52));
        grandTotalLabel.setBounds(35, 430, 300, 40);
        add(grandTotalLabel);

        // Final Confirm & Issue Button
        confirmIssueButton = new JButton("Confirm & Issue");
        confirmIssueButton.setFont(new Font("Arial", Font.BOLD, 15));
        confirmIssueButton.setBackground(new Color(5, 150, 105));
        confirmIssueButton.setForeground(Color.WHITE);
        confirmIssueButton.setFocusPainted(false);
        confirmIssueButton.setBounds(430, 430, 190, 42);
        add(confirmIssueButton);

        // Back to Dashboard Button
        backButton = new JButton("Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setBackground(new Color(71, 85, 105));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBounds(635, 430, 145, 42);
        add(backButton);

        // 1. Add to List Action
        addToListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemDropdown.getSelectedItem() == null || qtyField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select an item and enter quantity!");
                    return;
                }

                try {
                    String selected = itemDropdown.getSelectedItem().toString();
                    int itemId = Integer.parseInt(selected.split(" - ")[0]);
                    int reqQty = Integer.parseInt(qtyField.getText().trim());

                    if (reqQty <= 0) {
                        JOptionPane.showMessageDialog(null, "Quantity must be greater than 0!");
                        return;
                    }

                    Connection conn = DBConnection.getConnection();
                    PreparedStatement pst = conn.prepareStatement("SELECT item_name, quantity, price_per_unit FROM inventory WHERE item_id = ?");
                    pst.setInt(1, itemId);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        String name = rs.getString("item_name");
                        int availableStock = rs.getInt("quantity");
                        double price = rs.getDouble("price_per_unit");

                        if (reqQty > availableStock) {
                            JOptionPane.showMessageDialog(null, "Out of Stock! Only " + availableStock + " items available.");
                            return;
                        }

                        double rowTotal = price * reqQty;
                        grandTotal += rowTotal;

                        Object[] row = {itemId, name, price, reqQty, rowTotal};
                        issueTableModel.addRow(row);

                        grandTotalLabel.setText("Grand Total: Rs. " + grandTotal);
                        qtyField.setText("");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number in Quantity!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 2. Final Confirm & Issue Action
        confirmIssueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customer = customerField.getText().trim();
                if (customer.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter Customer / Student Name!");
                    return;
                }
                if (issueTableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Item list is empty! Please add items first.");
                    return;
                }

                try {
                    Connection conn = DBConnection.getConnection();

                    for (int i = 0; i < issueTableModel.getRowCount(); i++) {
                        int itemId = Integer.parseInt(issueTableModel.getValueAt(i, 0).toString());
                        String itemName = issueTableModel.getValueAt(i, 1).toString();
                        int qtySold = Integer.parseInt(issueTableModel.getValueAt(i, 3).toString());
                        double totalPrice = Double.parseDouble(issueTableModel.getValueAt(i, 4).toString());

                        PreparedStatement insertPst = conn.prepareStatement(
                                "INSERT INTO issued_items (customer_name, item_id, item_name, quantity_issued, total_price) VALUES (?, ?, ?, ?, ?)"
                        );
                        insertPst.setString(1, customer);
                        insertPst.setInt(2, itemId);
                        insertPst.setString(3, itemName);
                        insertPst.setInt(4, qtySold);
                        insertPst.setDouble(5, totalPrice);
                        insertPst.executeUpdate();

                        PreparedStatement updatePst = conn.prepareStatement(
                                "UPDATE inventory SET quantity = quantity - ? WHERE item_id = ?"
                        );
                        updatePst.setInt(1, qtySold);
                        updatePst.setInt(2, itemId);
                        updatePst.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(null, "Items Issued Successfully to " + customer + "!\nTotal Amount: Rs. " + grandTotal);
                    dispose();
                    new DashboardFrame();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        // 3. Back Button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DashboardFrame();
            }
        });

        loadItemsIntoDropdown();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadItemsIntoDropdown() {
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM inventory WHERE quantity > 0");
            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("item_name");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price_per_unit");
                itemDropdown.addItem(id + " - " + name + " (Stock: " + qty + " | Rs. " + price + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
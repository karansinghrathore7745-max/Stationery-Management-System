import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IssueFrame extends JFrame {

    private JTextField txtCustomerName, txtQuantity;
    private JComboBox<String> cmbItems;
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JLabel lblGrandTotal;

    private Map<String, Integer> itemIdMap = new HashMap<>();
    private Map<String, Integer> itemStockMap = new HashMap<>();
    private Map<String, Double> itemPriceMap = new HashMap<>();

    private double grandTotal = 0.0;
    private String detectedPriceColumn = "price_per_unit";

    public IssueFrame() {
        setTitle("Issue Stationery Items");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // Top Header Banner (Royal Blue)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 58, 138));
        headerPanel.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lblTitle = new JLabel("ISSUE STATIONERY ITEMS & BILLING");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Form + Table)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Input Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Issue Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer / Student Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Customer / Student Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtCustomerName = new JTextField(18);
        formPanel.add(txtCustomerName, gbc);

        // Select Item
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Select Item:"), gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0;
        cmbItems = new JComboBox<>();
        formPanel.add(cmbItems, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtQuantity = new JTextField(10);
        formPanel.add(txtQuantity, gbc);

        // Add to List & Remove Selected Buttons
        JPanel listActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnAddToList = new JButton("Add to List");
        btnAddToList.setBackground(new Color(37, 99, 235));
        btnAddToList.setForeground(Color.WHITE);
        btnAddToList.setFont(new Font("Arial", Font.BOLD, 12));
        btnAddToList.setFocusPainted(false);

        JButton btnRemoveRow = new JButton("Remove Selected");
        btnRemoveRow.setBackground(new Color(220, 38, 38));
        btnRemoveRow.setForeground(Color.WHITE);
        btnRemoveRow.setFont(new Font("Arial", Font.BOLD, 12));
        btnRemoveRow.setFocusPainted(false);

        listActionPanel.add(btnAddToList);
        listActionPanel.add(btnRemoveRow);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(listActionPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Issue Items Table
        String[] columns = {"Item ID", "Item Name", "Quantity", "Rate (Rs)", "Total (Rs)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        issueTable = new JTable(tableModel);
        issueTable.setRowHeight(25);
        issueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        issueTable.getTableHeader().setBackground(new Color(219, 234, 254));
        centerPanel.add(new JScrollPane(issueTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel (Grand Total + Confirm & Issue + Back)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(8, 20, 15, 20));

        lblGrandTotal = new JLabel("Grand Total: Rs. 0.00");
        lblGrandTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblGrandTotal.setForeground(new Color(22, 101, 52));
        bottomPanel.add(lblGrandTotal, BorderLayout.WEST);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnConfirmIssue = new JButton("Confirm & Issue");
        btnConfirmIssue.setBackground(new Color(22, 163, 74));
        btnConfirmIssue.setForeground(Color.WHITE);
        btnConfirmIssue.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirmIssue.setFocusPainted(false);

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setBackground(new Color(71, 85, 105));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setFocusPainted(false);

        rightBtns.add(btnConfirmIssue);
        rightBtns.add(btnBack);
        bottomPanel.add(rightBtns, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Safe load available items from MySQL
        loadAvailableItems();

        // Button Actions
        btnAddToList.addActionListener(e -> addItemToList());
        btnRemoveRow.addActionListener(e -> removeSelectedRow());
        btnConfirmIssue.addActionListener(e -> confirmAndIssueItems());
        btnBack.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
    }

    private void loadAvailableItems() {
        cmbItems.removeAllItems();
        itemIdMap.clear();
        itemStockMap.clear();
        itemPriceMap.clear();

        String sql = "SELECT * FROM inventory WHERE quantity > 0 ORDER BY item_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            for (int c = 1; c <= colCount; c++) {
                String colName = meta.getColumnName(c).toLowerCase();
                if (colName.contains("price") || colName.contains("rate")) {
                    detectedPriceColumn = meta.getColumnName(c);
                    break;
                }
            }

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("item_name");
                int stock = rs.getInt("quantity");
                double price = rs.getDouble(detectedPriceColumn);

                String displayLabel = name + " (Stock: " + stock + " | Rs." + price + ")";
                cmbItems.addItem(displayLabel);
                itemIdMap.put(displayLabel, id);
                itemStockMap.put(displayLabel, stock);
                itemPriceMap.put(displayLabel, price);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Load Warning: " + ex.getMessage());
        }
    }

    private void addItemToList() {
        if (cmbItems.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No stationery items available in stock!");
            return;
        }

        String qtyStr = txtQuantity.getText().trim();
        if (qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity!");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric quantity!");
            return;
        }

        String selectedLabel = cmbItems.getSelectedItem().toString();
        int itemId = itemIdMap.get(selectedLabel);
        int availableStock = itemStockMap.get(selectedLabel);
        double rate = itemPriceMap.get(selectedLabel);
        String pureItemName = selectedLabel.substring(0, selectedLabel.indexOf(" (Stock:"));

        int alreadyInTable = 0;
        int existingRow = -1;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == itemId) {
                alreadyInTable = (int) tableModel.getValueAt(i, 2);
                existingRow = i;
                break;
            }
        }

        if (qty + alreadyInTable > availableStock) {
            JOptionPane.showMessageDialog(this, "Out of Stock! Available stock is only " + availableStock + " units.");
            return;
        }

        if (existingRow != -1) {
            int updatedQty = alreadyInTable + qty;
            double updatedTotal = updatedQty * rate;
            tableModel.setValueAt(updatedQty, existingRow, 2);
            tableModel.setValueAt(String.format("%.2f", updatedTotal), existingRow, 4);
        } else {
            double rowTotal = qty * rate;
            tableModel.addRow(new Object[]{itemId, pureItemName, qty, String.format("%.2f", rate), String.format("%.2f", rowTotal)});
        }

        recalculateTotal();
        txtQuantity.setText("");
    }

    private void removeSelectedRow() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item row from the table to remove.");
            return;
        }
        tableModel.removeRow(selectedRow);
        recalculateTotal();
    }

    private void recalculateTotal() {
        grandTotal = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            grandTotal += Double.parseDouble(tableModel.getValueAt(i, 4).toString());
        }
        lblGrandTotal.setText(String.format("Grand Total: Rs. %.2f", grandTotal));
    }

    private void confirmAndIssueItems() {
        String customerName = txtCustomerName.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Customer / Student Name!");
            return;
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to the list!");
            return;
        }

        String insertIssueSql = "INSERT INTO issued_items (customer_name, item_id, item_name, quantity_issued, total_price) VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE inventory SET quantity = quantity - ? WHERE item_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstInsert = conn.prepareStatement(insertIssueSql);
                 PreparedStatement pstUpdate = conn.prepareStatement(updateStockSql)) {

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int itemId = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                    String itemName = tableModel.getValueAt(i, 1).toString();
                    int qtyIssued = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                    double totalPrice = Double.parseDouble(tableModel.getValueAt(i, 4).toString());

                    pstInsert.setString(1, customerName);
                    pstInsert.setInt(2, itemId);
                    pstInsert.setString(3, itemName);
                    pstInsert.setInt(4, qtyIssued);
                    pstInsert.setDouble(5, totalPrice);
                    pstInsert.addBatch();

                    pstUpdate.setInt(1, qtyIssued);
                    pstUpdate.setInt(2, itemId);
                    pstUpdate.addBatch();
                }

                pstInsert.executeBatch();
                pstUpdate.executeBatch();
                conn.commit();

                // Open Official Store Receipt & PDF Download Dialog
                showAndDownloadReceipt(customerName, tableModel, grandTotal);

                // Reset form after receipt closes
                txtCustomerName.setText("");
                txtQuantity.setText("");
                tableModel.setRowCount(0);
                recalculateTotal();
                loadAvailableItems();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Transaction Failed: " + ex.getMessage());
        }
    }

    private void showAndDownloadReceipt(String customerName, DefaultTableModel listModel, double totalAmount) {
        JDialog receiptDialog = new JDialog(this, "Stationery Issue Receipt", true);
        receiptDialog.setSize(450, 540);
        receiptDialog.setLocationRelativeTo(this);
        receiptDialog.setLayout(new BorderLayout(10, 10));

        String receiptNo = "STN-" + (10000 + (int) (Math.random() * 90000));
        String dateTime = new SimpleDateFormat("dd-MMM-yyyy  hh:mm a").format(new Date());

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("             CENTRAL STATIONERY STORE             \n");
        sb.append("         STATIONERY ISSUE VOUCHER / RECEIPT       \n");
        sb.append("==================================================\n");
        sb.append("Receipt No   : ").append(receiptNo).append("\n");
        sb.append("Date & Time  : ").append(dateTime).append("\n");
        sb.append("Issued To    : ").append(customerName).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-21s %4s %9s %11s\n", "Item Description", "Qty", "Rate", "Amount"));
        sb.append("--------------------------------------------------\n");

        for (int i = 0; i < listModel.getRowCount(); i++) {
            String itemName = listModel.getValueAt(i, 1).toString();
            if (itemName.length() > 19) {
                itemName = itemName.substring(0, 19) + "..";
            }
            String qty = listModel.getValueAt(i, 2).toString();
            String rate = listModel.getValueAt(i, 3).toString();
            String total = listModel.getValueAt(i, 4).toString();
            sb.append(String.format("%-21s %4s %9s %11s\n", itemName, qty, rate, total));
        }

        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-35s Rs. %.2f\n", "NET PAYABLE AMOUNT:", totalAmount));
        sb.append("==================================================\n");
        sb.append("  Goods once issued will not be taken back.       \n");
        sb.append("             Authorized Store Copy                \n");
        sb.append("==================================================\n");

        JTextArea receiptArea = new JTextArea(sb.toString());
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        receiptArea.setMargin(new Insets(14, 14, 14, 14));
        receiptArea.setBackground(Color.WHITE);

        JButton btnDownloadPdf = new JButton("Download / Save as PDF");
        btnDownloadPdf.setBackground(new Color(30, 58, 138));
        btnDownloadPdf.setForeground(Color.WHITE);
        btnDownloadPdf.setFont(new Font("Arial", Font.BOLD, 13));
        btnDownloadPdf.setFocusPainted(false);

        JButton btnClose = new JButton("Close");
        btnClose.setBackground(new Color(100, 116, 139));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Arial", Font.BOLD, 13));
        btnClose.setFocusPainted(false);

        btnDownloadPdf.addActionListener(e -> {
            try {
                boolean printed = receiptArea.print();
                if (printed) {
                    JOptionPane.showMessageDialog(receiptDialog, "Receipt PDF Downloaded / Saved Successfully!");
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(receiptDialog, "Unable to save PDF: " + ex.getMessage());
            }
        });

        btnClose.addActionListener(e -> receiptDialog.dispose());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        footerPanel.add(btnDownloadPdf);
        footerPanel.add(btnClose);

        receiptDialog.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        receiptDialog.add(footerPanel, BorderLayout.SOUTH);
        receiptDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IssueFrame().setVisible(true));
    }
}

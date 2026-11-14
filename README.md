# Stationery Management System
A desktop-based Inventory & Item Issue Management System developed using **Java (Swing/AWT)** and **MySQL (JDBC)**.

## Key Modules & Features
- **Authentication (`LoginFrame`, `RegisterFrame`, `ChangePasswordFrame`):** Secure user login, registration, and password update using `PreparedStatement`.
- **Live Dashboard (`DashboardFrame`):** Displays real-time **Remaining Stock** and **Total Issued Items** directly from MySQL.
- **Inventory Management (`InventoryFrame`):** Add new items, **Update Stock (`+` / `-`)**, Delete records, and Search items.
- **Item Issue / Billing (`IssueFrame`):** Multi-item issue list with live stock validation and automatic inventory deduction.

## Tech Stack
- **Frontend:** Java Swing & AWT
- **Backend:** Core Java (JDBC)
- **Database:** MySQL (`stationery_db`)

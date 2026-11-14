import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Yahan root ke baad apna MySQL wala password dal dena
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/stationery_db", "root", "Vi9d7742#");
            System.out.println("Database Connected Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;

    }public static void main(String[] args) {
        getConnection();
    }
}
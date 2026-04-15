import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Trying to connect...");

            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/idcard_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root",
                "Vijaya@123"
            );

            System.out.println("Connected!");

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 SHOW ERROR
        }

        return con;
    }
}

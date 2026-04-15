import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {
    public static void main(String[] args) {
        try {
            System.out.println("Loading driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Connecting...");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3307/idcard_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root",
                "Vijaya@123"
            );

            System.out.println("SUCCESS ✅ Connected!");

        } catch (Exception e) {
            System.out.println("FAILED ❌");
            e.printStackTrace();
        }
    }
}

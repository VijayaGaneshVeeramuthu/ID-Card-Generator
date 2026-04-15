import java.sql.*;

public class SetupDB {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            
            System.out.println("Executing guaranteed fix database updates...");
            
            stmt.executeUpdate("DROP TABLE IF EXISTS login");
            stmt.executeUpdate("CREATE TABLE login (username VARCHAR(50), password VARCHAR(50))");
            stmt.executeUpdate("INSERT INTO login VALUES ('admin', '1234')");
            
            System.out.println("Table reset. Admin user inserted.");
            con.close();
            
            System.out.println("Setup completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

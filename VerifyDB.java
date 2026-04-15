import java.sql.*;

public class VerifyDB {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            
            System.out.println("Verifying DB login table...");
            ResultSet rs = stmt.executeQuery("SELECT * FROM login");
            
            boolean found = false;
            while (rs.next()) {
                String u = rs.getString("username");
                String p = rs.getString("password");
                System.out.println("Row found -> Username: [" + u + "] | Password: [" + p + "]");
                if ("admin".equals(u) && "1234".equals(p)) {
                    found = true;
                }
            }
            
            if (!found) {
                System.out.println("Exact match for admin|1234 not found! Purging and repairing...");
                stmt.executeUpdate("DELETE FROM login");
                stmt.executeUpdate("INSERT INTO login VALUES ('admin', '1234')");
                System.out.println("Repaired! Expected row successfully inserted.");
            } else {
                System.out.println("Verification successful: admin row exists cleanly.");
            }
            
            con.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

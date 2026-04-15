import java.sql.*;

public class AddPrimaryKey {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            
            System.out.println("Adding PRIMARY KEY to login table...");
            stmt.executeUpdate("ALTER TABLE login ADD PRIMARY KEY (username)");
            System.out.println("Primary key successfully added!");
            
            con.close();
        } catch (Exception e) {
            System.out.println("Notice: " + e.getMessage());
        }
    }
}

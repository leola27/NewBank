package newbank.server.sqlite.connect.net.src;

import java.sql.*;

/**
 *
 * @author sqlitetutorial.net
 */
public class Connect {
    /**
     * Connect to a sample database
     */
    public static String connect(String Query, String Parameter,String column ) {
        Connection conn = null;
        String result = null;
        String Query2=Query+"\""+ Parameter +"\"";
        try {
            // db parameters
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:sqlite/db/NewBankDb.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();

            System.out.println("Connection to SQLite has been established.");
            ResultSet rs = stmt.executeQuery(Query2);
            result = rs.getString(column);
            //"select  * from albums limit 5"
            //while (rs.next()) {
               // String name = rs.getString("title");
               // System.out.println(name);
            //}

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return result;
    }

    //public static void FindNameInCustomersTable()
    /**
     * @param args the command line arguments
     */
    //public static void main(String[] args) {
      //  connect();
   // }
}
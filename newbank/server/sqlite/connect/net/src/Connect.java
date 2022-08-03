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
    public static String connectSelect(String Query, String column ) {
        Connection conn = null;
        String result = null;

        try {
            // db parameters
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:sqlite/db/NewBankDb.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();

            System.out.println("Connection to SQLite has been established.");
            ResultSet rs = stmt.executeQuery(Query);
            result = rs.getString(column);

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

    public static String connectInsert(String Query, String column ) {
        Connection conn = null;
        String result = null;

        try {
            // db parameters
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:sqlite/db/NewBankDb.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();

            System.out.println("Connection to SQLite has been established.");
            stmt.executeUpdate(Query);

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

}
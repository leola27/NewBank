package newbank.server.StandingOrders;

import newbank.server.Account;
import newbank.server.Customer.CustomerID;
import newbank.server.sqlite.connect.net.src.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class SOHandler {

    private StandingOrder standingOrder;
    private CustomerID customerID;

    public SOHandler(StandingOrder standingOrder, CustomerID customerID) {
        this.standingOrder = standingOrder;
        this.customerID = customerID;
    }

    public SOHandler(CustomerID customerID) {
        this.customerID = customerID;
    }

    /**
     * Creates a new standing order and updates the customers' database entry
     * @return returns a confirmation of the standing order created
     */
    public String createNewStandingOrder() {
        try {
            Connect.connectInsert("INSERT INTO StandingOrders (AccountNumber, StandingOrderID, PayeeAccNum, Amount, Frequency, StartDate) " +
                    "VALUES ( " + "\"" + new Account(customerID.getKey()).getAccountNumber() + "\",\"" + randomStandingOrderID() + "\",\"" + standingOrder.getPayee()
                    + "\",\"" + standingOrder.getAmount() + "\",\"" + standingOrder.getFrequency() + "\",\"" + LocalDate.now() + "\")", "");
            return "SUCCESS";
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    /**
     * Splits the user defined frequency and returns the integer
     * @param frequency Frequency of payments
     * @return returns the split frequency integer
     */
    private static Integer frequencyNumberConverter(String frequency) {
        return Integer.parseInt(frequency.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
    }
    /**
     * Splits the user defined frequency and returns the date type
     * @param frequency Frequency of payments
     * @return returns the split frequency date type
     */
    private static TemporalUnit frequencyDateTypeConverter(String frequency) {
        String s = frequency.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];
        return switch (s.toLowerCase()) {
            case "d" -> ChronoUnit.DAYS;
            case "w" -> ChronoUnit.WEEKS;
            case "m" -> ChronoUnit.MONTHS;
            case "y" -> ChronoUnit.YEARS;
            default -> null;
        };
    }

    /**
     * Used to check the customers current standing orders
     * @return returns all current standing orders
     */
    public String checkStandingOrders() {
        String output = "";
        ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Accounts WHERE CustomerID = " + "\"" + customerID.getKey() + "\"");
        try {
            while (rs.next()) {
                ResultSet rsTransactions = Connect.connectSelectResultSet("SELECT * FROM StandingOrders WHERE AccountNumber = " + "\"" + rs.getString("AccountNumber") + "\"");
                while (rsTransactions.next()) {
                    String AccountNum = String.format("%-20s %s", "Account Number:", rs.getString("AccountNumber") + "\n");
                    String StandingOrderID = String.format("%-20s %s", "StandingOrderID:", rsTransactions.getString(2) + "\n");
                    String PayeeAccNum = String.format("%-20s %s", "Payee Account Number:", rsTransactions.getString(3) + "\n");
                    String Amount = String.format("%-20s %s", "Amount:", rsTransactions.getString(4) + "\n");
                    String Frequency = String.format("%-20s %s %s", "Frequency:", frequencyNumberConverter(rsTransactions.getString(5)),
                            frequencyDateTypeConverter(rsTransactions.getString(5)) + "\n");
                    String StartDate = String.format("%-20s %s", "StartDate:", rsTransactions.getString(6) + "\n");
                    output += AccountNum + StandingOrderID + PayeeAccNum + Amount + Frequency + StartDate + "\n";
                }
                rsTransactions.close();
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }


    /**
     * Used to delete a user defined standing order
     * @param customerID CustomerID of current customer
     * @param input Entire user input string
     * @return returns the deleted standing order id or FAIL
     */
    public String deleteStandingOrder(CustomerID customerID, String[] input) {

        String standingOrderID = input[1];
        System.out.println(standingOrderID);
        System.out.println(new Account(customerID.getKey()).getAccountNumber());
        System.out.println();


        if (Connect.connectSelect("SELECT * FROM StandingOrders WHERE AccountNumber = " + "\"" + new Account(customerID.getKey()).getAccountNumber()
                + "\"" + " AND StandingOrderID = " + "\"" + standingOrderID + "\"", "StandingOrderID") != null) {
            Connect.connectInsert("DELETE FROM StandingOrders WHERE StandingOrderID = " + "\"" + standingOrderID + "\"", "");
            return "SUCCESS";
        }
        return "FAIL";
    }


    public static int randomStandingOrderID() {
        Random random = new Random();
        int StandingOrderID = random.nextInt(100000, 999999);
        while (Connect.connectSelect("SELECT * FROM StandingOrders WHERE StandingOrderID = " + "\"" + StandingOrderID + "\"", "StandingOrderID") != null) {
            StandingOrderID = random.nextInt(100000, 999999);
        }
        return StandingOrderID;
    }


}

package newbank.server.StandingOrders;

import newbank.server.Customer.CustomerID;
import newbank.server.sqlite.connect.net.src.Connect;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String StandingStandingOrders = Connect.connectSelect("SELECT * FROM Customers WHERE Name = " + "\"" + customerID.getKey() + "\"", "StandingOrders");
        String dbFormat;
        if (StandingStandingOrders == null) {
            dbFormat = "\"[" + standingOrder.getPayee() + "," + standingOrder.getAmount() + "," + standingOrder.getFrequency() + "," +
                    LocalDate.now() + "," + LocalDate.now().plus(frequencyNumberConverter(standingOrder.getFrequency()), frequencyDateTypeConverter(standingOrder.getFrequency())) + "]\"";
        }
        else {
            dbFormat = "\"" + StandingStandingOrders + "[" + standingOrder.getPayee() + "," + standingOrder.getAmount() + "," + standingOrder.getFrequency() + "," +
                    LocalDate.now() + "," + LocalDate.now().plus(frequencyNumberConverter(standingOrder.getFrequency()), frequencyDateTypeConverter(standingOrder.getFrequency())) + "]\"";
        }
        Connect.connectInsert("UPDATE Customers SET StandingOrders = " + dbFormat + " WHERE Name = " + "\"" + customerID.getKey() + "\"", "");
        return "Standing Order Created.\n Payee => " + standingOrder.getPayee() +
                "\n Amount => " + standingOrder.getAmount() + "\n Next Payment Due in => " + frequencyNumberConverter(standingOrder.getFrequency()) + " " + frequencyDateTypeConverter(standingOrder.getFrequency()) +
                " (" + LocalDate.now().plus(frequencyNumberConverter(standingOrder.getFrequency()), frequencyDateTypeConverter(standingOrder.getFrequency())) + ")";
    }

    /**
     * Splits the user defined frequency and returns the integer
     * @param frequency Frequency of payments
     * @return returns the split frequency integer
     */
    private Integer frequencyNumberConverter(String frequency) {
        return Integer.parseInt(frequency.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
    }
    /**
     * Splits the user defined frequency and returns the date type
     * @param frequency Frequency of payments
     * @return returns the split frequency date type
     */
    private TemporalUnit frequencyDateTypeConverter(String frequency) {
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
        String[] currentStandingOrders = Connect.connectSelect("SELECT * FROM Customers WHERE Name = " +
                "\"" + customerID.getKey() + "\"", "StandingOrders").split("\\]\\[");
        String output = "";
        for (int i = 0; i < currentStandingOrders.length; i++) {
            String[] temp = currentStandingOrders[i].split(",");
            output += "Standing Order ID : " + (i+1) + " Payee => " + temp[0] + " Amount => " + temp[1] + " Every => "
                    + frequencyNumberConverter(temp[2]) + " " + frequencyDateTypeConverter(temp[2]) + " Next Payment Due => " + temp[4] + "\n";
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

        int i = Integer.parseInt(input[1]);
        String[] currentStandingOrders = Connect.connectSelect("SELECT * FROM Customers WHERE Name = " +
                "\"" + customerID.getKey() + "\"", "StandingOrders").split("]\\[+");

        List<String> list = new ArrayList<>(Arrays.asList(currentStandingOrders));
        list.remove(i-1);
        currentStandingOrders = list.toArray(new String[0]);
        String output = "";
        for (String s : currentStandingOrders) {
            s = s.replaceAll("]", "");
            System.out.println(s);
            output += ("[" + s + "]");
        }
        System.out.println(output);
        Connect.connectInsert("UPDATE Customers SET StandingOrders = " + "\"" + output + "\"" + " WHERE Name = " + "\"" + customerID.getKey() + "\"", "");
        if (list.size() + 1> currentStandingOrders.length) {
            return "Deleted Standing Order ID : " + i;
        }
        return "FAIL";
    }

}

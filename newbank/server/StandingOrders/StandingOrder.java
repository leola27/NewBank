package newbank.server.StandingOrders;

import newbank.server.Customer.CustomerID;

public class StandingOrder {
    private String payee;
    private double amount;
    private String frequency;
    private CustomerID customerID;

    public StandingOrder(String[] words, CustomerID customerID) {
        this.payee = words[1];
        this.amount = Double.parseDouble(words[2]);
        this.frequency = words[3];
        this.customerID = customerID;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public CustomerID getCustomerID() {
        return customerID;
    }

    public void setCustomerID(CustomerID customerID) {
        this.customerID = customerID;
    }



}

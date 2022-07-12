package newbank.server;

import newbank.server.Customer.Customer;

public class Loans {

    public Loans() {
    }

    public String offerLoan(double amountRequested, Customer customer) {
        double maxAmount = customer.getAccount("Main").getBalance() * 3;
        if (amountRequested >= maxAmount) {
            return "We can offer you up to £" + maxAmount;
        }
        customer.getAccount("Main").addBalance(amountRequested);
        return amountRequested + " added to your account";
    }
}

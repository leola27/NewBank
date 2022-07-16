package newbank.server;

import newbank.server.Customer.Customer;

public class Loans {
    private double loanAmount;
    private double loanPaid;
    public Loans(double loanAmount) {
        this.loanAmount = loanAmount;
        this.loanPaid = 0;
    }

    public double getLoanAmount(){
        return loanAmount;
    }

    public double getLoanPaid(){
        return loanPaid;
    }

    public void repay(double amount){
        loanPaid += amount;
    }

    public void addLoan(double amount){
        loanAmount += amount;
    }

    public double getLoanBalance() {
        return loanAmount - loanPaid;
    }

    public String loanHistory(){
        return "Loan amount: " + loanAmount + "\nPaid: " + loanPaid + "\nRemaining balance: " + getLoanBalance();
    }



}

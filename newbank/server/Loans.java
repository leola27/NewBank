package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.Account;
import java.lang.Math;

public class Loans {
    private double loanAmount;
    private double loanPaid;
    public Loans(double balance, double monthlyInterestRate, int numberOfYears,
      double loanAmount) {
        this.loanAmount = loanAmount;
        this.loanPaid = 0;
        this.balance = getBalance();
        this.monthlylInterestRate = monthlyInterestRate;
        this.numberOfYears = numberOfYears;
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
    
    public double getMonthlyLoanRepayment() {
        double monthlyInterestRate = 0.1;
        double monthlyPayment = getAccount("Main").getBalance() * monthlyInterestRate / (1 -
        (1 / Math.pow(1 + monthlyInterestRate, numberOfYears * 12))); //calculate monthly repayment
        return monthlyPayment;    
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

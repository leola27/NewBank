package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.Account;
import java.lang.Math;
import java.time.LocalDate;

public class Loans {
    private double loanAmount;
    private double loanPaid;
    private double loanBalance;
    private double monthlyInterestRate;
    private int numberOfYears;
    
    public Loans(double loanAmount) {
        this.loanAmount = loanAmount;
        this.loanPaid = 0;
        this.monthlyInterestRate = 0.1;
        this.numberOfYears = 3;
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
    

    
//    public double getMonthlyLoanRepayment() {
//        double monthlyRepayment = balance * monthlyInterestRate / (1 -
//        (1 / Math.pow(1 + monthlyInterestRate, numberOfYears * 12))); //calculate monthly repayment
//        return monthlyRepayment;
//    }

//    public boolean repayLoanMonthly(){
//         LocalDate todaysDate = LocalDate.now();
//         if(todaysDate == LocalDate.now().withDayOfMonth( 1) && balance > getMonthlyLoanRepayment()) {// pay back at the start of every month
//         balance = getAccount("Main").getBalance()- getMonthlyLoanRepayment();
//         return true;
//           }
//        return false;
//        }


    public void addLoan(double amount){
        loanAmount += amount;
    }

    public double getLoanBalance() {
        return loanAmount - loanPaid;
    }

    public String loanHistory(){
        return "Loan amount: " + loanAmount + "\nPaid: " + loanPaid + "\nRemaining balance: " + getLoanBalance();
    }

    public void addToLoanBalance(double amount) {
        loanBalance += amount;
    }


}

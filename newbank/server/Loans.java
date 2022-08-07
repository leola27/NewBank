package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.sqlite.connect.net.src.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Loans {
    private double loanAmount;
    private double loanPaid;
    private double monthlyInterestRate;
    private int numberOfYears;
    private int accountNumber;

    public Loans(double loanAmount) {
        this.loanAmount = loanAmount;
        this.loanPaid = 0;
        this.monthlyInterestRate = 0.1;
        this.numberOfYears = 3;
    }

    public Loans(double loanAmount, int accountNumber) {
        this.loanAmount = loanAmount;
        this.loanPaid = 0;
        this.monthlyInterestRate = 0.1;
        this.numberOfYears = 3;
        this.accountNumber = accountNumber;
    }
    public Loans(int accountNumber) {
        this.loanPaid = 0;
        this.monthlyInterestRate = 0.1;
        this.numberOfYears = 3;
        this.accountNumber = accountNumber;
    }

    public double getLoanAmount(){
//        return loanAmount;
        System.out.println(accountNumber);
        return Double.parseDouble(Connect.connectSelect("SELECT * FROM Loans WHERE AccountNumber = " + "\"" + accountNumber + "\"", "CurrentLoanAmount"));
    }

    public double getLoanPaid(){
        return loanPaid;
    }

    public void repay(double amount){
        double newAmount = getLoanBalance() - amount;
//        loanPaid += amount;
        Connect.connectInsert("UPDATE Loans SET CurrentLoanAmount = " + "\"" + newAmount + "\"" + " WHERE AccountNumber = " + "\"" + accountNumber + "\"", "");

    }

    public void addLoan(double amount){
        loanAmount += amount;
    }

    public double getLoanBalance() {
//        return loanAmount - loanPaid;
        System.out.println(accountNumber);
        return Double.parseDouble(Connect.connectSelect("SELECT * FROM Loans WHERE AccountNumber = " + "\"" + accountNumber + "\"", "CurrentLoanAmount"));
    }

    public String loanHistory(){
//        return "Loan amount: " + loanAmount + "\nPaid: " + loanPaid + "\nRemaining balance: " + getLoanBalance();
        ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Loans WHERE AccountNumber = " + "\"" + accountNumber + "\"");
        try {
            if (rs != null) {
                String output = "";
                while (rs.next()) {
                    String AccountNumber = String.format("%-30s %s", "Account Number:", rs.getString(1) + "\n");
                    String OriginalLoanAmount = String.format("%-30s %s", "Original Loan:", rs.getString(2) + "\n");
                    String CurrentLoanAmount = String.format("%-30s %s", "Remaining Payment:", rs.getString(3) + "\n");
                    output += AccountNumber + OriginalLoanAmount + CurrentLoanAmount + "\n";
                }
                rs.close();
                return output;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "You don't have any current loans";
    }
    
    public double getMonthlyInterestRate(){
        return monthlyInterestRate;
    }

    public int getNumberOfYears(){
        return numberOfYears;
    }


    public void createLoan() {
        Connect.connectInsert("INSERT INTO Loans (AccountNumber, OriginalLoanAmount, CurrentLoanAmount)" +
                " VALUES (" + "\"" + accountNumber + "\",\"" + loanAmount + "\",\"" + loanAmount + "\")", "");

    }
}

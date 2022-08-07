package newbank.server.Customer;
import newbank.server.sqlite.connect.net.src.Connect;

import newbank.server.Account;
import newbank.server.Loans;
import newbank.server.Transaction.Transaction;
import newbank.server.Transaction.TransactionHistory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

public class Customer {
	private String username;
	private String password;
	private String customerID;

	public Customer(String username, String password){
		this.username = username;
		this.password = password;
//		transactions = new TransactionHistory();
	}

	public Customer(String userName, String password, String accountName, int accountNumber, double balance) {
		this.username = userName;
		this.password = password;

		Connect.connectInsert("INSERT INTO Customers (Name, Password) VALUES (\"" + userName + "\", \"" + password + "\")", "");
		this.customerID = Connect.connectSelect("SELECT * FROM Customers WHERE Name = " + "\"" + userName + "\" AND Password = " + "\"" + password + "\"", "CustomerID");

//		accounts = new HashMap<>();
//		accounts.put(accountName, new Account(accountName, accountNumber, balance, customerID));
//		transactions = new TransactionHistory();
	}

	public Customer(String customerID) {
		ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Customers WHERE CustomerID = " + "\"" + customerID + "\"");
		try {
			this.username = rs.getString("Name");
			this.password = rs.getString("Password");
			this.customerID = customerID;
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


//	public synchronized Customer newCustomer(String customerName, String password, String accountName, double openingBalance){
//		Customer customer = new Customer(customerName, password);
//		customer.addAccount(new Account(accountName, openingBalance));
//		return customer;
//	}

	public String getUsername(){
		return username;
	}

	public boolean CheckPassword(String password){
		return this.password.equals(password);
	}

	public String accountsToString(){

		return new Account(customerID).getAllAccounts();

//		String s = "";
//		for(Account a : accounts.values()){
//			s += a.toString() + "\n";
//		}
//		return s;
	}


//	public void addNewCustomerToDb(String userName, String Password)
//	{
//
//		connection.connectInsert("INSERT INTO CUSTOMERS (NAME,PASSWORD) VALUES (\""+username+"\",\""+password+"\")", userName);
//
//	}

	public boolean hasAccount(String name){
//		return accounts.containsKey(name);
		return Connect.connectSelect("SELECT * FROM Accounts WHERE AccountName = " + "\"" + name + "\"", "AccountName") != null;
	}

//	public boolean hasAccount(String name) {
//		return Connect.connectSelectResultSet("SELECT * FROM Accounts WHERE ")
//	}

//	public boolean addAccount(Account account){
//		if(hasAccount(account.getAccountName())){
//			return false;
//		}
//		accounts.put(account.getAccountName(), account);
//		return true;
//	}

	public synchronized boolean addLoan(double amountRequested) {
		ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Accounts WHERE CustomerID = " + "\"" + customerID + "\"" + " AND AccountName = 'Main'");
		try {
			if (rs.getString("AccountName") != null) {
				double maxAmount = (Double.parseDouble(rs.getString("Balance"))) * 3;
				rs.close();
				System.out.println(customerID);
				System.out.println(new Account(customerID, "Main").getAccountNumber());

				boolean hasLoan = (Connect.connectSelect("SELECT * FROM Loans WHERE AccountNumber = " + "\"" + new Account(customerID, "Main").getAccountNumber()
						+ "\"", "CurrentLoanAmount")) != null;

				int accountNumber = new Account(customerID, "Main").getAccountNumber();

				Transaction transaction = new Transaction(accountNumber, "LOAN", amountRequested, "PENDING");
				addTransaction(transaction);

				if (amountRequested >= maxAmount || hasLoan) {
					transaction.setStatus("FAIL");
					rs.close();
					return false;
				}
				getAccount("Main").deposit(amountRequested);
//				loan = new Loans(amountRequested, accountNumber);

				new Loans(amountRequested, accountNumber).createLoan();

				transaction.setStatus("SUCCESS");
				rs.close();
				return true;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public double getDebtBalance(){
//		return loan.getLoanBalance();
		return Double.parseDouble(Connect.connectSelect("SELECT * FROM Loans WHERE AccountNumber = " + "\"" +
				new Account(customerID, "Main").getAccountNumber() + "\"", "CurrentLoanAmount"));
	}


	public String loanHistory(){
//		if(loan == null){
//			return "You did not take any loan";
//		}
		return new Loans(new Account(customerID, "Main").getAccountNumber()).loanHistory();
//		return loan.loanHistory();
	}

	public boolean repay10percOfLoan(String name){
		return repayLoan(customerID, (new Loans(new Account(customerID, "Main").getAccountNumber())).getLoanAmount() / 10);
	}

	public synchronized boolean repayLoan(String customer, double amount) {
//		Transaction transaction = new Transaction("LOAN_REPAYMENT", amount, username, "NewBank");
		Transaction transaction = new Transaction(new Account(customerID, "Main").getAccountNumber(), "LOAN", amount, "PENDING");
		addTransaction(transaction);

//		Account main = getAccount(customer);
		Account account = new Account(customer);
		// amount = (main.getBalance() * loan.getMonthlyInterestRate() / (1 -
		//(1 / Math.pow(1 + loan.getMonthlyInterestRate(), loan.getNumberOfYears() * 12))));
		boolean hasLoan = Connect.connectSelect("SELECT * FROM Loans WHERE AccountNumber = " + "\"" + new Account(customerID, "Main").getAccountNumber()
				+ "\"", "AccountNumber") != null;

		if (!hasLoan || !hasAccount("Main")){
			transaction.setStatus("FAIL");
			return false;
		}
		//LocalDate todaysDate = LocalDate.now();
		// todaysDate == LocalDate.now().withDayOfMonth( 1) &&
		if (account.getBalance() > amount && (new Loans(new Account(customerID, "Main").getAccountNumber()).getLoanBalance()) >= amount) {
			account.withdraw(amount);
			new Loans(new Account(customerID, "Main").getAccountNumber()).repay(amount);
			transaction.setStatus("SUCCESS");
			return true;
		}
		transaction.setStatus("FAIL");
		return false;
		


		}

	public boolean moveMoney(double amount, String fromAccountName, String toAccountName) {
		if (hasAccount(fromAccountName) && hasAccount(toAccountName)) {
			Account fromAccount = getAccount(fromAccountName);
			Account toAccount = getAccount(toAccountName);

			if (fromAccount.getBalance() >= amount) {
				fromAccount.withdraw(amount);
				toAccount.deposit(amount);
				return true;
			}
		}
		return false;
	}


	public Account getAccount(String accountName) {
		return new Account(customerID, accountName);

//		return accounts.get(accountName);
	}

	public String getName(){
		return username;
	}

//	// print the transaction history of the customer
//	public String allTransactionsToString(){
////		return transactions.allTransactionsToString();
//		return transactions.allTransactionsToString(customerID);
//	}

	// add transaction to the transaction history of the customer
	public void addTransaction(Transaction transaction){
		new TransactionHistory().addTransaction(transaction);
	}

//	public Collection<Account> getAccounts(){
//		return accounts.values();
//	}


	// -- Kameira to complete the below functions --
	// I moved these as I believe the loan functionality belongs to the customer to do and not the actual loan class itself
	// This is where the errors were coming in as the loan class is essentially the lowest in the class hierarchy
	// As there is a repayLoan function above, shall we just amend below to calculate interest over the course of the year and add to loan original balance?


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


}

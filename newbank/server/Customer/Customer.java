package newbank.server.Customer;
import newbank.server.sqlite.connect.net.src.Connect;

import newbank.server.Account;
import newbank.server.Loans;
import newbank.server.Transaction.Transaction;
import newbank.server.Transaction.TransactionHistory;

import java.util.Collection;
import java.util.HashMap;

public class Customer {
	private String username;
	private String password;
	private HashMap<String, Account> accounts;
	Connect connection = new Connect();
	private TransactionHistory transactions;

	private Loans loan;
	public Customer(String username, String password){
		this.username = username;
		this.password = password;
		accounts = new HashMap<>();
		transactions = new TransactionHistory();
	}

	public Customer(String userName, String password, String accountName, int accountNumber, double balance){
		this.username = userName;
		this.password = password;
		accounts = new HashMap<>();
		accounts.put(accountName, new Account(accountName, accountNumber, balance));
		transactions = new TransactionHistory();
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
		String s = "";
		for(Account a : accounts.values()){
			s += a.toString() + "\n";
		}
		return s;
	}

//	public void addNewCustomerToDb(String userName, String Password)
//	{
//
//		connection.connectInsert("INSERT INTO CUSTOMERS (NAME,PASSWORD) VALUES (\""+username+"\",\""+password+"\")", userName);
//
//	}

	public boolean hasAccount(String name){
		return accounts.containsKey(name);
	}

	public boolean addAccount(Account account){
		if(hasAccount(account.getAccountName())){
			return false;
		}
		accounts.put(account.getAccountName(), account);
		return true;
	}

	public boolean addLoan(double amountRequested){
		double maxAmount = getAccount("Main").getBalance() * 3;
		if (amountRequested >= maxAmount){
			return false;
		}
		getAccount("Main").deposit(amountRequested);
		loan = new Loans(amountRequested);
		return true;
	}

	public String loanHistory(){
		if(loan == null){
			return "You did not take any loan";
		}
		return loan.loanHistory();
	}

	public boolean repayLoan(String accountName, double amount){
		if(loan == null || !hasAccount(accountName)){
			return false;
		}
		Account main = getAccount(accountName);
		if(main.getBalance() < amount || loan.getLoanBalance() < amount){
			return false;
		}
		main.withdraw(amount);
		loan.repay(amount);
		return true;
	}

	public boolean moveMoney(double amount, String fromAccountName, String toAccountName){
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
		return accounts.get(accountName);
	}

	public String getName(){
		return username;
	}

	// print the transaction history of the customer
	public String allTransactionsToString(){
		return transactions.allTransactionsToString();
	}

	// add transaction to the transaction history of the customer
	public void addTransaction(Transaction transaction){
		transactions.addTransaction(transaction);
	}

	public Collection<Account> getAccounts(){
		return accounts.values();
	}


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

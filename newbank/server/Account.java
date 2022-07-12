package newbank.server;

import newbank.server.Customer.Customer;

public class Account {

	private String accountName;
	private double openingBalance;
	private double currentBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = openingBalance;
	}

	public synchronized String newAccount(Customer customer, String accountName) {
		if (customer.hasAccount(accountName)){
			return "FAIL";
		}
		customer.addAccount(new Account(accountName, 0));
		return "SUCCESS";
	}

	public String getAccountName(){
		return accountName;
	}

	public double getBalance() {return currentBalance;}

	public void addBalance(double amount) {
		currentBalance += amount;
//		currentBalance = currentBalance + amount;
	}

	public String toString() {
		return (accountName + ": " + openingBalance);
	}





}

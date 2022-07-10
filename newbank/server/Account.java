package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private double currentBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = openingBalance;
	}

	public String getAccountName(){
		return accountName;
	}

	public double getBalance() {return currentBalance;}

	public void addBalance(double amount) {
		currentBalance = currentBalance + amount;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}



}

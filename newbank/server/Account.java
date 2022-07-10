package newbank.server;

import newbank.server.Customer.Customer;

public class Account {

	private String accountName;
	private double balance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.balance = openingBalance;
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

	public String toString() {
		return (accountName + ": " + balance);
	}

  public double getBalance() {
    return balance;
  }

  public void deposit(double amount) {
    balance += amount;
  }

  public void withdraw(double amount) {
    balance -= amount;
  }
}

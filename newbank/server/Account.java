package newbank.server;
import java.lang.Exception;
import newbank.server.Customer.Customer;

public class Account {

	private String accountName;
	private double openingBalance;
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


	public void setBalance(double newBalance) {
   		this.balance = newBalance;
	}

	private void checkPositiveAmount(double amount) throws Exception {
  		if (amount < 0) {
   		throw new Exception("Unable to process negative amount: " + amount);
	 }
	}

	private void checkSufficientFunds(double amount) throws Exception {
  		if (balance < amount) {
    		throw new Exception("Not enough funds to withdraw: " + amount);
  	 }
	}

	public void transferMoney(Account fromAccount, Account toAccount, double amount){ 
    		if(fromAccount.getBalance() > 0) {
        	toAccount.setBalance(toAccount.balance += amount);
        	fromAccount.setBalance(fromAccount.balance -= amount);
   		} else {
        	System.out.println("Transfer Failed, not enough funds to complete transcation");
    	 }
	}

// I feel like this method can be used to transfer to person to person or adapted too

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

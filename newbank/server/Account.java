package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private double balance; 

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.balance = openingBalance;
	}

	public String getAccountName(){
		return accountName;
	}
	
	public String toString() {
		return (accountName + ": " + balance);
	}
	


	public double getBalance(){
		return balance ;
	}

	public void setBalance(double newBalance) {
   		this.balance = newBalance;
	}

	private void checkPositiveAmount(double amount) { throws NegativeAmountException 
  		if (amount < 0) {
   		throw new NegativeAmountException("Unable to process negative amount: " + amount);
	 }
	}

	private void checkSufficientFunds(double amount) {
  		if (balance.compareTo(amount) < 0) {
    		throw new InsufficientBalanceException("Not enough funds to withdraw: " + amount);
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

}

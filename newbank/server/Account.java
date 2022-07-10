package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}

	public String getAccountName(){
		return accountName;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}
	
	// may be worth changing openingBalance too just balance


	public double getOpeningBalance(){
	return openingBalance ;
	}

	public void setBalance(double newBalance) {
   	this.openingBalance = newBalance;
	}

	private void checkPositiveAmount(double amount) {
  	if (amount.asBigDecimal().compareTo(BigDecimal.ZERO) < 0) {
   	throw new NegativeAmountException("Unable to process negative amount: " + amount);
	 }
	}

	private void checkSufficientFunds(double amount) {
 	if (openingBalance.compareTo(amount) < 0) {
   	throw new InsufficientBalanceException("Not enough funds to withdraw: " + amount);
	 }
	}

	public void transferMoney(Account thisAccount, Account toAccount, double amount){
    	if(thisAccount.getOpeningbalance() > 0) {
        toAccount.setBalance(toAccount.openingBalance += amount);
        thisAccount.setBalance(this.openingBalance -= amount);
   	} else {
        System.out.println("Transfer Failed, not enough funds to complete transcation");
    	 }
	}

// I feel like this method can be used to transfer to person to person or adapted too

}

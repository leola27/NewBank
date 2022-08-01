package newbank.server;
import java.lang.Exception;
import java.math.BigInteger;

import newbank.server.Customer.Customer;

public class Account {

	public static final int SORT_CODE = 653512;
	public static final String BANK_CODE = "NEWB";
	public static final String BANK_CODE_NUMBERS = "145232";
	public static final String GB_00_IN_NUMBERS = "7200";
	private String accountName;
	private int accountNumber;
	private double openingBalance;
	private double balance;

	public String swiftCode(){
		return BANK_CODE + "GB22";
	}

	public String IBAN(){
		// algorithm for calculating IBAN check digit
		// https://en.wikipedia.org/wiki/International_Bank_Account_Number#Generating_IBAN_check_digits
		BigInteger bigInteger = new BigInteger(BANK_CODE_NUMBERS + accountNumber +
				SORT_CODE + GB_00_IN_NUMBERS); // big integer is needed because the number is very long
		int checkDigits = 98 - bigInteger.mod(BigInteger.valueOf(97)).intValue();
		return "GB" + checkDigits + BANK_CODE + accountNumber + SORT_CODE;
	}

	public Account(String accountName, int accountNumber, double openingBalance) {
		this.accountName = accountName;
		this.balance = openingBalance;
		this.accountNumber = accountNumber;
	}

	public static synchronized String newAccount(Customer customer, String accountName, int accountNumber) {
		if (customer.hasAccount(accountName)){
			return "FAIL";
		}
		customer.addAccount(new Account(accountName,accountNumber, 0));
		return "SUCCESS";
	}

	public String getAccountName(){
		return accountName;
	}

	public int getAccountNumber(){
		return accountNumber;
	}

	public String toString() {
		return ("Account name: \t" + accountName + "\n"+ "Account number: " + accountNumber + "\n"+ "Sort code: \t\t" + SORT_CODE
				+ "\n"+ "IBAN:\t\t\t"+ IBAN() + "\n" + "SWIFT: \t\t\t" + swiftCode() + "\n"+ "Balance: \t\t" + balance + "\n");
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

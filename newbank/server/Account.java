package newbank.server;
import java.lang.Exception;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import newbank.server.Customer.Customer;
import newbank.server.Customer.CustomerID;
import newbank.server.sqlite.connect.net.src.Connect;

public class Account {

	public static final int SORT_CODE = 653512;
	public static final String BANK_CODE = "NEWB";
	public static final String BANK_CODE_NUMBERS = "145232";
	public static final String GB_00_IN_NUMBERS = "7200";
	private String customerID;
	private int accountNumber;
	private String accountName;
	private double openingBalance;
	private double balance;

	public static String swiftCode() {
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

	public Account(String accountName, int accountNumber, double openingBalance, String customerID) {
		this.accountName = accountName;
		this.balance = openingBalance;
		this.accountNumber = accountNumber;
		Connect.connectInsert("INSERT INTO Accounts (CustomerID, AccountNumber, AccountName, IBAN, Swift, Balance) " +
				"VALUES ( \""  + customerID + "\", \"" + accountNumber + "\", \"" + accountName + "\", \"" + IBAN() + "\", \"" + swiftCode() + "\", \"" + openingBalance +"\")", "");
	}

	public Account(String customerID, String accountName) {
		ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Accounts WHERE CustomerID = " + "\"" + customerID + "\"" + " AND AccountName = " + "\"" + accountName + "\"");
		try {
			this.customerID = customerID;
			this.accountName = accountName;
			this.accountNumber = Integer.parseInt(rs.getString(2));
			rs.close();
		}
		catch (SQLException ignored) {}

	}

	public Account(String customerID) {
		ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Accounts WHERE CustomerID = " + "\"" + customerID + "\"");
		try {
			this.customerID = customerID;
			this.accountNumber = Integer.parseInt(rs.getString(2));
			this.accountName = rs.getString(3);
			this.balance = Double.parseDouble(rs.getString(6));
			rs.close();
		}
		catch (SQLException ignored) {}

	}

//	public static synchronized String newAccount(Customer customer, String accountName, int accountNumber) {
//		if (customer.hasAccount(accountName)){
//			return "FAIL";
//		}
//		customer.addAccount(new Account(accountName,accountNumber, 0));
//		return "SUCCESS";
//	}

	public static synchronized String newAccount(CustomerID customerID, String accountName, int accountNumber) {
		if (Connect.connectSelect("SELECT * FROM Accounts WHERE CustomerID = " + "\""
				+ customerID.getKey() +"\"" + "AND AccountName = " + "\"" + accountName + "\"", "AccountName") != null) {
			return "FAIL";
		}
		new Account(accountName, accountNumber, 0, customerID.getKey());
		return "SUCCESS";

//		Connect.connectInsert("INSERT INTO Accounts (CustomerID, AccountNumber, AccountName, Balance) " +
//				"VALUES " + "(\"" + customerID.getKey() + "\", \"" + accountNumber + "\", \"" + accountName + "\", " + "0)","");
//		return "SUCCESS";
	}

	public String getAccountName() {
//		return accountName;
		return Connect.connectSelect("SELECT * FROM Accounts WHERE AccountNumber = " + "\"" + accountNumber + "\"", "AccountName");
	}

	public int getAccountNumber(){
		return accountNumber;
	}

//	public String toString() {
//		return ("Account name: \t" + accountName + "\n"+ "Account number: " + accountNumber + "\n"+ "Sort code: \t\t" + SORT_CODE
//				+ "\n"+ "IBAN:\t\t\t"+ IBAN() + "\n" + "SWIFT: \t\t\t" + swiftCode() + "\n"+ "Balance: \t\t" + balance + "\n");
//	}

	public String getAllAccounts() {
		String output = "";
		ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Accounts WHERE CustomerID = " + "\"" + customerID + "\"");
		try {
			while (rs.next()) {
				String AccountNumber = String.format("%-20s %s", "Account Number:", rs.getString(2) + "\n");
				String AccountName = String.format("%-20s %s", "Account Name:", rs.getString(3) + "\n");
				String IBAN = String.format("%-20s %s", "IBAN:", rs.getString(4) + "\n");
				String Swift = String.format("%-20s %s", "Swift:", rs.getString(5) + "\n");
				String Balance = String.format("%-20s %s", "Balance:", rs.getString(6) + "\n");
				output += AccountName + AccountNumber +IBAN + Swift + Balance + "\n";
			}
			rs.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return output;
	}


	public void setBalance(double newBalance) {
//   		this.balance = newBalance;
		   Connect.connectInsert("UPDATE Accounts SET Balance = " + "\"" + newBalance + "\"" + "WHERE AccountNumber = " + "\"" + accountNumber + "\"", "");
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

	public void transferMoney(Account fromAccount, Account toAccount, double amount) {
    		if(fromAccount.getBalance() > 0) {
        	toAccount.setBalance(toAccount.balance += amount);
        	fromAccount.setBalance(fromAccount.balance -= amount);
   		} else {
        	System.out.println("Transfer Failed, not enough funds to complete transcation");
    	 }
	}

// I feel like this method can be used to transfer to person to person or adapted too

	public double getBalance() {
		return Double.parseDouble(Connect.connectSelect("SELECT * FROM Accounts WHERE AccountNumber = " + "\"" + accountNumber + "\"", "Balance"));
//		return balance;
	}

	public void deposit(double amount) {
		double newBalance = amount + Double.parseDouble(Connect.connectSelect("SELECT * FROM Accounts WHERE AccountNumber = " + "\"" + accountNumber + "\"", "Balance"));
		Connect.connectInsert("UPDATE Accounts SET Balance = " + "\"" + newBalance + "\"" + "WHERE AccountNumber = " + "\"" + accountNumber + "\"", "");
//		balance += amount;
	}

	public void withdraw(double amount) {
		double newBalance = Double.parseDouble(Connect.connectSelect("SELECT * FROM Accounts WHERE AccountNumber = " + "\"" + accountNumber + "\"", "Balance")) - amount;
		Connect.connectInsert("UPDATE Accounts SET Balance = " + "\"" + newBalance + "\"" + "WHERE AccountNumber = " + "\"" + accountNumber + "\"", "");
//		balance -= amount;
	}
}

package newbank.server.Transaction;

import newbank.server.sqlite.connect.net.src.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class TransactionHistory {
	private ArrayList<Transaction> transactions;

	public TransactionHistory() {
		transactions = new ArrayList<Transaction>();
	}

	public synchronized void addTransaction(Transaction transaction) {
//		transactions.add(transaction);

		if (transaction.getType().equals("LOAN")) {
			Connect.connectInsert("INSERT INTO Transactions (AccountNumber, TransactionID, Type, Amount, Status) VALUES (" + "\"" + transaction.getAccountNumber() + "\",\"" +
					transaction.getTransactionID() + "\",\"" + transaction.getType() + "\",\"" + transaction.getValue() + "\",\"" + transaction.getStatus() + "\")", "");
		}
		else {
			Connect.connectInsert("INSERT INTO Transactions (AccountNumber, TransactionID, Type, Amount, Status, PayeeAccNum) VALUES (" + "\""
					+ transaction.getAccountNumber() + "\",\"" + transaction.getTransactionID() + "\",\"" + transaction.getType() + "\",\"" + transaction.getValue() +
					"\",\"" + transaction.getStatus() + "\",\"" + transaction.getPayeeAccNumber() + "\")", "");

		}
	}

	public ArrayList<Transaction> getTransactionsByType(String type) {
		ArrayList<Transaction> transactionsByType = new ArrayList<Transaction>();
		for (Transaction transaction : transactions) {
			if (transaction.getType().equals(type)) {
				transactionsByType.add(transaction);
			}
		}
		return transactionsByType;
	}

	public ArrayList<Transaction> getTransactionsByOrigin(String origin) {
		ArrayList<Transaction> transactionsByOrigin = new ArrayList<Transaction>();
		for (Transaction transaction : transactions) {
			if (transaction.getOrigin().equals(origin)) {
				transactionsByOrigin.add(transaction);
			}
		}
		return transactionsByOrigin;
	}

	public String allTransactionsToString() {
		String s = "";
		for (Transaction transaction : transactions) {
			s += transaction.toString() + "\n=========\n";
		}
		return s;
	}

	/*
		public static String getAllAccounts(String customerID) {
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
	 */


	public String allTransactionsToString(String customerID) {
		String output = "";
		ResultSet rs = Connect.connectSelectResultSet("SELECT * FROM Accounts WHERE CustomerID = " + "\"" + customerID + "\"");
		try {
			while (rs.next()) {
				ResultSet rsTransactions = Connect.connectSelectResultSet("SELECT * FROM Transactions WHERE AccountNumber = " + "\"" + rs.getString("AccountNumber") + "\"");
				while (rsTransactions.next()) {
					String AccountNum = String.format("%-20s %s", "Account Number:", rs.getString("AccountNumber") + "\n");
					String TransactionID = String.format("%-20s %s", "TransactionID:", rsTransactions.getString(2) + "\n");
					String Type = String.format("%-20s %s", "Type:", rsTransactions.getString(3) + "\n");
					String Amount = String.format("%-20s %s", "Amount:", rsTransactions.getString(4) + "\n");
					String Status = String.format("%-20s %s", "Status:", rsTransactions.getString(5) + "\n");
					String PayeeAccNum = String.format("%-20s %s", "PayeeAccNum:", rsTransactions.getString(6) + "\n");
					output += AccountNum + TransactionID + Type + Amount + Status + PayeeAccNum + "\n";
				}
				rsTransactions.close();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return output;

		//		String s = "";
//		for (Transaction transaction : transactions) {
//			s += transaction.toString() + "\n=========\n";
//		}
//		return s;
	}

	public static int transactionID() {
		Random random = new Random();
		int TransactionID = random.nextInt(10000000, 99999999);
		while (Connect.connectSelect("SELECT * FROM Transactions WHERE TransactionID = " + "\"" + TransactionID + "\"", "TransactionID") != null) {
			TransactionID = random.nextInt(10000000, 99999999);
		}
		return TransactionID;
	}

}

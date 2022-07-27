package newbank.server.Transaction;

import java.util.ArrayList;

public class TransactionHistory {
	private ArrayList<Transaction> transactions;

	public TransactionHistory() {
		transactions = new ArrayList<Transaction>();
	}

	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
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
}

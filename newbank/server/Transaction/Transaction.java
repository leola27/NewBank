package newbank.server.Transaction;

import newbank.server.sqlite.connect.net.src.Connect;

import java.util.Random;

public class Transaction {
	private String type;
	private double value;
	private String status;
	private String origin;
	private String target;
	private int accountNumber;
	private int payeeAccNumber;
	private int transactionID;

	public Transaction(String type, double value, String origin, String target) {
		this.type = type;
		this.value = value;
		this.origin = origin;
		this.target = target;
	}

//AccountNumber, TransactionID, Type, Amount, Status, PayeeAccNum
	public Transaction(int accountNumber, String type, double value, String status, int payeeAccNumber) {
		this.accountNumber = accountNumber;
		this.type = type;
		this.value = value;
		this.status = status;
		this.payeeAccNumber = payeeAccNumber;
		this.transactionID = TransactionHistory.transactionID();
	}
	public Transaction(int accountNumber, String type, double value, String status, int payeeAccNumber, int transactionID) {
		this.accountNumber = accountNumber;
		this.type = type;
		this.value = value;
		this.status = status;
		this.payeeAccNumber = payeeAccNumber;
		this.transactionID = transactionID;
	}

	public Transaction(int accountNumber, String type, double value, String status) {
		this.accountNumber = accountNumber;
		this.type = type;
		this.value = value;
		this.status = status;
		this.transactionID = TransactionHistory.transactionID();
	}

	public int getPayeeAccNumber() {
		return payeeAccNumber;
	}

	public void setPayeeAccNumber(int payeeAccNumber) {
		this.payeeAccNumber = payeeAccNumber;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public String getType() {
		return type;
	}

	public double getValue() {
		return value;
	}

	public String getStatus() {
		return status;
	}

	public String getOrigin() {
		return origin;
	}

	public String getTarget() {
		return target;
	}

	public void setStatus(String status) {
		this.status = status;
		Connect.connectInsert("UPDATE Transactions SET Status = " + "\"" + status + "\"" + "WHERE TransactionID = " + "\"" + transactionID + "\"", "");
	}

	public int getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public String toString() {
		return "Type: " + type + "\nValue: " + value + "\nStatus: " + status + "\nOrigin: " + origin + "\nTarget: " + target;
	}


}
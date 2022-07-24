package newbank.server.Transaction;

import java.util.HashMap;

public class TransactionHistory {
  private HashMap<String, Transaction> transactions;

  public TransactionHistory() {
    transactions = new HashMap<>();
  }

  public void addTransaction(Transaction transaction) {
    transactions.put(transaction.getType(), transaction);
  }

  public HashMap<String, Transaction> getTransactionsByType(String type) {
    HashMap<String, Transaction> result = new HashMap<>();
    for(Transaction t : transactions.values()) {
      if(t.getType().equals(type)) {
        result.put(t.getType(), t);
      }
    }

    printTransactions(result);

    return result;
  }

  public HashMap<String, Transaction> getTransactionsByOrigin(String origin) {
    HashMap<String, Transaction> result = new HashMap<>();
    for(Transaction t : transactions.values()) {
      if(t.getOrigin().equals(origin)) {
        result.put(t.getType(), t);
      }
    }

    printTransactions(result);

    return result;
  }

  public String allTransactionsToString() {
    String s = "";
    for (Transaction t : transactions.values()) {
      s += t.toString() + "\n=======\n";
    }
    return s;
  }

  public void printTransactions(HashMap<String, Transaction> transactions) {
    for(Transaction t : transactions.values()) {
      System.out.println(t.toString());
    }
  }

}

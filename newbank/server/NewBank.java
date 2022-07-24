package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.Customer.CustomerID;
import newbank.server.sqlite.connect.net.src.Connect;
import java.util.Objects;
import newbank.server.Transaction.Transaction;

import java.util.HashMap;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String, Customer> customers;
	Connect connection = new Connect();
	private boolean isValidCustomer;

	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	private void addTestData() {
		customers.put("bhagy", new Customer("bhagy", "1234", "Main", 1000.0));
		customers.put("christina", new Customer("christina", "abcd", "Main", 1500.0));
		customers.put("john", new Customer("john", "password", "Main", 250.0));
	}

	public static NewBank getBank() {
		return bank;
	}

	//public synchronized boolean isCustomer(String userName)
	public boolean isValidCustomerCheck(String userName)
	{
		String query="SELECT NAME FROM CUSTOMERS WHERE NAME="+"\""+ userName +"\"";
		String result = connection.connectSelect(query,"Name");
		isValidCustomer= Objects.nonNull(result);
		return isValidCustomer;
	}

	public synchronized CustomerID checkLogInDetails(String userName, String givenPassword)
	{
		if (isValidCustomer)
		{
			//Customer customer = customers.get(userName);
			String query="SELECT PASSWORD FROM CUSTOMERS WHERE NAME="+"\""+ userName +"\"";
			String passwordFromDb = connection.connectSelect(query,  "Password");
			if (passwordFromDb.equals(givenPassword)) {
				return new CustomerID(userName);
			}
		}
//		if(customers.containsKey(userName)) {
//			Customer customer = customers.get(userName);
//			if(customer.CheckPassword(givenPassword)) {
//				return new CustomerID(userName);
//			}
//		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(isValidCustomer) {
		//if(customers.containsKey(customer.getKey())) {
			String[] words = request.split(" ");
			if(words.length == 0){
				return "FAIL";
			}
			switch(words[0]) {
				case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
				case "NEWACCOUNT": return accountCreationReview(words, customer);
				case "REQUESTLOAN": return loanReview(words, customer);
				case "LOANHISTORY" : return loanHistory(words, customer);
				case "PAY": return pay(words, customer);
				case "MOVE": return move(words, customer);
				case "TRANSACTIONHISTORY": return printTransactionHistory(customer);
//					return initialiseOfferLoan(words, customer);
				default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private synchronized String accountCreationReview(String[] words, CustomerID customer) {
		try {
			return new Account(words[1], 0).newAccount(customers.get(customer.getKey()), words[1]);
		}
		catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}
	private synchronized String loanHistory(String[] words, CustomerID customerID) {
		try {
			Customer customer = customers.get(customerID.getKey());
			return customer.loanHistory();
		}
		catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}


	private synchronized String loanReview(String[] words, CustomerID customerID) {
		try {
			double amountRequested = Double.parseDouble(words[1]);
			Customer customer = customers.get(customerID.getKey());
			if(customer.addLoan(amountRequested)){
				return "SUCCESS";
			}
			else {
				return "FAIL";
			}
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String pay(String[] words, CustomerID customer) {
		try {
			String receivingCustomer = words[1];
			double amount = Double.parseDouble(words[2]);

			Customer sender = customers.get(customer.getKey());
			Customer receiver = customers.get(receivingCustomer);
      Transaction transaction = new Transaction("PAY", amount, sender.getName(), receiver.getName());

			if (sender.hasAccount("Main") && receiver.hasAccount("Main")) {
        Account senderMain = sender.getAccount("Main");
				Account receiverMain = receiver.getAccount("Main");

				if (senderMain.getBalance() >= amount) {
					senderMain.withdraw(amount);
					receiverMain.deposit(amount);

          transaction.setStatus("SUCCESS");

					return "SUCCESS";
				} else {
          transaction.setStatus("FAIL");
        }

        sender.addTransaction(transaction);
        receiver.addTransaction(transaction);
			}
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			e.printStackTrace();
		}

		return "FAIL";
	}

	private String move(String[] words, CustomerID customer) {
		try {
			double amount = Double.parseDouble(words[1]);
			String fromAccountName = words[2];
			String toAccountName = words[3];

			Customer sender = customers.get(customer.getKey());
			if (toAccountName.equalsIgnoreCase("LOAN")) {
				if (sender.repayLoan(fromAccountName, amount)) {
					return "SUCCESS";
				}
			}
			else if (sender.moveMoney(amount, fromAccountName, toAccountName)) {
				return "SUCCESS";
			}

		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			e.printStackTrace();
		}

		return "FAIL";
	}
  private String printTransactionHistory(CustomerID customerID) {
    try {
      Customer customer = customers.get(customerID.getKey());
      return customer.allTransactionsToString();
    } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
      e.printStackTrace();
    }
    return "FAIL";
  }

}

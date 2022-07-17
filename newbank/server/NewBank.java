package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.Customer.CustomerID;

import java.util.HashMap;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String, Customer> customers;

	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	private void addTestData() {
		customers.put("Bhagy", new Customer("Bhagy", "1234", "Main", 1000.0));
		customers.put("Christina", new Customer("Christina", "abcd", "Main", 1500.0));
		customers.put("John", new Customer("John", "password", "Main", 250.0));
	}

	public static NewBank getBank() {
		return bank;
	}

	public synchronized boolean isCustomer(String userName){
		return customers.containsKey(userName);
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			Customer customer = customers.get(userName);
			if(customer.CheckPassword(password)) {
				return new CustomerID(userName);
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
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
			if(customer.addLoad(amountRequested)){
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
			if (receivingCustomer.equals("LOAN")) {
				if(sender.repayLoan(amount)){
					return "SUCCESS";
				}
				return "FAIL";
			}
			Customer receiver = customers.get(receivingCustomer);

			if (sender.hasAccount("Main") && receiver.hasAccount("Main")) {
				Account senderMain = sender.getAccount("Main");
				Account receiverMain = receiver.getAccount("Main");

				if (senderMain.getBalance() >= amount) {
					senderMain.withdraw(amount);
					receiverMain.deposit(amount);

					return "SUCCESS";
				}
			}
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			e.printStackTrace();
		}

		return "FAIL";
	}

}

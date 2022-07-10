package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		newCustomer("Bhagy", "1234", "Main", 1000.0);
		newCustomer("Christina", "abcd", "Saving", 1500.0);
		newCustomer("John","password", "Checking", 250.0);
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
				case "SHOWMYACCOUNTS" :
					return showMyAccounts(customer);
				case "NEWACCOUNT":
					if(words.length == 2) {
						String accountName = words[1];

						if (newAccount(customer, accountName)) {
							return "SUCCESS";
						}
					}
					return "FAIL";
				case "REQUESTLOAN":
					if(words.length == 2) {
						double amountRequested = Double.parseDouble(words[1]);
						return offerLoan(customer, amountRequested);
					}
					return "FAIL";
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	public synchronized void newCustomer(String customerName, String password, String accountName, double openingBalance){
		Customer customer = new Customer(customerName, password);
		customer.addAccount(new Account(accountName, openingBalance));
		customers.put(customerName, customer);
	}

	private synchronized boolean newAccount(CustomerID customerID, String accountName){
		Customer customer = customers.get(customerID.getKey());
		if(customer.hasAccount(accountName)){
			return false;
		}
		Account account = new Account(accountName, 0);
		return customer.addAccount(account);
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String offerLoan(CustomerID customer, Double amountRequested) {
		double customerBalance = customers.get(customer.getKey()).getAccount("Main").getBalance();
		double maxAmount = customerBalance * 3;
		if(amountRequested > maxAmount) {
			return "We can offer you up to £" + maxAmount;
		}
		customers.get(customer.getKey()).getAccount("Main").addBalance(amountRequested);
		return amountRequested + " added to your account";
	}
}

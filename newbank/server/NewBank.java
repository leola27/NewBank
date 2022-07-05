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
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
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
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}

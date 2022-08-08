package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.Customer.CustomerID;
import newbank.server.StandingOrders.SOHandler;
import newbank.server.StandingOrders.StandingOrder;
import newbank.server.Transaction.TransactionHistory;
import newbank.server.sqlite.connect.net.src.Connect;
import newbank.server.Transaction.Transaction;

import java.util.HashMap;
import java.util.Random;

public class NewBank {

	private static final NewBank bank = new NewBank();
	Connect connection = new Connect();
	private boolean isValidCustomer;

	private NewBank() {
		isValidCustomer = true; // Temporary solution for testing
	}

	public static NewBank getBank() {
		return bank;
	}

	public synchronized boolean isCustomer(String userName) {
//		return customers.containsKey(userName);
		return Connect.connectSelect("SELECT * from Customers WHERE Name = " + "\"" + userName + "\"", "CustomerID") != null;
	}


	// public boolean isValidCustomerCheck(String userName)
	// {
	// 	String query="SELECT NAME FROM CUSTOMERS WHERE NAME="+"\""+ userName +"\"";
	// 	String result = connection.connectSelect(query,"Name");
	// 	isValidCustomer= Objects.nonNull(result);
	// 	return isValidCustomer;
	// }

	public synchronized CustomerID checkLogInDetails(String userName, String givenPassword) {
		String dbPassword = Connect.connectSelect("SELECT * from Customers WHERE Name =" + "\"" + userName + "\"", "Password");
		if (dbPassword.equals(givenPassword)) {
			return new CustomerID(Connect.connectSelect("SELECT * from Customers WHERE Name = " + "\"" + userName + "\"", "CustomerID"));
		}
		return null;

		// if (isValidCustomer)
		// {
		// 	//Customer customer = customers.get(userName);
		// 	String query="SELECT PASSWORD FROM CUSTOMERS WHERE NAME="+"\""+ userName +"\"";
		// 	String passwordFromDb = connection.connectSelect(query,  "Password");
		// 	if (passwordFromDb.equals(givenPassword)) {
		// 		return new CustomerID(userName);
		// 	}
		// }
//		if(customers.containsKey(userName)) {
//			Customer customer = customers.get(userName);
//			if(customer.CheckPassword(givenPassword)) {
//				return new CustomerID(userName);
//			}
//		}
//		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
//		if(isValidCustomer) {
		//if(customers.containsKey(customer.getKey())) {
			String[] words = request.split(" ");
			if(words.length == 0){
				return "PLEASE ENTER A COMMAND";
			}
			switch(words[0].toUpperCase()) {
				case "SHOWMYACCOUNTS" : return showMyAccounts(customer); //DB integration done
				case "NEWACCOUNT": return accountCreationReview(words, customer); //DB integration done
				case "REQUESTLOAN": return loanReview(words, customer); //DB integration done
				case "REPAYLOAN": return repay10percOfLoan(words, customer); //DB integration done
				case "LOANHISTORY" : return loanHistory(words, customer); //DB integration done
				case "PAY": return pay(words, customer);
				case "MOVE": return move(words, customer);
				case "TRANSACTIONHISTORY": return printTransactionHistory(customer); //DB integration done
				case "STANDINGORDER" : return createStandingOrder(words, customer);
				case "CHECKSTANDINGORDERS" : return new SOHandler(customer).checkStandingOrders();
				case "DELETESTANDINGORDER" : return new SOHandler(customer).deleteStandingOrder(customer, words);
//					return initialiseOfferLoan(words, customer);
				default : return "UNKNOWN COMMAND PLEASE TRY AGAIN";
			}
//		}
//		return "FAIL";
	}

	public int newAccountNumber(){
		Random random = new Random();
		int accountNumber = random.nextInt(10000000, 99999999);
		while (Connect.connectSelect("SELECT * FROM Accounts WHERE AccountNumber = " + "\"" + accountNumber + "\"", "AccountNumber") != null) {
			accountNumber = random.nextInt(10000000, 99999999);
		}
		return accountNumber;


//		boolean usedAccountNumber = false;
//		int accountNumber;
//		do{
//			// generate a random account number
//			Random random = new Random();
//			accountNumber = random.nextInt(10000000, 99999999);
//			// check if the account number is taken
//			for(Customer customer1: customers.values()){
//				for(Account account: customer1.getAccounts()){
//					if(account.getAccountNumber() == accountNumber){
//						usedAccountNumber = true;
//						break;
//					}
//				}
//			}
//
//		}
//		while (usedAccountNumber);  // if it is taken, try another random account number
//		return accountNumber;
	}

	private synchronized String accountCreationReview(String[] words, CustomerID customer) {
		try {
			return Account.newAccount(customer, words[1], newAccountNumber());
//			return Account.newAccount(customers.get(customer.getKey()), words[1], newAccountNumber());
		}
		catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}
	private synchronized String loanHistory(String[] words, CustomerID customerID) {
		try {
//			Customer customer = customers.get(customerID.getKey());
//			return customer.loanHistory();
			return new Customer(customerID.getKey()).loanHistory();
		}
		catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}

	private synchronized String repay10percOfLoan(String[] words, CustomerID customerID) {
//		Customer customer = customers.get(customerID.getKey());
		Customer customer = new Customer(customerID.getKey());
//		Transaction transaction = new Transaction("LOAN", customer.getDebtBalance() / 10, customer.getName(), "NewBank");
		Transaction transaction = new Transaction(new Account(customerID.getKey(), "Main").getAccountNumber(), "LOAN", customer.getDebtBalance() / 10, "PENDING");

		if (customer.repay10percOfLoan("Main")) {
			transaction.setStatus("SUCCESS");
			customer.addTransaction(transaction);
			return "SUCCESS";
		}

		transaction.setStatus("FAIL");
		customer.addTransaction(transaction);
		return "FAIL";
	}

	private synchronized String loanReview(String[] words, CustomerID customerID) {
		double amountRequested = Double.parseDouble(words[1]);
//		Customer customer = customers.get(customerID.getKey());
		Customer customer = new Customer(customerID.getKey());
//		Transaction transaction = new Transaction("LOAN", amountRequested, "NewBank", customer.getName());
		Transaction transaction = new Transaction(new Account(customerID.getKey(), "Main").getAccountNumber(), "LOAN", amountRequested, "PENDING");
		try {
			if(customer.addLoan(amountRequested)){
				transaction.setStatus("SUCCESS");
				customer.addTransaction(transaction);

				return "SUCCESS";
			}
			else {
				return "FAIL";
			}
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
			e.printStackTrace();
		}

		transaction.setStatus("FAIL");
		customer.addTransaction(transaction);
		return "FAIL";
	}








	private String showMyAccounts(CustomerID customerID) {
		return new Account(customerID.getKey()).getAllAccounts();


//		return (customers.get(customer.getKey())).accountsToString();
	}

	private boolean isValidIBAN(String string){
		// very basic validation that can be improved
		if (string.length() < 15 || string.length() > 32){
			return false;
		}
		if (string.charAt(0) < 'A' || string.charAt(0) > 'Z'){
			return false;
		}
		if (string.charAt(1) < 'A' || string.charAt(1) > 'Z'){
			return false;
		}
		if (string.charAt(2) < '0' || string.charAt(2) > '9'){
			return false;
		}
		if (string.charAt(3) < '0' || string.charAt(3) > '9'){
			return false;
		}
		return true;
	}


	private String pay(String[] words, CustomerID customer) {
		try {
			String receivingCustomer = words[1];
			double amount = Double.parseDouble(words[2]);

//			Customer sender = customers.get(customer.getKey());
			Customer sender = new Customer(customer.getKey());

			if (sender.hasAccount("Main") ) {
				Account senderMain = sender.getAccount("Main");
				Customer receiver = null;
				Account receiverAccount = null;
				// first, check if the receivingCustomer matches a customer name
//				if(customers.containsKey(receivingCustomer)){

				if((Connect.connectSelect("SELECT * FROM Customers WHERE Name = " + "\"" + receivingCustomer + "\"", "Name"))!= null){
//					receiver =  customers.get(receivingCustomer.toLowerCase());
					receiver = new Customer(Connect.connectSelect("SELECT * FROM Customers WHERE Name = " + "\"" + receivingCustomer + "\"", "CustomerID"));
					if(receiver.hasAccount("Main")){
						receiverAccount = receiver.getAccount("Main");
					}
				}
				else {
					// if not, check if it matches an account number of an IBAN
//					for(Customer customer1: customers.values()){
//						for(Account account: customer1.getAccounts()){
//							// comparing both the account number and IBAN and see if one of them match
//							if(String.valueOf(account.getAccountNumber()).equals(receivingCustomer) || account.IBAN().equals(receivingCustomer)){
//								receiver = customer1;
//								receiverAccount = account;
//								break;
//							}
//						}

					// if we found the receiver already, there is no point to continue looping, so break
//					if(receiverAccount != null){
//						break;
//					}
//				}

					String findCustomer = Connect.connectSelect("SELECT * FROM Accounts WHERE AccountNumber = " + "\"" + receivingCustomer + "\"", "CustomerID");
					String IBANAccount = Connect.connectSelect("SELECT * FROM Accounts WHERE IBAN = " + "\"" + receivingCustomer + "\"", "CustomerID");
					if (findCustomer != null) {
						receiver = new Customer(findCustomer);
					}
					if (IBANAccount != null) {
						receiverAccount = new Account(IBANAccount, "Main");
						receiver = new Customer(IBANAccount);
					}

				}

				if(receiverAccount != null && receiver != sender) {
//					Transaction transaction = new Transaction("PAY", amount, sender.getName(), receiver.getName());

//					Transaction transaction = new Transaction("PAY", amount, sender.getName(), receiver.getName());
					Transaction transaction = new Transaction(sender.getAccount("Main").getAccountNumber(), "PAY", amount, "PENDING", receiverAccount.getAccountNumber());


					Account receiverMain = receiver.getAccount("Main");

					if (senderMain.getBalance() >= amount) {
						senderMain.withdraw(amount);
						receiverMain.deposit(amount);

						transaction.setStatus("SUCCESS");

						sender.addTransaction(transaction);
//						receiver.addTransaction(transaction);

						receiver.addTransaction(new Transaction(receiver.getAccount("Main").getAccountNumber(), "RECEIVE",
								transaction.getValue(), "SUCCESS", transaction.getAccountNumber(), transaction.getTransactionID()));

						return "SUCCESS";

					} else {
						sender.addTransaction(transaction);
						receiver.addTransaction(transaction);

						transaction.setStatus("FAIL");

						return "FAIL";
					}
				}
				else{
					// if the customer is not found, check to see if it's a valid IBAN (for international payments)
//					Transaction transaction = new Transaction("PAY", amount, sender.getName(), receivingCustomer);

					Transaction transaction = new Transaction(new Account(sender.getName()).getAccountNumber(), "PAY", amount, "PENDING", new Account(receivingCustomer).getAccountNumber());

					if(isValidIBAN(receivingCustomer)){
						// we only need to withdraw from the sender (as the receiver is not one of the bank's customers)
						senderMain.withdraw(amount);

						transaction.setStatus("SUCCESS");

						sender.addTransaction(transaction);

						return "SUCCESS";

					}
					else{
						transaction.setStatus("FAIL");
						sender.addTransaction(transaction);
						return "FAIL";
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			e.printStackTrace();
		}

		return "FAIL";
	}

	private String move(String[] words, CustomerID customer, String testingOther) {
		try {
			double amount = Double.parseDouble(words[1]);
			String fromAccountName = words[2];
			String toAccountName = words[3];

//			Customer sender = customers.get(customer.getKey());
			Customer sender = new Customer(customer.getKey());
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

	private String move(String[] words, CustomerID customer) {
		double amount = Double.parseDouble(words[1]);
		String fromAccountName = words[2];
		String toAccountName = words[3];

//			Customer sender = customers.get(customer.getKey());
		Customer sender = new Customer(customer.getKey());
//		Transaction transaction = new Transaction("MOVE", amount, fromAccountName, toAccountName);
		Transaction transaction = new Transaction(new Account(customer.getKey(), fromAccountName).getAccountNumber(),
				"MOVE", amount, toAccountName, new Account(customer.getKey(), toAccountName).getAccountNumber());
		try {
			if (toAccountName.equalsIgnoreCase("LOAN")) {
				if (sender.repayLoan(fromAccountName, amount)) {
					transaction.setStatus("SUCCESS");
					sender.addTransaction(transaction);

					return "SUCCESS";
				}
			}
			else if (sender.moveMoney(amount, fromAccountName, toAccountName)) {
				transaction.setStatus("SUCCESS");
				sender.addTransaction(transaction);

				return "SUCCESS";
			}

		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			e.printStackTrace();
		}

		transaction.setStatus("FAIL");
		sender.addTransaction(transaction);

		return "FAIL";
	}




	private String printTransactionHistory(CustomerID customerID) {
		try {
			return new TransactionHistory().allTransactionsToString(customerID.getKey());
//			Customer customer = customers.get(customerID.getKey());
//			return customer.allTransactionsToString();
		} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}



//	public void addCustomer(String customerName, Customer customer){
//		customers.put(customerName, customer);
//	}


	private synchronized String createStandingOrder(String[] words, CustomerID customerID) {
		try {
			return new SOHandler(new StandingOrder(words, customerID), customerID).createNewStandingOrder();
		}
		catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}

}

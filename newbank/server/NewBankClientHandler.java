package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.Customer.CustomerID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class NewBankClientHandler extends Thread {

	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	private int loginAttempts = 3;
	private boolean userIsInactive = false;
	public static Timer timer;
	private static TimerTask task;

	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}

	public void setInactivityTimer() {
		if (task != null) {
			timer.cancel();
			task.cancel();
		}
		task = new TimerTask() {
			@Override
			public void run() {
				if(!userIsInactive) {
					userIsInactive = true;
					out.println("You've been logged out from inactivity, hit return to log back in");
				}
			}
		};
		timer = new Timer();
		timer.schedule(task, 30000);
	}

	public void run() {
		// keep getting requests from the client and processing them
		try {

			while (true) {
				// ask for user name
				out.println("Enter Username");
				String userName = in.readLine().toLowerCase();
				userName = userName.toLowerCase().replaceAll(" ", "");
				// ask for password
				userIsInactive = false;
				out.println("Enter Password");
				String password = in.readLine();
				out.println("Checking Details...");
				// authenticate user and get customer ID token from bank for use in subsequent
				// requests
				if (bank.isCustomer(userName)) {
					// List all running threads (performing this as I'm thinking of terminating
					// client thread after failing login 3 times)
					Set<Thread> threads = Thread.getAllStackTraces().keySet();
					for (Thread t : threads) {
						System.out.println(t.getName());
					}
					CustomerID customer = bank.checkLogInDetails(userName, password);

					// if the user is authenticated then get requests from the user and process them
					if (customer != null) {
						out.println("Log In Successful.");
						loginAttempts = 3;
						while (customer != null) {

							out.println("Here is the menu:" + "\nSHOWMYACCOUNTS - lists all accounts"
									+ "\nNEWACCOUNT <AccountType> - adds new account for current customer"
									+ "\nLOANHISTORY - shows the loans of current customer"
									+ "\nREQUESTLOAN <Amount> - requests a loan"
									+ "\nREPAYLOAN - Repay 10% of the loan"
									+ "\nPAY <Customer> <Amount> - pay another customer a different amount"
									+ "\nPAY <IBAN> <Amount> - pay another customer with IBAN number"
									+ "\nMOVE <Amount> <From> <To>  - move money between your own accounts"
									+ "\nTRANSACTIONHISTORY - show history of your transactions"
									+ "\nSTANDINGORDER <Payee Account Number> <Amount> <Frequency (2m = 2 months, 1y = 1 year etc.)> \n - Creates a new standing order that will pay in specified intervals"
									+ "\nCHECKSTANDINGORDERS - Shows all current Standing Orders"
									+ "\nDELETESTANDINGORDER <id> - Deletes selected Standing Order");
							out.println("What do you want to do?");
							setInactivityTimer();
							String request = in.readLine();
							setInactivityTimer();
							if (request.equals("LOGOUT") || userIsInactive) {
								task.cancel();
								timer.cancel();
								customer = null;
							} else {
								System.out.println("Request from " + customer.getKey());
								String response = bank.processRequest(customer, request);
								out.println(response);
							}
						}
					} else {
						out.println("Incorrect password. Test Try again");
						loginAttempts--;
						out.println("You have " + loginAttempts + " attempts remaining");
						if (loginAttempts == 0) {
							out.println(
									"You've entered an incorrect password 3 times, you are now locked out for 30 seconds");
							loginAttempts = 3;
							Thread.sleep(30000);
						}
					}

				} else {
					out.println("Username doesn't exist. Would you like to create an account with that name? (y/n)");
					String request = in.readLine();
					if (request.toLowerCase().equals("y")) {
						out.println("Please enter the account name");
						String accountName = in.readLine();
						Customer newCustomer = new Customer(userName, password, accountName,
								bank.newAccountNumber(), 0);
						// newCustomer.addNewCustomerToDb(userName, password);
//						bank.addCustomer(userName, newCustomer);
						out.println("Success, please login with your new account");
					} else {
						out.println("Please try again");
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

}

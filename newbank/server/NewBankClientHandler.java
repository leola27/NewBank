package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.Customer.CustomerID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class NewBankClientHandler extends Thread {

	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	int loginAttempts = 3;

	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}

	public void run() {
		// keep getting requests from the client and processing them
		try {

			while (true) {
				// ask for user name
				out.println("Enter Username");
				String userName = in.readLine();
				userName = userName.toLowerCase().replaceAll(" ", "");
				// ask for password
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
						while (customer != null) {
							out.println("What do you want to do?");
							String request = in.readLine();

							if (request.equals("LOGOUT")) {
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
						newCustomer.addNewCustomerToDb(userName, password);
						// bank.newCustomer(userName, password, accountName, 0);
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

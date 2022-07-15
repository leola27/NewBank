package newbank.server;

import newbank.server.Customer.Customer;
import newbank.server.sqlite.connect.net.src.Connect;
import newbank.server.Customer.CustomerID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{

	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;

	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}

	public void run() {
		// keep getting requests from the client and processing them
		try {

			while (true){
				// ask for user name
				out.println("Enter Username");
				Connect connection = new Connect();
				connection.connect();
				String userName = in.readLine();
				// ask for password
				out.println("Enter Password");
				String password = in.readLine();
				out.println("Checking Details...");
				// authenticate user and get customer ID token from bank for use in subsequent requests
				if(bank.isCustomer(userName)){
					CustomerID customer = bank.checkLogInDetails(userName, password);
					// if the user is authenticated then get requests from the user and process them
					if(customer != null) {
						out.println("Log In Successful. What do you want to do?");
						while(true) {
							String request = in.readLine();
							System.out.println("Request from " + customer.getKey());
							String response = bank.processRequest(customer, request);
							out.println(response);
						}
					}
					else{
						out.println("Incorrect password. Try again");
					}
				}
				else {
					out.println("Username doesn't exist. Would you like to create an account with that name? (y/n)");
					String request = in.readLine();
					if (request.toLowerCase().equals("y")) {
						out.println("Please enter the account name");
						String accountName = in.readLine();
						new Customer(userName, password, accountName, 0);
//						bank.newCustomer(userName, password, accountName, 0);
						out.println("Success, please login with your new account");
					} else {
						out.println("Please try again");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
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

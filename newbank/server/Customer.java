package newbank.server;

import java.util.ArrayList;

public class Customer {
	private String username;
	private String password;
	private ArrayList<Account> accounts;
	
	public Customer(String username, String password) {
		this.username = username;
		this.password = password;
		accounts = new ArrayList<>();
	}

	public String getUsername() {
		return username;
	}

	public boolean CheckPassword(String password){
		return this.password.equals(password);
	}

	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}

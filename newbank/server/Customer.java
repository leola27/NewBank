package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;

public class Customer {
	private String username;
	private String password;
	private HashMap<String,Account> accounts;
	
	public Customer(String username, String password) {
		this.username = username;
		this.password = password;
		accounts = new HashMap<>();
	}

	public String getUsername() {
		return username;
	}

	public boolean CheckPassword(String password){
		return this.password.equals(password);
	}

	public String accountsToString() {
		String s = "";
		for(Account a : accounts.values()) {
			s += a.toString() + "\n";
		}
		return s;
	}

	public boolean hasAccount(String name){
		return accounts.containsKey(name);
	}

	public boolean addAccount(Account account) {
		if(hasAccount(account.getAccountName())){
			return false;
		}
		accounts.put(account.getAccountName(), account);
		return true;
	}

	public Account getAccount(String accountName) {
		return accounts.get(accountName);
	}
}

package newbank.server.Transaction;

public class Transaction {
	private String type;
	private double value;
	private String status;
	private String origin;
	private String target;

	public Transaction(String type, double value, String origin, String target) {
		this.type = type;
		this.value = value;
		this.origin = origin;
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public double getValue() {
		return value;
	}

	public String getStatus() {
		return status;
	}

	public String getOrigin() {
		return origin;
	}

	public String getTarget() {
		return target;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String toString() {
		return "Type: " + type + "\nValue: " + value + "\nStatus: " + status + "\nOrigin: " + origin + "\nTarget: " + target;
	}
}

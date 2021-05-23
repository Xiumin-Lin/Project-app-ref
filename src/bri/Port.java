package bri;

/**
 * Connection port to Service BRi. For Programmer it's 2000, Amateur it's 3000
 */
public enum Port {
	PROG(2000), AMATEUR(3000);

	private int number;

	Port(int i) {
		this.number = i;
	}

	public int getNumber() {
		return number;
	}
}

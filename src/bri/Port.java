package bri;

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

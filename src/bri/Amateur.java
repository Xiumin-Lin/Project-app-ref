package bri;

public class Amateur {
	private String login;
	private String password;
	public Amateur() {}
	
	public Amateur(String login, String pwd) {
		//TODO check login is unique
		this.login = login;
		this.password = pwd;
	}
	
	public String getlogin() {
		return this.login;
	}
}

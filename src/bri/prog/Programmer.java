package bri.prog;

public class Programmer {
	private String login;
	private String password;
	private String serverFTP;
	private Boolean isCertifiedBRi = true; // for now is true by default

	public Programmer(String login, String pwd, String pathServerFTP) {
		this.login = login;
		this.password = pwd;
		this.serverFTP = pathServerFTP;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getServerFTP() {
		return this.serverFTP;
	}

	public void setServerFTP(String serverFTP) {
		this.serverFTP = serverFTP;
	}

	public Boolean getIsCertifiedBri() {
		return isCertifiedBRi;
	}

	public void setIsCertifiedBri(Boolean bool) {
		this.isCertifiedBRi = bool;
	}

}

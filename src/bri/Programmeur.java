package bri;

public class Programmeur {
	private String login;
	private String password;
	private String serverFTP;
	private Boolean isCertifiedBRi = true; // for now is true by default

	public Programmeur(String login, String pwd, String pathServerFTP) {
		// TODO check path correct
		this.setLogin(login);
		this.setPassword(pwd);
		this.serverFTP = pathServerFTP;
	}

	public String getServerFTP() {
		return this.serverFTP;
	}

	public void setServerFTP(String serverFTP) {
		this.serverFTP = serverFTP;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsCertifiedBri() {
		return isCertifiedBRi;
	}

	public void setIsCertifiedBri(Boolean isCertifiedBRi) {
		this.isCertifiedBRi = isCertifiedBRi;
	}

}

package bri;

import java.util.ArrayList;

public class Programmeur{
	private String login;
	private String password;
	private String serverFTP;
	private Boolean isCertifiedBri = true; //for now is true by default
	private ArrayList<String> listService;
	
	public Programmeur(String login, String pwd, String pathServerFTP) {
		//TODO check path correct
		this.login = login;
		this.password = pwd;
		this.serverFTP = pathServerFTP;
		this.listService = new ArrayList<>();
	}
	
	public void addService(String serviceName) {
		//TODO
	}
	
	public void updateService(String serviceName) {
		//TODO
	}
	
	public void setServerFTP(String pathServerFTP) {
		//TODO
	}
	
	//OPTIONAL
//	public void startService(String serviceName) {}
//	
//	public void stopService(String serviceName) {}
//	
//	public void deleteService(String serviceName) {}
	
}

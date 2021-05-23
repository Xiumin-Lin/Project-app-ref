package bri;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;

class ServiceBRiPROG implements Runnable {

	private Socket client; // programmeur's socket
	private Programmeur prog;
	// network allowing the client to communicate with the server
	private Communication net;
	private Boolean clientIsExit;

	public ServiceBRiPROG(Socket socket) {
		client = socket;
		prog = null;
		net = null;
		clientIsExit = false;
	}

	@Override
	public void run() {
		try {
			System.out.println("[Thread ServiceBRiPROG] Connected : " + Thread.currentThread().getName()); // Debug
			net = new Communication(client);

			net.write("Hello and Welcome to the BRi Service for programmers !##");
			net.write("You must be logged in to use the Bri service.");

			this.authentification(); // Authentification
			if(clientIsLogin()) { // Sends the list of available BRi services
				if(prog.getIsCertifiedBri())
					this.manageService();
				else {
					net.write("Your account is not BRi certified !##");
					prog = null;
				}
			}
		} catch(Exception e) { // End of service
			System.err.println("[ERROR] Service BRi Prog : " + e.getMessage());
		}

		try { // Close socket & communication
			client.close();
			net.close();
			System.out.println("[Thread ServiceBRiPROG] Closed : " + Thread.currentThread().getName()); // Debug
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the list of actions available to a programmer
	 * 
	 * @return the list of actions available
	 */
	public String getAvailableActionList() {
		StringBuilder sb = new StringBuilder();
		sb.append("##Please enter the number of the desired action :##");
		if(!clientIsLogin()) {
			sb.append("1 : Log in.##");
			sb.append("2 : Create an account.##");
		} else {
			sb.append("1 : Add a new service.##");
			sb.append("2 : Updating a service.##");
			sb.append("3 : Change your FTP server address.##");
			sb.append("4 : Start a service.##");
			sb.append("5 : Stop a service.##");
			sb.append("6 : Delete a service.##");
		}
		sb.append("0 : <Exit>##");
		return sb.toString();
	}

	/**
	 * Communication with the customer by showing actions for authentication. The
	 * customer can log in with an existing account or create a new one.
	 * 
	 * @throws SocketException if client socket is down
	 */
	private void authentification() throws SocketException {
		while(!clientIsLogin() && !this.clientIsExit && !this.client.isClosed()) {
			// send all previous writed msg to client
			net.send(this.getAvailableActionList());

			try {
				int choice = Integer.parseInt(net.readLine());

				switch(choice) {
				case 1: // connexion
					connexionProg();
					break;
				case 2: // create an account
					createProgAccount();
					break;
				case 0: // exit
					exit();
					break;
				default:
					throw new Exception("Invalid choice !##");
				}
			} catch(SocketException e) {
				throw new SocketException("Client Server Down - " + e.getMessage());
			} catch(Exception e) {
				net.write("[ERROR] Authentification : " + e.getMessage() + "##");
			}
		}
	}

	/**
	 * Only when the user is logged in. Communication with the customer by showing
	 * actions for manage the services it offers on Bri. It can add a new service,
	 * update it, change the ftp server address offers, etc.
	 * 
	 * @throws SocketException if client socket is down
	 */
	private void manageService() throws SocketException {
		// presentation
		net.write("##Hello " + this.prog.getLogin() + ", you are authorised to use the BRi service for dev !");
		net.write("##_ To add a service, it must be present on your FTP server."
				+ "##_ The service developed must be in a package with the same name as your login."
				+ "##_ To use a service, connect to the server Bri for amateur, port 3000.##");

		while(!this.clientIsExit && !this.client.isClosed()) {
			net.send(this.getAvailableActionList());

			try {
				int choice = Integer.parseInt(net.readLine());
				switch(choice) {
				case 1: // add a service
					addProgService();
					break;
				case 2: // update a service
					updateProgService();
					break;
				case 3: // change path to FTP serv
					changeProgFTPPath();
					break;
				case 4: // start a service
					startProgService();
					break;
				case 5: // stop a service
					stopProgService();
					break;
				case 6: // delete a service
					deleteProgService();
					break;
				case 0: // exit
					exit();
				default:
					net.write("Invalid choice !##");
				}
			} catch(SocketException e) {
				throw new SocketException("Client Server Down - " + e.getMessage());
			} catch(Exception e) {
				net.write("[ERROR] ManageService : " + e.getMessage() + "##");
			}
		}
	}

	/**
	 * Communication allowing the user to leave the service.
	 */
	private void exit() {
		net.send("");
		this.clientIsExit = true;
	}

	/**
	 * Communication allowing the user to connect to the BRi service for prog.
	 * 
	 * @throws IOException
	 * @throws AuthException Msg explaining the reason for authentication failure
	 */
	private void connexionProg() throws IOException, AuthException {
		net.send("Login :");
		String login = net.readLine();
		net.send("Password :");
		String pwd = net.readLine();
		this.prog = ProgRegistry.ProgAuthentification(login, pwd);
	}

	/**
	 * Communication allowing the user to create an account to connect to the BRi
	 * service for progs. If no AuthException is thrown, it means that the creation
	 * has been successful & the user will be connected with this account.
	 * 
	 * @throws IOException
	 * @throws AuthException Msg explaining the reason for authentication failure
	 */
	private void createProgAccount() throws IOException, AuthException {
		net.send("New login :");
		String newLogin = net.readLine();
		net.send("New password :");
		String newPwd = net.readLine();
		net.send("Your FTP server address (Ex: ftp://localhost:2121/classes/) :");
		String pathFTP = net.readLine();
		Programmeur newProg = new Programmeur(newLogin, newPwd, pathFTP);
		ProgRegistry.addProg(newProg);
		// Success if addProg didn't throw AuthException
		this.prog = newProg;
	}

	/**
	 * Communication allowing the customer to add a new service on BRi. A message
	 * will be displayed indicating whether the addition was successful or not.
	 */
	private void addProgService() {
		net.send("Enter the service to be [ADDED] using the following syntax => "
				+ "packageName.ServiceClassName (Ex: login.ServiceInversion) :");
		try {
			String classeName = net.readLine();
			// load the class and declare it to the ServiceRegistry
			Class<?> classe = getLoadedClass(classeName);
			ServiceRegistry.addService(classe);
			// Success if no error & exception is thrown by getLoadedClass & addService
			net.write("The service has been added !##");
		} catch(NormBRiException e) {
			net.write("[ERROR] Non-compliance with the Bri : ##" + e.getMessage());
		} catch(Exception e) {
			net.write("[ERROR] in addProgService : " + e.getMessage() + "##");
		}
	}

	/**
	 * Communication allowing the customer to update an existing service on BRi
	 */
	private void updateProgService() {
		net.send("Enter the service to be [UPDATED] using the following syntax => "
				+ "packageName.ServiceClassName (Ex: login.ServiceInversion) : ");
		try {
			String classeName = net.readLine();
			Class<?> classe = getLoadedClass(classeName);
			ServiceRegistry.updateService(classe);
			net.write("The service has been updated !##");
		} catch(Exception e) {
			net.write("[ERROR] in updateProgService : " + e.getMessage() + "##");
		}
	}

	/**
	 * Retrieve and load the class given as a parameter from the prog's ftp server.
	 * Also check if the class name respects the requested syntax. If loading fail,
	 * throw a Exception with the reason for failure
	 * 
	 * @param classeName - the class to be load
	 * @return the loaded class if success
	 * @throws Exception Msg explaining the reason for failure
	 */
	private Class<?> getLoadedClass(String classeName) throws Exception {
		// if check fail, throw a Exception
		checkPackNameIsLogin(classeName);
		// URLClassLoader sur ftp
		String fileNameURL = prog.getServerFTP();
		try {
			URLClassLoader urlcl = URLClassLoader.newInstance(new URL[] { new URL(fileNameURL) });
			Class<?> classe = urlcl.loadClass(classeName);
			return classe;
		} catch(ClassNotFoundException e) {
			throw new ClassNotFoundException("Loading Class Fail, Not Found " + e.getMessage() + " in " + fileNameURL);
		} catch(NoClassDefFoundError e) {
			throw new ClassNotFoundException("Loading Class Fail, " + e.getMessage());
		} catch(MalformedURLException e) {
			throw new MalformedURLException("Loading Class Fail, Wrong URL, " + e.getMessage());
		}
	}

	/**
	 * Check that the package name is the same as the prog login name
	 * 
	 * @param classeName - the service name to check
	 * @return true if package name matches the login name
	 * @throws Exception if package name isn't the same as the prog login name
	 */
	private Boolean checkPackNameIsLogin(String classeName) throws Exception {
		String[] classeNameSplit = classeName.split("\\.", 2);
		if(!classeNameSplit[0].equals(this.prog.getLogin()))
			throw new Exception("The Service package name should be the same as your login name !");
		return true;
	}

	/**
	 * Change the address of the programmer's ftp server
	 * 
	 * @throws IOException
	 */
	private void changeProgFTPPath() throws IOException {
		net.send("Enter the new FTP server address :");
		String newPath = net.readLine();
		this.prog.setServerFTP(newPath);
		net.write("The address has been modified !##");
	}

	/**
	 * Communication allowing the customer to start an existing service on Bri if it
	 * is arrested
	 */
	private void startProgService() {
		try {
			net.write(ServiceRegistry.toStringueStoppedService());
			net.send("##Enter the service NAME to be [STARTED] or empty to return to the previous menu :");
			String classeName = net.readLine();
			checkPackNameIsLogin(classeName);
			ServiceRegistry.startService(classeName);
			// Success if no exception is thrown by startService & checkPackNameIsLogin
			net.write("The service has been started !##");
		} catch(Exception e) {
			net.write("[ERROR] in startProgService : " + e.getMessage() + "##");
		}
	}

	/**
	 * Communication allowing the customer to stop (no delete) an existing service
	 * on BRi
	 */
	private void stopProgService() {
		try {
			net.write(ServiceRegistry.toStringue());
			net.send("##Enter the service NAME to be [STOPPED] or empty to return to the previous menu :");
			String classeName = net.readLine();
			checkPackNameIsLogin(classeName);
			ServiceRegistry.stopService(classeName);
			// Success if no exception is thrown by stopService & checkPackNameIsLogin
			net.write("The service has been stopped !##");
		} catch(Exception e) {
			net.write("[ERROR] in stopProgService : " + e.getMessage() + "##");
		}
	}

	/**
	 * Communication allowing the customer to delete an existing service on BRi
	 */
	private void deleteProgService() {
		try {
			net.write(ServiceRegistry.toStringue() + "##");
			net.write(ServiceRegistry.toStringueStoppedService());
			net.send("##Enter the service NAME to be [DELETED] or empty to return to the previous menu :");
			String classeName = net.readLine();
			checkPackNameIsLogin(classeName);
			ServiceRegistry.deleteService(classeName);
			// Success if no exception is thrown by deleteService & checkPackNameIsLogin
			net.write("The service has been deleted !##");
		} catch(Exception e) {
			net.write("[ERROR] in deleteProgService : " + e.getMessage() + "##");
		}
	}

	/**
	 * Check if client is log in
	 * 
	 * @return true if the client is connected, otherwise false
	 */
	public Boolean clientIsLogin() {
		return prog != null;
	}

	@Override
	protected void finalize() throws Throwable {
		client.close();
	}

	// launch of the service
	public void start() {
		(new Thread(this)).start();
	}
}

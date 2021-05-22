package bri;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

class ServiceBRiPROG implements Runnable {

	private Socket client; // programmeur's socket
	private Programmeur prog;
	// network allowing the client to communicate with the server
	private Communication net;

	public ServiceBRiPROG(Socket socket) {
		client = socket;
		prog = null;
		net = null;
	}

	@Override
	public void run() {
		try {
			System.out.println("[Thread ServiceBRiPROG] Connect : " + Thread.currentThread().getName());
			net = new Communication(client);

			net.write("Bonjour et Bienvenue au Service BRi pour les programmeurs !##");
			net.write("Les prog doivent s'authentifier afin d'utiliser le service Bri.");

			this.authentification(); // Authentification
			if(clientIsLogin()) // Sends the list of available BRi services
				this.manageService();

		} catch(IOException e) { // End of service
			System.err.println("[ERROR] Service BRi Prog : " + e.getMessage());
		}

		try { // Close socket & communication
			client.close();
			net.close();
			System.out.println("[ServiceBRiPROG Thread] Close : " + Thread.currentThread().getName());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public String getActionList() {
		StringBuilder sb = new StringBuilder();
		if(!clientIsLogin()) {
			sb.append("1 : Me connecter !##");
			sb.append("2 : Me creer un compte !##");
		} else {
			sb.append("1 : Ajouter un nouveau service.##");
			sb.append("2 : Mettre à jour un service.##");
			sb.append("3 : Changer l’adresse de son serveur ftp##");
		}
		sb.append("0 : Exit !##");
		return sb.toString();
	}

	/**
	 * TODO
	 */
	private void authentification() {
		while(!clientIsLogin()) {
			// send all previous writed msg to client
			net.send("##Veillez entrer le numéro de l'action désirée :##" + this.getActionList());

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
			} catch(Exception e) {
				net.write("[ERROR] Authentification : " + e.getMessage() + "####");
			}
		}
	}

	/**
	 * TODO
	 */
	private void manageService() {
		// presentation
		net.write("##Hello " + this.prog.getLogin() + ", vous être autorisé à utilisé le service BRi pour Prog !");
		net.write("##Pour ajouter un service, celui-ci doit étre présent sur votre serveur ftp."
				+ "##Le service développé doit se situer dans un package portant le même nom que votre login."
				+ "##Les clients se connectent au serveur amateur port 3000 pour lancer un service##");

		while(true) {
			net.send("##Veillez entrer le numéro de l'action désirée :##" + this.getActionList());

			try {
				int choice = Integer.parseInt(net.readLine());
				switch(choice) {
				case 1: // add service
					addProgService();
					break;
				case 2: // update service
					updateProgService();
					break;
				case 3: // change path to FTP serv
					changeProgFTPPath();
					break;
				case 0: // exit
					exit();
				default:
					net.write("Choix invalide !##");
				}
			} catch(Exception e) {
				net.write("[ERROR] ManageService : " + e.getMessage() + "####");
			}
		}
	}

	/**
	 * Communication allowing the user to leave the service
	 */
	private void exit() {
		net.send("");
	}

	/**
	 * Communication allowing the user to connect to the BRi service for prog
	 * 
	 * @throws IOException
	 * @throws AuthException
	 */
	private void connexionProg() throws IOException, AuthException {
		net.send("Login :");
		String login = net.readLine();
		net.send("Mot de passe :");
		String pwd = net.readLine();
		this.prog = ProgRegistry.ProgAuthentification(login, pwd);
	}

	/**
	 * Communication allowing the user to create an account to connect to the BRi
	 * service for the
	 * 
	 * @throws IOException
	 * @throws AuthException
	 */
	private void createProgAccount() throws IOException, AuthException {
		net.send("Nouveau Login :");
		String newLogin = net.readLine();
		net.send("Nouveau Mot de passe :");
		String newPwd = net.readLine();
		net.send("Le lien de votre serveur FTP (Ex: ftp://localhost:2121/classes/) :");
		String pathFTP = net.readLine();
		Programmeur newProg = new Programmeur(newLogin, newPwd, pathFTP);
		ProgRegistry.addProg(newProg);
		this.prog = newProg;
	}

	/**
	 * TODO
	 */
	private void addProgService() {
		net.send("Le Service à ajouter (Ex: login.ServiceInversion) :");
		try {
			String classeName = net.readLine();
			// charger la classe et la déclarer au ServiceRegistry
			Class<?> classe = getLoadedClass(classeName);
			ServiceRegistry.addService(classe);
			net.write("Le service a bien été ajouté !##");
		} catch(NormBRiException e) {
			net.write("[ERROR] Non-compliance with the Bri : ##" + e.getMessage() + "##");
		} catch(Exception e) {
			net.write("[ERROR] Add Service : " + e.getMessage() + "##");
		}
	}

	/**
	 * TODO
	 */
	private void updateProgService() {
		net.send("Le Service à mettre à jour (Ex: login.ServiceInversion) :");
		try {
			String classeName = net.readLine();
			Class<?> classe = getLoadedClass(classeName);
			ServiceRegistry.updateService(classe);
			net.write("Le service a bien été mise à jour !##");
		} catch(Exception e) {
			net.write("[ERROR] Update Service : " + e.getMessage() + "##");
		}
	}

	/**
	 * TODO
	 * 
	 * @param classeName
	 * @return
	 * @throws Exception
	 */
	private Class<?> getLoadedClass(String classeName) throws Exception {
		// Check that the package name is the same as the prog login name
		String[] classeNameSplit = classeName.split("\\.", 2);
		if(!classeNameSplit[0].equals(this.prog.getLogin()))
			throw new Exception("The Service package name should be the same as your login name");
		// URLClassLoader sur ftp
		try {
			String fileNameURL = prog.getServerFTP();
			URLClassLoader urlcl = URLClassLoader.newInstance(new URL[] { new URL(fileNameURL) });
			Class<?> classe = urlcl.loadClass(classeName);
			return classe;
		} catch(ClassNotFoundException e) {
			throw new ClassNotFoundException("Loading Class Fail, Not Found " + e.getMessage() + "##");
		} catch(NoClassDefFoundError e) {
			throw new ClassNotFoundException("Loading Class Fail, " + e.getMessage() + "##");
		} catch(MalformedURLException e) {
			throw new MalformedURLException("Loading Class Fail, Wrong URL, " + e.getMessage() + "##");
		}
	}

	/**
	 * TODO
	 * 
	 * @throws IOException
	 */
	private void changeProgFTPPath() throws IOException {
		net.send("Entrer le nouveau url du serveur FTP :");
		String newPath = net.readLine();
		this.prog.setServerFTP(newPath);
		net.write("Le path a bien été modifié !##");
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public Boolean clientIsLogin() {
		return prog != null;
	}

	@Override
	protected void finalize() throws Throwable {
		client.close();
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}
}

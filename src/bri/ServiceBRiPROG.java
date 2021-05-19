package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

class ServiceBRiPROG implements Runnable {

	private Socket client; // programmeur
	private Programmeur prog;

	public ServiceBRiPROG(Socket socket) {
		client = socket;
		prog = null;
	}

	@Override
	public void run() {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {

			int choice;
			StringBuilder sb = new StringBuilder();

			sb.append("Bonjour et Bienvenue au Service BRi pour les programmeurs !##");
			sb.append("Les prog doivent s'authentifier afin d'utiliser le service Bri.");

			// Authentification
			this.authentification(in, out);

			// Envoie de la liste des services BRi disponible
			if(clientIsLogin())
				this.manageService(in, out);

		} catch(IOException e) {
			// Fin du service
			System.err.println("[ERROR] Service BRi : " + e.getMessage());
		}

		try {
			client.close();
		} catch(IOException e2) {
			e2.printStackTrace();
		}
	}

	private void authentification(BufferedReader in, PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		int choice;
		try {
			while(!clientIsLogin()) {
				sb.append("Veillez entrer le numéro de l'action désirée :##" + this.getActionList());
				out.println(sb.toString()); // Send msg to client
				sb.setLength(0); // Clear StringBuilder
				choice = Integer.parseInt(in.readLine());

				out.println("Votre login :");
				String login = in.readLine();
				out.println("Votre mot de passe :");
				String pwd = in.readLine();

				switch(choice) {
				case 1: // connexion
					this.prog = ProgRegistry.ProgAuthentification(login, pwd);
					if(!clientIsLogin()) {
						sb.append("Authentification Echoué !####");
					}
					break;
				case 2: // creation de compte
					out.println("Le lien de votre serveur FTP (Ex: ftp://localhost:2121/classes/) :");
					String pathFTP = in.readLine();
					this.prog = new Programmeur(login, pwd, pathFTP);
					// TODO
					break;
				case 0: // exit
					out.println("");
					break;
				default:
					sb.append("Choix invalide !####");
				}
			}
		} catch(Exception e) {
			out.println("[ERROR] Authentification : " + e.getMessage() + "##");
		}
	}

	private void manageService(BufferedReader in, PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		int choice;

		// presentation
		sb.append("Hello " + this.prog.getLogin() + ", vous être autorisé à utilisé le service BRi pour Prog !");
		sb.append("##Pour ajouter un service, celui-ci doit étre présent sur votre serveur ftp."
				+ "##Le service développé doit se situer dans un package portant le même nom que votre login."
				+ "##Les clients se connectent au serveur amateur port 3000 pour lancer un service##");

		try {
			while(true) {
				sb.append("Veillez entrer le numéro de l'action désirée :##" + this.getActionList());
				out.println(sb.toString());
				sb.setLength(0); // clear StringBuilder
				choice = Integer.parseInt(in.readLine());

				switch(choice) {
				case 1: // add service
					out.println("Le Service à ajouter (Ex: login.ServiceInversion) :");
					try {
						String classeName = in.readLine();
						// charger la classe et la déclarer au ServiceRegistry
						Class<?> classe = getLoadedClass(classeName);
						ServiceRegistry.addService(classe);
						sb.append("Le service a bien été ajouté !####");
					} catch(Exception e) {
						sb.append("[ERROR] Add Service : " + e.getMessage() + "##");
					}
					break;
				case 2: // update service
					out.println("Le Service à mettre à jour (Ex: login.ServiceInversion) :");
					try {
						String classeName = in.readLine();
						Class<?> classe = getLoadedClass(classeName);
						ServiceRegistry.updateService(classe);
						sb.append("Le service a bien été mise à jour !####");
					} catch(Exception e) {
						sb.append("[ERROR] Update Service : " + e.getMessage() + "##");
					}
					break;
				case 3: // change path to FTP serv
					out.println("Entrer le nouveau url du serveur FTP :");
					String newPath = in.readLine();
					this.prog.setServerFTP(newPath);
					out.append("Le path a bien été modifié !####");
					break;
				case 0: // exit
					out.println("");
				default:
					sb.append("Choix invalide !####");
				}
			}
		} catch(Exception e) {
			out.println("[ERROR] ManageService : " + e.getMessage() + "##");
		}
	}

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

	public Boolean clientIsLogin() {
		return prog != null;
	}

	private Class<?> getLoadedClass(String classeName) throws Exception {
//		System.out.println("[DEBUG] in getLoadedClass");
		try {
			// URLClassLoader sur ftp
			String fileNameURL = prog.getServerFTP();
//			System.out.println("[DEBUG] fileNameURL = " + fileNameURL);
			URLClassLoader urlcl = URLClassLoader.newInstance(new URL[] { new URL(fileNameURL) });
			Class<?> classe = urlcl.loadClass(classeName);
			return classe;
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("[ERROR] : " + e.getMessage() + "##");
		}
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

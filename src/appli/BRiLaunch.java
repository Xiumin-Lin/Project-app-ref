package appli;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import bri.ServeurBRi;
import bri.ServiceRegistry;

public class BRiLaunch {
	private final static int PORT_PROG = 2000;
	private final static int PORT_AMA = 3000;

	public static void main(String[] args) throws MalformedURLException {
		@SuppressWarnings("resource")
		Scanner clavier = new Scanner(System.in);
		// TODO à modifier pour que ça soit utiliser par le prog

		// URLClassLoader sur ftp
		String fileNameURL = "ftp://localhost:2121/classes/";
		URLClassLoader urlcl = URLClassLoader.newInstance(new URL[] { new URL(fileNameURL) });

		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activité BRi");
		System.out.println("Pour ajouter une activité, celle-ci doit étre présente sur votre serveur ftp");
		System.out.println("A tout instant, en tapant le nom de la classe, vous pouvez l'intégrer");
		System.out.println("Les clients se connectent au serveur 3000 pour lancer une activité");

		new Thread(new ServeurBRi(PORT_PROG)).start();
		new Thread(new ServeurBRi(PORT_AMA)).start();

		while(true) {
			try {
				String classeName = clavier.next();
				// charger la classe et la déclarer au ServiceRegistry
				Class<?> classe = urlcl.loadClass(classeName);
				ServiceRegistry.addService(classe);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[ERROR] : " + e.getMessage());
			}
		}
	}
}

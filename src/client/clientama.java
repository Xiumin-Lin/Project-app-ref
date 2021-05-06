package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Ce client se connecte à un serveur dont le protocole est 
 * menu-choix-question-réponse client-réponse service
 * il n'y a qu'un échange (pas de boucle)
 * la réponse est saisie au clavier en String
 * 
 * Le protocole d'échange est suffisant pour le tp4 avec ServiceInversion
 * ainsi que tout service qui pose une question, traite la donnée du client et envoie sa réponse 
 * mais est bien sur susceptible de (nombreuses) améliorations
 */
public class clientama {
	private final static int PORT_AMA = 3000;
	private final static String HOST = "localhost";

	public static void main(String[] args) {
		Socket s = null;
		try {
			s = new Socket(HOST, PORT_AMA);

			BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter sout = new PrintWriter(s.getOutputStream(), true);
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Connecté au serveur " + s.getInetAddress() + ":" + s.getPort());

			// Service BRi affiche le menu et choix des services dispo
			String line = sin.readLine();
			System.out.println(line.replaceAll("##", "\n")); // TODO
			// Amateur saisie/envoie son choix
			sout.println(clavier.readLine());
			// réception/affichage de la question du service demandé
			System.out.println(sin.readLine());
			// saisie clavier/envoie au service de la réponse
			sout.println(clavier.readLine());
			// réception/affichage de la réponse
			System.out.println(sin.readLine());

		} catch (IOException e) {
			System.err.println("Fin de la connexion");
		}

		try {
			if(s != null)
				s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

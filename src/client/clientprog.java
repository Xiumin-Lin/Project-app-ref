package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import bri.Communication;
import bri.Port;

public class ClientProg {
	private final static int PORT_PROG = Port.PROG.getNumber();
	private final static String HOST = "localhost";

	public static void main(String[] args) {
		// Socket s = null;
		try(Socket s = new Socket(HOST, PORT_PROG);
				Communication net = new Communication(s);
				BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));) {

			System.out.println("Connecté au serveur " + s.getInetAddress() + ":" + s.getPort());
			// Echange avec le serveur Bri PROG
			String line = net.readLine();

			while(!line.isEmpty()) {
				System.out.println(line.replaceAll("##", "\n"));
				net.send(clavier.readLine());
				line = net.readLine();
			}

			System.out.println("Connexion terminée. Merci d'avoir utilisé le service Bri !");

		} catch(IOException e) {
			System.err.println("Fin de la connexion");
		}
	}

}

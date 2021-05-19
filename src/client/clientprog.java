package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bri.Port;

public class ClientProg {
	private final static int PORT_PROG = Port.PROG.getNumber();
	private final static String HOST = "localhost";

	public static void main(String[] args) {
		// Socket s = null;
		try(Socket s = new Socket(HOST, PORT_PROG);
				BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter sout = new PrintWriter(s.getOutputStream(), true);
				BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));) {

			System.out.println("Connecté au serveur " + s.getInetAddress() + ":" + s.getPort());
			// Echange avec le serveur Bri PROG
			String line = sin.readLine();

			while(!line.isEmpty()) {
				System.out.println(line.replaceAll("##", "\n"));
				sout.println(clavier.readLine());
				line = sin.readLine();
			}

			System.out.println("Connexion terminée. Merci d'avoir utilisé le service Bri !");

		} catch(IOException e) {
			System.err.println("Fin de la connexion");
		}
	}

}

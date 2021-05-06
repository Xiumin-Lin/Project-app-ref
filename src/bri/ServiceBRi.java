package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServiceBRi implements Runnable {

	private Socket client;

	ServiceBRi(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {
			// Envoie liste des services disponible
			out.println(ServiceRegistry.toStringue() + "##Tapez le numéro de service désiré :");
			int choix = Integer.parseInt(in.readLine());

			// instancier le service numéro "choix" en lui passant la socket "client"
			// invoquer run() pour cette instance ou la lancer dans un thread à part

		} catch (IOException e) {
			// Fin du service
			System.err.println("[ERROR] : " + e.getMessage());
		}

		try {
			client.close();
		} catch (IOException e2) {
			e2.printStackTrace();
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

package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

class ServiceBRi implements Runnable {

	private Socket client; // amateur

	public ServiceBRi(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {
			// Envoie la liste des services disponible
			out.println(ServiceRegistry.toStringue() + "##Tapez le numéro de service désiré :");
			int choix = Integer.parseInt(in.readLine());
			Class<?> service = ServiceRegistry.getServiceClass(choix);
			// instancier le service numéro "choix" en lui passant la socket "client"
			// invoquer run() pour cette instance ou la lancer dans un thread à part
			try {
				service.getDeclaredConstructor(Socket.class).newInstance(client);
			} catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch(IOException e) {
			// Fin du service
			System.err.println("[ERROR] : " + e.getMessage());
		}

		try {
			client.close();
		} catch(IOException e2) {
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

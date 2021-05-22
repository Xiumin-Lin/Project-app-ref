package bri;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

class ServiceBRi implements Runnable {

	private Socket client; // amateur
	// network allowing the client to communicate with the server
	private Communication net;

	public ServiceBRi(Socket socket) {
		client = socket;
		net = null;
	}

	@Override
	public void run() {

		try {
			net = new Communication(client);
			// Envoie la liste des services disponible
			net.send(ServiceRegistry.toStringue() + "##Tapez le numéro de service désiré :");

			int choix = Integer.parseInt(net.readLine());
			Class<?> service = ServiceRegistry.getServiceClass(choix);
			// instancier le service numéro "choix" en lui passant la socket "client"
			// invoquer run() pour cette instance ou la lancer dans un thread à part
			try {
				service.getDeclaredConstructor(Socket.class).newInstance(client);
			} catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				System.err.println("[ERROR] Create New Instance : " + e.getMessage());
			}

		} catch(IOException e) {
			// Fin du service
			System.err.println("[ERROR] Service BRi : " + e.getMessage());
		}

		try {
			client.close();
			net.close();
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

package bri;

import java.io.IOException;
import java.net.ServerSocket;

public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;

	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port) {
		try {
			listen_socket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree un ServiceBri qui va la traiter.
	@Override
	public void run() {
		try {
			while(true)
				new ServiceBRi(listen_socket.accept()).start();
		} catch (IOException e) {
			try {
				this.listen_socket.close();
			} catch (IOException e1) {
				e.printStackTrace();
			}
			System.err.println("Pb sur le port d'écoute :" + e.getMessage());
		}
	}

	// restituer les ressources --> finalize
	@Override
	protected void finalize() throws Throwable {
		try {
			this.listen_socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();
	}
}

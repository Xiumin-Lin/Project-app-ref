package bri;

import java.io.IOException;
import java.net.ServerSocket;

public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;

	// Create a TCP server - object of the ServerSocket class
	public ServeurBRi(int port) {
		try {
			listen_socket = new ServerSocket(port);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("[ServerBRi Thread] : " + Thread.currentThread().getName()); // Debug
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree selon le port de la socket :
	// >> soit un ServiceBri pour traiter une demande de connexion d'un amateur
	// >> soit un ServiceBriPROG pour les demandes d'un programmeurs
	@Override
	public void run() {
		System.out.println("Starting the BRi Server ! Port " + listen_socket.getLocalPort());
		try {
			if(listen_socket.getLocalPort() == Port.AMATEUR.getNumber()) {
				while(true) {
					System.out.println("[Thread ServeurAMA] : " + Thread.currentThread().getName()); // Debug
					new ServiceBRi(listen_socket.accept()).start();
				}

			} else if(listen_socket.getLocalPort() == Port.PROG.getNumber()) {
				while(true) {
					System.out.println("[Thread ServeurPROG] : " + Thread.currentThread().getName()); // Debug
					new ServiceBRiPROG(listen_socket.accept()).start();
				}
			}
		} catch(IOException e) {
			try {
				this.listen_socket.close();
			} catch(IOException e1) {
				e.printStackTrace();
			}
			System.err.println("[Error] on the listening port :" + e.getMessage());
		}
	}

	// restituer les ressources --> finalize
	@Override
	protected void finalize() throws Throwable {
		try {
			this.listen_socket.close();
		} catch(Throwable e1) {
			throw e1;
		}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();
	}
}

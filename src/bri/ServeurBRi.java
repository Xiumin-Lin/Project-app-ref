package bri;

import java.io.IOException;
import java.net.ServerSocket;

public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;

	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port) {
		try {
			listen_socket = new ServerSocket(port);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("[ServerBRi Current Thread] : " + Thread.currentThread().getName()); // Debug
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree selon le port de la socket :
	// >> soit un ServiceBri pour traiter une demande de connexion d'un amateur
	// >> soit un ServiceBriPROG pour les demandes d'un programmeurs
	@Override
	public void run() {
		System.out.println("Démarrage du Serveur BRi ! Port " + listen_socket.getLocalPort());
		try {
			if(listen_socket.getLocalPort() == Port.AMATEUR.getNumber()) {
				while(true) {
					System.out.println("[ServeurAMA Current Thread] : " + Thread.currentThread().getName()); // Debug
					new ServiceBRi(listen_socket.accept()).start();
				}

			} else if(listen_socket.getLocalPort() == Port.PROG.getNumber()) {
				while(true) {
					System.out.println("[ServeurPROG Current Thread] : " + Thread.currentThread().getName()); // Debug
					new ServiceBRiPROG(listen_socket.accept()).start();
				}
			}
		} catch(IOException e) {
			try {
				this.listen_socket.close();
			} catch(IOException e1) {
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
		} catch(Throwable e1) {
			throw e1;
		}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();
	}
}

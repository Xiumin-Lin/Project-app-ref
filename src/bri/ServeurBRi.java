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
		System.out.println("[Thread ServerBRi] : " + Thread.currentThread().getName()); // Debug
	}

	// The server listens and accepts connections.
	// for each connection, it creates according to the socket port :
	// - Port.AMATEUR => a ServiceBri to handle a connection request from amateurs
	// - Port.PROG => a ServiceBriPROG for requests from programmers
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

	// return the resources --> finalize
	@Override
	protected void finalize() throws Throwable {
		try {
			this.listen_socket.close();
		} catch(Throwable e1) {
			throw e1;
		}
	}

	// launch server
	public void lancer() {
		(new Thread(this)).start();
	}
}

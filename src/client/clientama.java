package client;

import java.io.IOException;
import java.net.Socket;

public class clientama {
	private final static int PORT_AMA = 3000;
	private final static String HOST = "localhost"; 
	
	public static void main(String[] args) {
		Socket s = null;
		try {
			s = new Socket(HOST, PORT_AMA);
			//TODO 
			
		} catch (IOException e) {
			System.err.println("Fin de la connexion"); 
		}

	}
}

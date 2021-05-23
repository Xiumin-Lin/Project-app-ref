package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import bri.Communication;
import bri.Port;

/**
 * This programmers connects to a server whose protocol is to choose a service
 * from the list displayed, and then use that service. The client's response is
 * typed into String. Works almost identical to ClientAma, only the connection
 * port changes. Check {@link Port}
 */
public class ClientProg {
	private final static int PORT_PROG = Port.PROG.getNumber();
	private final static String HOST = "localhost";

	public static void main(String[] args) {
		try(Socket s = new Socket(HOST, PORT_PROG);
				Communication net = new Communication(s);
				BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));) {

			System.out.println("Connected to Bri server for programmers " + s.getInetAddress() + ":" + s.getPort());

			// Communicates with the Bri PROG server
			String line = net.readLine();
			do {
				System.out.println(line.replaceAll("##", "\n")); // Displays a message received by the server
				net.send(clavier.readLine());// Send a response to this message
				line = net.readLine(); // Wait for the server to respond
			} while(!line.isBlank()); // Continue while line is not blank or empty

			System.out.println("Connection completed. Thank you for using the Bri service!");

		} catch(IOException e) {
			System.err.println("End of connection");
		}
	}

}

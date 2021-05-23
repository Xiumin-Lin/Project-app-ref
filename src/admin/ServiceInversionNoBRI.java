package admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

abstract class ServiceInversionNoBRI implements Runnable {
//	private final Socket client; //Good
//	private Socket client; // no final
	public final Socket client; // no private

//	private ServiceInversionNoBRI(Socket socket) { // no public
//		client = socket;
//	}

	public ServiceInversionNoBRI() { // no socket
		client = null;
	}

	public ServiceInversionNoBRI(Socket socket) throws Exception { // throw Excep
		client = socket;
		throw new Exception("a");
	}

	@Override
	public void run() {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {
			out.println("Tapez un texte à inverser");

			String line = in.readLine();

			String invLine = new String(new StringBuffer(line).reverse());

			out.println("Résultat de l'inversion : " + invLine);

			client.close();
		} catch(IOException e) {
			// Fin du service d'inversion
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		client.close();
	}

//	public String toStringue() { // no static
//		return "Inversion de texte";
//	}

//	private static String toStringue() { // no public
//		return "Inversion de texte";
//	}

	public static String toStringue() throws Exception { // throw excep
		throw new Exception("a");
	}
}

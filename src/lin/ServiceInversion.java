package lin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceInversion implements bri.Service {

	private final Socket client;

	public ServiceInversion(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {
			out.println("Tapez un texte à inverser :");

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

	public static String toStringue() {
		return "Inversion de texte";
	}
}

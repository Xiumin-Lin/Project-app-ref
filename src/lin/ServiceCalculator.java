package lin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceCalculator implements bri.Service {

	private final Socket client;

	public ServiceCalculator(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {

			out.println("Bienvenue dans le service calculatrice (simple) qui calcul 2 valeurs !##"
					+ "Veillez saisir votre action :##1) Addition##2) Soustraction##3) Multiplication##4) Division##");

			try {
				int choice = Integer.parseInt(in.readLine());
				out.println("Entrer la 1ère valeur : ");
				float val1 = Float.parseFloat(in.readLine());
				out.println("Entrer la 2e valeur : ");
				float val2 = Float.parseFloat(in.readLine());
				switch(choice) {
				case 1:
					out.printf("Résultat de l'addition : %.2f + %.2f = %.2f", val1, val2, val1 + val2);
					break;
				case 2:
					out.printf("Résultat de la soustraction : %.2f - %.2f = %.2f", val1, val2, val1 - val2);
					break;
				case 3:
					out.printf("Résultat de la multiplication : %.2f * %.2f = %.2f", val1, val2, val1 * val2);
					break;
				case 4:
					out.printf("Résultat de la division : %.2f / %.2f = %.2f", val1, val2, val1 / val2);
					break;
				default:
					throw new NumberFormatException("Ce choix n'est pas prise en compte : " + choice);
				}
			} catch(NumberFormatException e) {
				out.println("[Error] ServiceCalculator : " + e.getMessage());
			}

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
		return "Calcul de 2 valeurs";
	}
}

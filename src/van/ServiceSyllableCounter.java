package van;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceSyllableCounter implements bri.Service {

	private final Socket client;

	public ServiceSyllableCounter(Socket socket) {
		client = socket;
	}

	@Override
	public void run() {
		try(BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {
			out.println("Tapez un texte pour compter ses syllabes");

			String line = in.readLine();
		
			int nbSyllables = 0;
	        // Set everything to upper case
	        String upperCaseWord = line.toUpperCase();
	        // The loop will run from 1 to the character before the last
	        for (int i = 1; i < upperCaseWord.length()-1; i++){
	            char ch = upperCaseWord.charAt(i);
	            char c = (upperCaseWord.charAt(i-1));
	        // Only adds if the char is in the index AND if there is no
	        // other letter in the index fore i  
	            if ("AEIOUY".indexOf(ch) >= 0 && "AEIOUY".indexOf(c) == -1){
	                nbSyllables++;
	            }
	             
	        }
	        //Check the first character
	        char firstChar = upperCaseWord.charAt(0);
	        //Check the last character
	        char lastChar = upperCaseWord.charAt(upperCaseWord.length()-1);
	         
	        //Not count if 'E' is the last char
	        if (lastChar == 'E'){
	            nbSyllables = nbSyllables;
	        } 
	        //Add if the last char is not 'E'
	        else if ("AIOUY".indexOf(lastChar) >= 0){
	            nbSyllables++;
	        }
	        //Add if the first character is in the index
	        if ("AEIOUY".indexOf(firstChar) >= 0){
	            nbSyllables++;
	        }
	        //There must be atleast one syllable
	        if(nbSyllables <= 0){
	            nbSyllables = 1;
	        }
			
			out.println("Nombres de syllabes dans " + line + " : " + nbSyllables);
			

			client.close();
		} catch (IOException e) {
			// Fin du service d'inversion
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		client.close();
	}

	public static String toStringue() {
		return "Compteur de de syllabes";
	}
}

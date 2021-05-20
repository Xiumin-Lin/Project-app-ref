package bri;

import java.util.HashMap;
import java.util.Map;

/**
 * Registre de programmeur
 */
public class ProgRegistry {
	static {
		programmeurList = new HashMap<String, Programmeur>();
	}
	private static Map<String, Programmeur> programmeurList;

	public static void addProg(Programmeur prog) throws AuthException {
		synchronized (programmeurList) {
			if(!programmeurList.containsKey(prog.getLogin()))
				programmeurList.put(prog.getLogin(), prog);
			else
				throw new AuthException("Login déjà existant");
		}
	}

	public static void addNewProg(String login, String pwd, String pathServerFTP) throws AuthException {
		synchronized (programmeurList) {
			if(!programmeurList.containsKey(login))
				programmeurList.put(login, new Programmeur(login, pwd, pathServerFTP));
			else
				throw new AuthException("Login déjà existant");
		}
	}

	/**
	 * 
	 * @param login
	 * @param pwd
	 * @return the prog if authentification is success, else return null
	 * @throws Exception
	 */
	public static Programmeur ProgAuthentification(String login, String pwd) throws AuthException {
		synchronized (programmeurList) {
			if(programmeurList.containsKey(login)) {
				Programmeur prog = programmeurList.get(login);
				if(prog.getPassword().equals(pwd))
					return prog;
				else
					throw new AuthException("Mot de passe incorrect");
			} else
				throw new AuthException("Login inconnu");
		}
	}

}

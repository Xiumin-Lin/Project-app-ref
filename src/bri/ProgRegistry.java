package bri;

import java.util.HashMap;
import java.util.Map;

/**
 * Programmer's register
 */
public class ProgRegistry {
	private static Map<String, Programmeur> programmeurMap;

	static {
		programmeurMap = new HashMap<String, Programmeur>();
		// a default programmer account
		programmeurMap.put("admin", new Programmeur("admin", "admin", "ftp://localhost:2121/classes/"));
	}

	/**
	 * Add a programmer in the register
	 * 
	 * @param prog the programmer to add in the register
	 * @throws AuthException Msg explaining the reason for authentication failure
	 */
	public static void addProg(Programmeur prog) throws AuthException {
		synchronized (programmeurMap) {
			if(!programmeurMap.containsKey(prog.getLogin()))
				programmeurMap.put(prog.getLogin(), prog);
			else
				throw new AuthException("Already existing login");
		}
	}

	/**
	 * Create & Add a programmer in the register
	 * 
	 * @param login         - new prog login
	 * @param pwd           - new prog password
	 * @param pathServerFTP - new prog FTP server address
	 * @throws AuthException Msg explaining the reason for authentication failure
	 */
	public static void addNewProg(String login, String pwd, String pathServerFTP) throws AuthException {
		synchronized (programmeurMap) {
			if(!programmeurMap.containsKey(login))
				programmeurMap.put(login, new Programmeur(login, pwd, pathServerFTP));
			else
				throw new AuthException("Already existing login");
		}
	}

	/**
	 * Authentication of a programmer by checking that the password is associated
	 * with the correct login
	 * 
	 * @param login - prog login
	 * @param pwd   - prog password
	 * @return the prog if authentification is success, else return null
	 * @throws AuthException Msg explaining the reason for authentication failure
	 */
	public static Programmeur ProgAuthentification(String login, String pwd) throws AuthException {
		synchronized (programmeurMap) {
			if(programmeurMap.containsKey(login)) {
				Programmeur prog = programmeurMap.get(login);
				if(prog.getPassword().equals(pwd))
					return prog;
				else
					throw new AuthException("Incorrect password");
			} else
				throw new AuthException("Login unknown");
		}
	}

}

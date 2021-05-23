package bri.prog;

import java.util.HashMap;
import java.util.Map;

import bri.exception.AuthException;

/**
 * Programmer's register
 */
public class ProgRegistry {
	private static Map<String, Programmer> programmerMap;

	static {
		programmerMap = new HashMap<String, Programmer>();
		// a default programmer account
		programmerMap.put("admin", new Programmer("admin", "admin", "ftp://localhost:2121/classes/"));
	}

	/**
	 * Add a programmer in the register
	 * 
	 * @param prog the programmer to add in the register
	 * @throws AuthException Msg explaining the reason for authentication failure
	 */
	public static void addProg(Programmer prog) throws AuthException {
		synchronized (programmerMap) {
			if(!programmerMap.containsKey(prog.getLogin()))
				programmerMap.put(prog.getLogin(), prog);
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
		synchronized (programmerMap) {
			if(!programmerMap.containsKey(login))
				programmerMap.put(login, new Programmer(login, pwd, pathServerFTP));
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
	public static Programmer ProgAuthentification(String login, String pwd) throws AuthException {
		synchronized (programmerMap) {
			if(programmerMap.containsKey(login)) {
				Programmer prog = programmerMap.get(login);
				if(prog.getPassword().equals(pwd))
					return prog;
				else
					throw new AuthException("Incorrect password");
			} else
				throw new AuthException("Login unknown");
		}
	}

}

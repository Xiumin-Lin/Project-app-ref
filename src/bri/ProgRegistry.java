package bri;

import java.util.HashMap;
import java.util.Map;

public class ProgRegistry {
	static {
		programmeurList = new HashMap<String, Programmeur>();
	}
	private static Map<String, Programmeur> programmeurList;

	public static void addProg(Programmeur prog) {
		synchronized (programmeurList) {
			if(!programmeurList.containsKey(prog.getLogin()))
				programmeurList.put(prog.getLogin(), prog);
		}
	}

	public static void addNewProg(String login, String pwd, String pathServerFTP) {
		synchronized (programmeurList) {
			if(!programmeurList.containsKey(login)) {
				programmeurList.put(login, new Programmeur(login, pwd, pathServerFTP));
			}
		}
	}

	/**
	 * 
	 * @param login
	 * @param pwd
	 * @return the prog if authentification is success, else return null
	 */
	public static Programmeur ProgAuthentification(String login, String pwd) {
		synchronized (programmeurList) {
			if(programmeurList.containsKey(login)) {
				Programmeur prog = programmeurList.get(login);
				if(prog.getPassword().equals(pwd))
					return prog;
			}
		}
		return null;
	}

}

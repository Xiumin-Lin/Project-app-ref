package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<Class<?>>(); // Class<? extends bri.Service>
	}
	private static List<Class<?>> servicesClasses;

	// ajoute une classe de service après contrôle de la norme BRi
	public static void addService(Class<?> classe) {
		System.out.println("In add serv function for " + classe.getName());
		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		if(isNormeBRi(classe)) {
			System.out.println("True => Adding");
		}
//		servicesClasses.add(classe.newInstance());
	}

	// renvoie la classe de service (numService -1)
	public static void getServiceClass(int numService) {

	}

	// liste les activités présentes
	public static String toStringue() {
		StringBuilder result = new StringBuilder("Activités présentes :##");
		// TODO
		return result.toString();
	}

	private static boolean isNormeBRi(Class<?> classe) {
		String classeName = classe.getName();
		int modifiers = classe.getModifiers();
		// ne pas être abstract
		if(!Modifier.isAbstract(modifiers)) {
			System.out.println(classeName + " is not abstract !");
			return false;
			// être publique
		} else if(!Modifier.isPublic(modifiers)) {
			System.out.println(classeName + " is not public !");
			return false;
		}

		Class<?>[] interfaceClasses = classe.getInterfaces();
		// implémenter l'interface bri.Service
		if(interfaceClasses.length != 0) {
			boolean containService = false;
			for (Class<?> aClass : interfaceClasses) {
				if(aClass.getSimpleName().equals("Service"))
					containService = true;
			}
			if(!containService) {
				System.out.println(classeName + " no implement Service");
				return false;
			}
		}
		// avoir un constructeur public (Socket) sans exception
		// avoir un attribut Socket private final
		// avoir une méthode public static String toStringue() sans exception
		Constructor<?>[] constructors = classe.getConstructors();
		if(constructors.length != 0) {

		}
		return true;
	}

	public void updateService(String serviceName) {
		// TODO
	}

	// OPTIONAL
//	public void startService(String serviceName) {}
//	
//	public void stopService(String serviceName) {}
//	
//	public void deleteService(String serviceName) {}

}

package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partag�e en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<Class<?>>();
	}
	private static List<Class<?>> servicesClasses;

// ajoute une classe de service apr�s contr�le de la norme BLTi
	public static void addService(Class<?> classe) {
		System.out.println("In add serv function for " + classe.getName());
		// v�rifier la conformit� par introspection
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
	
// liste les activit�s pr�sentes
	public static String toStringue() {
		String result = "Activit�s pr�sentes :##";
		// todo
		return result;
	}
	
	private static boolean isNormeBRi(Class<?> classe) {
		String classeName = classe.getName();
		int modifiers = classe.getModifiers();
		
		
		if(!Modifier.isAbstract(modifiers)) {
			System.out.println(classeName + " is not abstract !");
			return false;
			
		} else if(!Modifier.isPublic(modifiers)) {
			System.out.println(classeName + " is not public !");
			return false;
		}
		
		Class<?>[] interfaceClasses = classe.getInterfaces();
		if(interfaceClasses.length != 0) {
			boolean containService = false;
			for (Class<?> aClass : interfaceClasses) {
				if(aClass.getSimpleName().equals("Service")) containService = true;
			}
			if(!containService) {
				System.out.println(classeName + " no implement Service");
				return false;
			}
		} 
		Constructor<?>[] constructors = classe.getConstructors();
		if(constructors.length != 0) {
			
		}
		return true;
		
	}

}

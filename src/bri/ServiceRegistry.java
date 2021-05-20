package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
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
		System.out.println("Adding new service " + classe.getName());
		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		// TODO à Finir
//		if(isNormeBRi(classe)) {
//			System.out.println("True => Adding");
//		}
		// TODO à corriger newInstance()
		servicesClasses.add(classe);
	}

	// renvoie la classe de service (numService -1)
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}

	// liste les activités présentes
	public static String toStringue() {
		StringBuilder result = new StringBuilder("Activités présentes :##");
		if(servicesClasses.isEmpty())
			result.append("Aucun service disponible !##");
		else {
			int i = 1;
			for (Class<?> service : servicesClasses) {
				// TODO à remplacer par toStringue()
				result.append(i + ") " + service.getName() + "##");
				i++;
			}
		}
		return result.toString();
	}

	private static boolean isNormeBRi(Class<?> classe) {
		String classeName = classe.getName();
		int modifiers = classe.getModifiers();
		String errMsg = "[Norme BRi] ";
		// ne pas être abstract
		if(!Modifier.isAbstract(modifiers)) {
			System.out.println(errMsg + classeName + " should not be abstract !");
			return false;
			// être publique
		} else if(!Modifier.isPublic(modifiers)) {
			System.out.println(errMsg + classeName + " is not public !");
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
		Constructor<?>[] constructors = classe.getConstructors();
		if(constructors.length != 0) {
			boolean containSocketConstructor = false; 
			for(Constructor<?> aConstructor : constructors) {
				if(aConstructor.getParameterCount() == 1 && Modifier.isPublic(aConstructor.getModifiers()) && aConstructor.getExceptionTypes().length == 0) {
					for(Class<?> aParameterType : aConstructor.getParameterTypes()) {
						if(aParameterType.getName().equals(Socket.class.getName())) {
							containSocketConstructor = true;
					}
					}
				}
			}
			if(!containSocketConstructor) {
				System.out.println(classeName + " do not have public constructor with Socket parameter");
				return false;
			}
		}
		
		// avoir une méthode public static String toStringue() sans exception
		Method[] methods = classe.getMethods();
		if(methods.length != 0) {
			boolean containToStringue = false;
			for (Method aMethod : methods) {
				if(aMethod.getName().equals("toStringue") && Modifier.isPublic(aMethod.getModifiers()) && Modifier.isStatic(aMethod.getModifiers()) && aMethod.getReturnType().getName().equals(String.class.getName()) && aMethod.getExceptionTypes().length == 0) 
					containToStringue = true;
			}
			if(!containToStringue) {
				System.out.println(classeName + " do not have method toStringue");
				return false;
			}
		}
		
		// avoir un attribut Socket private final
		Field[] fields = classe.getDeclaredFields();
		if(fields.length != 0) {
			boolean containSocket = false;
			for(Field aField : fields) {
				if(aField.getType().getName().equals(Socket.class.getName()) && Modifier.isPrivate(aField.getModifiers()) && Modifier.isFinal(aField.getModifiers())) {
					containSocket = true;
				}
			}
			if(!containSocket) {
				System.out.println(classeName + " do not have private final Socket field");
				return false;
			}
		}
		
		
		return true;
	}

	public static void updarvice(Class<?> serviceClass) {
		System.out.println("Update service " + serviceClass.getName());
		// TODO
	}

	// OPTIONAL
//	public void startService(String serviceName) {}
//	
//	public void stopService(String serviceName) {}
//	
//	public void deleteService(String serviceName) {}

}

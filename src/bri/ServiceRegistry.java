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

	/**
	 * Adds a class of service after checking the BRi Norm
	 * 
	 * @param classe the service to be add
	 * @throws NormBRiException
	 */
	public static void addService(Class<?> classe) throws NormBRiException {
		System.out.println("Trying add new service : " + classe.getName());
		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		// TODO à Finir
		if(isNormBRi(classe)) {
			System.out.println("Adding Success");
			servicesClasses.add(classe);
		} else {
			System.out.println("Adding Fail");
		}

	}

	/**
	 * Returns the service class at index (numService - 1) of the list of registered
	 * services.
	 * 
	 * @param numService (the service index number) + 1
	 * @return
	 */
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}

	/**
	 * lists the activities present in the list of available services
	 * 
	 * @return lists the activities available
	 */
	public static String toStringue() {
		StringBuilder result = new StringBuilder("Activités présentes :##");
		if(servicesClasses.isEmpty())
			result.append("Aucun service disponible !##");
		else {
			int i = 1;
			for (Class<?> service : servicesClasses) {
				result.append(i++ + ") " + service.getName() + "##");
			}
		}
		return result.toString();
	}

	/**
	 * Checks if the class respects the bri norm
	 * 
	 * @param classe
	 * @return true if all the rules are respected else throw a NormBRiException
	 *         with a message indicating the reason for failure
	 * @throws NormBRiException
	 */
	private static boolean isNormBRi(Class<?> classe) throws NormBRiException {
		int modifiers = classe.getModifiers();
		String className = "[BRi Norm] " + classe.getName();
		StringBuilder errMsg = new StringBuilder();

		// ne pas être abstract
		if(Modifier.isAbstract(modifiers)) {
			errMsg.append(className + " should not be abstract.##");
		} else if(!Modifier.isPublic(modifiers)) {
			errMsg.append(className + " is not public.##");
		}

		// implémenter l'interface bri.Service
		Class<?>[] interfaceClasses = classe.getInterfaces();
		if(interfaceClasses.length != 0) {
			boolean containService = false;
			for (Class<?> aClass : interfaceClasses) {
				if(aClass.getName().equals(Service.class.getName())) {
					containService = true;
					break;
				}
			}
			if(!containService) {
				errMsg.append(className + " no implement Service.##");
			}
		}

		// avoir un constructeur public (Socket) sans exception
		Constructor<?>[] constructors = classe.getConstructors();
		boolean containSocketConstructor = false;
		for (Constructor<?> aConstructor : constructors) {
			if(aConstructor.getParameterCount() == 1 && Modifier.isPublic(aConstructor.getModifiers())
					&& aConstructor.getExceptionTypes().length == 0) {
				for (Class<?> aParameterType : aConstructor.getParameterTypes()) {
					if(aParameterType.getName().equals(Socket.class.getName())) {
						containSocketConstructor = true;
					}
				}
			}
		}
		if(!containSocketConstructor) {
			errMsg.append(className + " do not have public constructor with Socket parameter.##");
		}

		// avoir une méthode public static String toStringue() sans exception
		Method[] methods = classe.getMethods();
		boolean containToStringue = false;
		for (Method aMethod : methods) {
			if(aMethod.getName().equals("toStringue") && Modifier.isPublic(aMethod.getModifiers())
					&& Modifier.isStatic(aMethod.getModifiers())
					&& aMethod.getReturnType().getName().equals(String.class.getName())
					&& aMethod.getExceptionTypes().length == 0) {

				containToStringue = true;
				break;
			}
		}
		if(!containToStringue) {
			errMsg.append(className + " do not have a public static String toStringue() method without exception.##");
		}

		// avoir un attribut Socket private final
		Field[] fields = classe.getDeclaredFields();
		boolean containSocket = false;
		for (Field aField : fields) {
			if(aField.getType().getName().equals(Socket.class.getName()) && Modifier.isPrivate(aField.getModifiers())
					&& Modifier.isFinal(aField.getModifiers())) {
				containSocket = true;
			}
		}
		if(!containSocket) {
			errMsg.append(className + " do not have private final Socket field.##");
		}

		if(errMsg.toString().isBlank())
			return true;

		throw new NormBRiException(errMsg.toString());
	}

	/**
	 * update a service in the list of services
	 * 
	 * @param serviceClass the service to be update
	 */
	public static void updateService(Class<?> serviceClass) {
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

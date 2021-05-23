package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

/**
 * This class is a register of services shared in competition by customers and
 * service like "add", "update", etc.
 *
 */
public class ServiceRegistry {

	static {
		servicesClasses = new Vector<Class<?>>();
	}
	private static List<Class<?>> servicesClasses;

	/**
	 * Returns the service class at index (numService - 1) of the list of registered
	 * services.
	 * 
	 * @param numService - (the service index number) + 1
	 * @return the service class at index (numService - 1)
	 */
	public static Class<?> getServiceClass(int numService) throws IndexOutOfBoundsException {
		synchronized (servicesClasses) {
			return servicesClasses.get(numService - 1);
		}
	}

	/**
	 * Adds a class of service after checking the BRi Norm through introspection. If
	 * compliant, add to the list of registered services. Else throw exception with
	 * clear message.
	 * 
	 * @param classe - the service to be add
	 * @throws Exception        if service already present in the register
	 * @throws NormBRiException Msg explaining the reason for Norm Bri failure
	 */
	public static void addService(Class<?> classe) throws Exception, NormBRiException {
		System.out.println("Trying add new service : " + classe.getName());
		synchronized (servicesClasses) {
			for (Class<?> aClass : servicesClasses) {
				if(aClass.getName().equals(classe.getName()))
					throw new Exception("Can't add! " + classe.getName() + " already present, try to update it instead");
			}
		}
		if(isNormBRi(classe)) {
			synchronized (classe) {
				servicesClasses.add(classe);
			}
			System.out.println("Addition Success");
		}
	}

	/**
	 * Update a service in the list of services
	 * 
	 * @param classe - the service to be update
	 * @throws Exception if service not present in the register
	 */
	public static void updateService(Class<?> classe) throws Exception {
		System.out.println("Trying update service : " + classe.getName());
		Class<?> classToUpdate = null;
		// check if the service is present in the register6
		synchronized (servicesClasses) {
			for (Class<?> aClass : servicesClasses) {
				if(aClass.getName().equals(classe.getName())) {
					classToUpdate = aClass;
					break;
				}
			}
		}
		if(classToUpdate == null)
			throw new Exception(classe.getName() + " not found in registry, try adding it");
		// check BRi norm
		if(isNormBRi(classe)) {
			synchronized (servicesClasses) {
				servicesClasses.set(servicesClasses.indexOf(classToUpdate), classe);
			}
		}
		System.out.println("Update Success");
	}

	/**
	 * lists the activities present in the list of available services
	 * 
	 * @return lists the activities available
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String toStringue() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		StringBuilder result = new StringBuilder("Available Activities :##");
		synchronized (servicesClasses) {
			if(servicesClasses.isEmpty())
				result.append("No services available !##");
			else {
				int i = 1;
				for (Class<?> service : servicesClasses) {
					Method toStringue = service.getMethod("toStringue");
					result.append(i++ + ") " + service.getName() + " => " + toStringue.invoke(null) + "##");
				}
			}
		}

		return result.toString();
	}

	/**
	 * Checks if the class respects the bri norm
	 * 
	 * @param classe - the class to be checked
	 * @return true if all the rules are respected else throw a NormBRiException
	 *         with a message indicating the reason for failure
	 * @throws NormBRiException Msg explaining the reason for failure
	 */
	private static boolean isNormBRi(Class<?> classe) throws NormBRiException {
		int modifiers = classe.getModifiers();
		String className = "[BRi Norm] " + classe.getName();
		StringBuilder errMsg = new StringBuilder();

		// not be abstract
		if(Modifier.isAbstract(modifiers)) {
			errMsg.append(className + " should not be abstract.##");
		} else if(!Modifier.isPublic(modifiers)) {
			errMsg.append(className + " is not public.##");
		}

		// implement the bri.Service interface
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

		// have a public constructor (Socket) with no exceptions
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

		// have a public static String toString() method without exception
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

		// have private final Socket field
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

		System.out.println(className + " is not compliant.");
		throw new NormBRiException(errMsg.toString());
	}

	// OPTIONAL
//	public void startService(String serviceName) {}
//	
//	public void stopService(String serviceName) {}
//	
//	public void deleteService(String serviceName) {}

}

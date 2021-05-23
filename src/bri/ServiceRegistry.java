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
 * service like "add", "update", etc. Register of services is divided into 2
 * parts, the first part represents the started and available services to
 * amateurs, and the second part represents the stopped services. The separation
 * between these 2 parties is indicated by the method firstStoppedServiceIdx()
 */
public class ServiceRegistry {

	static {
		servicesClasses = new Vector<Class<?>>();
		nbStoppedServices = 0;
	}
	private static List<Class<?>> servicesClasses;
	private static int nbStoppedServices;

	/**
	 * Index indicating the beginning for the stopped services in the register
	 * 
	 * @return the index of the 1st service which is stopped
	 */
	public static int firstStoppedServiceIdx() {
		synchronized (servicesClasses) {
			int i = servicesClasses.size() - nbStoppedServices;
			return (i < 0) ? 0 : i;
		}
	};

	/**
	 * Returns the service class at index (numService - 1) of the list of available
	 * services
	 * 
	 * @param numService - (the service index number) + 1
	 * @return the service class at index (numService - 1)
	 * @throws IndexOutOfBoundsException
	 */
	public static Class<?> getServiceClass(int numService) throws IndexOutOfBoundsException {
		int index = numService - 1;
		synchronized (servicesClasses) {
			if(index >= 0 && index < firstStoppedServiceIdx())
				return servicesClasses.get(index);
			throw new IndexOutOfBoundsException("No service corresponding to this number");
		}
	}

	/**
	 * Adds a class of service after checking the BRi Norm through introspection. If
	 * compliant, add to the list of registered services. Else throw exception with
	 * clear message.
	 * 
	 * @param classe - the service class to be add
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
				servicesClasses.add(firstStoppedServiceIdx(), classe);
			}
			System.out.println("Addition Success");
		}
	}

	/**
	 * Update a service in the register of services
	 * 
	 * @param classe - the service class to be update
	 * @throws Exception if service not present in the register
	 */
	public static void updateService(Class<?> classe) throws Exception {
		System.out.println("Trying update service : " + classe.getName());
		Class<?> classToUpdate = null;
		// check if the service is present in the register
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
	 * Start a service in the register of stopped services. The started service is
	 * move just before the first stopped service in the register. A
	 * 
	 * @param classeName - the service class name to start
	 * @throws Exception if service not present in the stopped service register or
	 *                   starting failed
	 */
	public static void startService(String classeName) throws Exception {
		System.out.println("Trying start service : " + classeName);
		Class<?> service = null;
		synchronized (servicesClasses) {
			// loop inside the stopped service register
			for (int i = firstStoppedServiceIdx(); i < servicesClasses.size(); i++) {
				service = servicesClasses.get(i);
				if(service.getName().equals(classeName)) {
					try {
						servicesClasses.remove(service);
						servicesClasses.add(firstStoppedServiceIdx(), service);
						nbStoppedServices--;
						System.out.println("Starting Success");
						return;
					} catch(Exception e) {
						throw new Exception("Can't start " + classeName + " : " + e.getMessage());
					}
				}
			}
		}
		throw new Exception(
				classeName + " not found in registry of stopped service, maybe doesn't exist or already started ");
	}

	/**
	 * Stop a service in the register of available services. The stopped service is
	 * move the end of the register
	 * 
	 * @param classeName - the service class name to stop
	 * @throws Exception if service not present in the available service register or
	 *                   stop failed
	 */
	public static void stopService(String classeName) throws Exception {
		System.out.println("Trying stop service : " + classeName);
		Class<?> service = null;
		synchronized (servicesClasses) {
			// loop inside the register without taking into account the arrested services
			for (int i = 0; i < firstStoppedServiceIdx(); i++) {
				service = servicesClasses.get(i);
				if(service.getName().equals(classeName)) {
					try { // if find, move the class to be stopped at the end of the register
						servicesClasses.remove(service);
						servicesClasses.add(service);
						nbStoppedServices++;
						System.out.println("Stopping Success");
						return;
					} catch(Exception e) {
						throw new Exception("Can't stop " + classeName + " : " + e.getMessage());
					}
				}
			}
		}
		throw new Exception(
				classeName + " not found in registry of available service, maybe doesn't exist or already stopped ");
	}

	/**
	 * Delete a service in the register of services
	 * 
	 * @param classeName - the service class name to delete
	 * @throws Exception if service not present in the register or deletion failed
	 */
	public static void deleteService(String classeName) throws Exception {
		System.out.println("Trying delete service : " + classeName);
		synchronized (servicesClasses) {
			for (Class<?> service : servicesClasses) {
				if(service.getName().equals(classeName)) {
					try {
						servicesClasses.remove(service);
						System.out.println("Delete Success");
						return;
					} catch(ClassCastException | NullPointerException | UnsupportedOperationException e) {
						throw new Exception("Can't delete " + classeName + " : " + e.getMessage());
					}
				}
			}
		}
		throw new Exception(classeName + " not found in registry, no need to delete it");
	}

	/**
	 * lists all available activities present in the register of services
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
			if(servicesClasses.isEmpty() || firstStoppedServiceIdx() == 0)
				result.append("No services available !##");
			else {
				for (int i = 0; i < firstStoppedServiceIdx(); i++) {
					Method toStringue = servicesClasses.get(i).getMethod("toStringue");
					result.append((i + 1) + ") " + servicesClasses.get(i).getName() + " => " + toStringue.invoke(null) + "##");
				}
			}
		}
		return result.toString();
	}

	/**
	 * lists the stopped activities in the register of services
	 * 
	 * @return lists the stopped activities
	 */
	public static String toStringueStoppedService() {
		StringBuilder result = new StringBuilder("Stopped Activities :##");
		synchronized (servicesClasses) {
			if(servicesClasses.isEmpty() || nbStoppedServices == 0)
				result.append("No stopped services !##");
			else {
				for (int i = firstStoppedServiceIdx(); i < servicesClasses.size(); i++) {
					result.append("[x_x] " + servicesClasses.get(i).getName() + "##");
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
}

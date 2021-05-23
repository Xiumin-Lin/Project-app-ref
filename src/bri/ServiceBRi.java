package bri;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

class ServiceBRi implements Runnable {

	private Socket client; // amateur socket
	// network allowing the client to communicate with the server
	private Communication net;
	private Boolean clientIsExit;

	public ServiceBRi(Socket socket) {
		client = socket;
		net = null;
		clientIsExit = false;
	}

	@Override
	public void run() {

		try {
			System.out.println("[Thread ServiceBRi] Connected : " + Thread.currentThread().getName()); // Debug
			net = new Communication(client);

			net.write("Hello and Welcome to the BRi Service for amateur !##");

			do {
				// Sends the list of available services
				net.send("##" + ServiceRegistry.toStringue() + "0) <Exit>####Please enter the number of the desired service :");
				try {
					// instantiate the service number "choice" by passing it the "client" socket
					// and then invoke run() for this instance
					int choice = Integer.parseInt(net.readLine());
					if(choice != 0) {
						Class<?> service = ServiceRegistry.getServiceClass(choice);
						Service newService = (Service) service.getDeclaredConstructor(Socket.class).newInstance(client);
						newService.run();
					} else
						exit();

				} catch(NumberFormatException e) {
					net.write("[ERROR] Need a integer : " + e.getMessage() + "##");
				} catch(IndexOutOfBoundsException e) {
					net.write("[ERROR] Invalid number : " + e.getMessage() + "##");
				} catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					net.write("[ERROR] Service New Instance : " + e.getMessage() + "##");
				}
			} while(!this.client.isClosed() && !this.clientIsExit);

		} catch(Exception e) { // End of service
			System.err.println("[ERROR] Service BRi : " + e.getMessage());
			e.getStackTrace();
		}

		try { // Close socket & communication
			client.close();
			net.close();
			System.out.println("[Thread ServiceBRi] Closed : " + Thread.currentThread().getName()); // Debug
		} catch(IOException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Communication allowing the user to leave the service
	 */
	private void exit() {
		net.send("");
		this.clientIsExit = true;
	}

	@Override
	protected void finalize() throws Throwable {
		client.close();
	}

	// launch of the service
	public void start() {
		(new Thread(this)).start();
	}
}

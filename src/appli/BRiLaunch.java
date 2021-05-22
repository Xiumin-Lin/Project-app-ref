package appli;

import java.net.MalformedURLException;

import bri.Port;
import bri.ServeurBRi;

public class BRiLaunch {
	public final static int PORT_PROG = Port.PROG.getNumber();
	public final static int PORT_AMA = Port.AMATEUR.getNumber();

	public static void main(String[] args) throws MalformedURLException {
		System.out.println("[Thread BriLaunch] : " + Thread.currentThread().getName()); // Debug
		System.out.println("LANCEMENT DU SERVICE BRI");

		new Thread(new ServeurBRi(PORT_PROG)).start();
		new Thread(new ServeurBRi(PORT_AMA)).start();
	}
}

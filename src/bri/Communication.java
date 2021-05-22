package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Communication implements AutoCloseable {
	private BufferedReader in;
	private PrintWriter out;
	private StringBuilder msg; // the message to be send

	public Communication(Socket s) throws IOException {
		this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.out = new PrintWriter(s.getOutputStream(), true);
		this.msg = new StringBuilder();
	}

	/**
	 * Write a msg to be sent to the machine whose connection has been made by the
	 * socket. If a string is already present in the msg, the new string is append
	 * at the end of the previous one
	 * 
	 * @param s the string to be add
	 */
	public void write(String s) {
		msg.append(s);
	}

	/**
	 * Clean the message to be send
	 */
	public void clear() {
		this.msg.setLength(0);
	}

	/**
	 * Sends the message that was written by the method write(String s). The message
	 * is clear after sending.
	 */
	public void send() {
		out.println(msg);
		this.clear();
	}

	/**
	 * Sends the message that was written by the method write(String s) & with a
	 * final message added at the end. The message is clear after sending.
	 */
	public void send(String s) {
		this.write(s);
		this.send();
	}

	/**
	 * Work like the method readLine() of {@link BufferedReader}
	 */
	public String readLine() throws IOException {
		return in.readLine();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void close() throws IOException {
		this.in.close();
		this.out.close();
	}

}

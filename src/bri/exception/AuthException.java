package bri.exception;

public class AuthException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthException(String errMsg) {
		super(errMsg);
	}
}

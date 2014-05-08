/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar;

/**
 * @author Jonas
 *
 */
public class JSaParException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5168472944836865906L;

	/**
	 * 
	 */
	public JSaParException() {
	}

	/**
	 * @param message
	 */
	public JSaParException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public JSaParException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JSaParException(String message, Throwable cause) {
        super(message + " - " + cause.getMessage(), cause);
	}

}

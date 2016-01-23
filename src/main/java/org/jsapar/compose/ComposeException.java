/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.compose;

import org.jsapar.JSaParException;

/**
 * @author Jonas
 *
 */
public class ComposeException extends JSaParException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7837454543034050720L;

	/**
	 * 
	 */
	public ComposeException() {
	}

	/**
	 * @param arg0
	 */
	public ComposeException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ComposeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComposeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}

/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.output;

import org.jsapar.JSaParException;

/**
 * @author Jonas
 *
 */
public class OutputException extends JSaParException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7837454543034050720L;

	/**
	 * 
	 */
	public OutputException() {
	}

	/**
	 * @param arg0
	 */
	public OutputException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public OutputException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OutputException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}

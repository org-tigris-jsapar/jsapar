package org.jsapar.error;

/**
 * Actually just wraps the normal NumberFormatException so that it can be constructed with a cause.
 * @author stejon0
 *
 */
public class JSaParNumberFormatException extends NumberFormatException {

    /**
     * 
     */
    private static final long serialVersionUID = 650507666549088563L;

    public JSaParNumberFormatException() {
    }

    public JSaParNumberFormatException(String s) {
        super(s);
    }

    /**
     * @param cause
     */
    public JSaParNumberFormatException(Throwable cause) {
        super.initCause(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public JSaParNumberFormatException(String message, Throwable cause) {
        super(message + " - " + cause.getMessage());
        super.initCause(cause);
    }
    
}

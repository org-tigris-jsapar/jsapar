package org.jsapar.error;

import java.io.Serial;

/**
 * Actually just wraps the normal NumberFormatException so that it can be constructed with a cause.
 */
@SuppressWarnings("this-escape")
public class JSaParNumberFormatException extends NumberFormatException {

    @Serial
    private static final long serialVersionUID = 650507666549088563L;


    /**
     * @param message Error description
     * @param cause The cause
     */
    public JSaParNumberFormatException(String message, Throwable cause) {
        super(message + " - " + cause.getMessage());
        super.initCause(cause);
    }
    
}

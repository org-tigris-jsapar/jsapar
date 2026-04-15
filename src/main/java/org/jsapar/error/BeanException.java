package org.jsapar.error;

import java.io.Serial;

/**
 * Used when creating or assigning beans. Usually a result of an underlying cause.
 */
public class BeanException extends JSaParException {
    @Serial
    private static final long serialVersionUID = 1L;
    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

}

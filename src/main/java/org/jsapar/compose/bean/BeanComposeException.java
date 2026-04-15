package org.jsapar.compose.bean;

import java.io.Serial;

public class BeanComposeException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public BeanComposeException(String message) {
        super(message);
    }

    @SuppressWarnings("WeakerAccess")
    public BeanComposeException(String message, Throwable cause) {
        super(message, cause);
    }

}

package org.jsapar.compose.bean;

public class BeanComposeException extends Exception {
    public BeanComposeException() {
    }

    public BeanComposeException(String message) {
        super(message);
    }

    public BeanComposeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanComposeException(Throwable cause) {
        super(cause);
    }

}

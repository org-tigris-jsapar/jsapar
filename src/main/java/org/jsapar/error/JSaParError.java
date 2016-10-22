package org.jsapar.error;

/**
 * Base class for all types of errors that can be added to an {@link ErrorEvent}
 */
public class JSaParError extends RuntimeException{

    public JSaParError(String message, Throwable cause) {
        super(makeSuperMessage(message, cause), cause);
    }

    private static String makeSuperMessage(String message, Throwable cause) {
        if(message == null){
            if(cause == null)
                return null;
            return cause.getMessage();
        }
        if(cause==null || cause.getMessage()==null || message.endsWith(cause.getMessage()))
            return message;
        return message + " - " + cause.getMessage();
    }

    public JSaParError(String message) {
        super(message);
    }

}

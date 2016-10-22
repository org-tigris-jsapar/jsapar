package org.jsapar.error;

/**
 * Base class for all types of errors that can be added to an {@link ErrorEvent}
 */
public class JSaParException extends RuntimeException{

    public JSaParException(String message, Throwable cause) {
        super(makeSuperMessage(message, cause), cause);
    }

    public JSaParException(Throwable cause) {
        super(makeSuperMessage(null, cause));
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

    public JSaParException(String message) {
        super(message);
    }

}

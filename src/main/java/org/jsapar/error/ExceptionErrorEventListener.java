package org.jsapar.error;

import org.jsapar.parse.ParseException;

/**
 * This error event listener throws an unchecked exception upon the first error that occurs. This is usually the default
 * behavior unless you register any other error event listener.
 */
public class ExceptionErrorEventListener implements ErrorEventListener {

    /**
     * This implementation throws a {@link ParseException} for every call. This means that parsing/composing will be
     * aborted upon the first error if this error event listener is registered.
     * @param event The event that contains the error information.
     */
    @Override
    public void errorEvent(ErrorEvent event) {
        throw new ParseException(event.getError());
    }
}

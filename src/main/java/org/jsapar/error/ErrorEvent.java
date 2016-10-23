package org.jsapar.error;

import java.util.EventObject;

/**
 * The event that is fired in any of the implementations of {@link org.jsapar.parse.Parser},
 * {@link org.jsapar.compose.Composer} or {@link org.jsapar.Converter} in case an error occured.
 * Please register your own {@link ErrorEventListener} in any of these classes in order to deal with the errors that
 * occurs.
 */
public final class ErrorEvent extends EventObject {
    final JSaParException error;

    /**
     * Creates a new error event.
     * @param source The source class where the error event was fired.
     * @param error The error that occured.
     */
    public ErrorEvent(Object source, JSaParException error) {
        super(source);
        this.error = error;
    }

    /**
     * @return the error that occured.
     */
    public JSaParException getError() {
        return error;
    }

}

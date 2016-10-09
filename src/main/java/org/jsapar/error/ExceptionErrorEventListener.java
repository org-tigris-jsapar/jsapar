package org.jsapar.error;

import org.jsapar.parse.ParseException;

/**
 * This error event listener throws an unchecked exception upon the first error that occurs. This is usually the default
 * behavior unless you register any other error event listener.
 * Created by stejon0 on 2016-10-02.
 */
public class ExceptionErrorEventListener implements ErrorEventListener {

    @Override
    public void errorEvent(ErrorEvent event) {
        throw new ParseException(event.getError());
    }
}

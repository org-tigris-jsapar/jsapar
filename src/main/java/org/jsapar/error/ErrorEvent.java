package org.jsapar.error;

import org.jsapar.parse.LineParseError;

import java.util.EventObject;

public final class ErrorEvent extends EventObject {
    final Error error;

    public ErrorEvent(Object source, Error error) {
        super(source);
        this.error = error;
    }

    /**
     * @return the error
     */
    public Error getError() {
        return error;
    }

}

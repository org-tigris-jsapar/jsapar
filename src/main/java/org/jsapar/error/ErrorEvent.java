package org.jsapar.error;

import java.util.EventObject;

public final class ErrorEvent extends EventObject {
    final JSaParException error;

    public ErrorEvent(Object source, JSaParException error) {
        super(source);
        this.error = error;
    }

    /**
     * @return the error
     */
    public JSaParException getError() {
        return error;
    }

}

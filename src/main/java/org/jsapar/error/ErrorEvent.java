package org.jsapar.error;

import java.util.EventObject;

public final class ErrorEvent extends EventObject {
    final JSaParError error;

    public ErrorEvent(Object source, JSaParError error) {
        super(source);
        this.error = error;
    }

    /**
     * @return the error
     */
    public JSaParError getError() {
        return error;
    }

}

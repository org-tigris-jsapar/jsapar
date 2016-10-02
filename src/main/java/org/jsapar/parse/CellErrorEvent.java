package org.jsapar.parse;

import java.util.EventObject;

public final class CellErrorEvent extends EventObject {

    final CellParseError parseError;

    public CellErrorEvent(Object source, CellParseError parseError) {
        super(source);
        this.parseError = parseError;
    }

    /**
     * @return the parseError
     */
    public CellParseError getParseError() {
        return parseError;
    }

}

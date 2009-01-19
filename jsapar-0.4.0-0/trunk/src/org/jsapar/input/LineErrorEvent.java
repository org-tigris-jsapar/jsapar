package org.jsapar.input;

import java.util.EventObject;

public class LineErrorEvent extends EventObject {
    /**
     * 
     */
    private static final long serialVersionUID = -7680966645336969237L;
    CellParseError cellParseError;

    public LineErrorEvent(Object source, CellParseError cellParseError){
	super(source);
	this.cellParseError = cellParseError;
    }

    /**
     * @return the cellParseError
     */
    public CellParseError getCellParseError() {
        return cellParseError;
    }

    /**
     * @param cellParseError the cellParseError to set
     */
    public void setCellParseError(CellParseError cellParseError) {
        this.cellParseError = cellParseError;
    }
}

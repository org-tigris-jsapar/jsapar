package org.jsapar.error;

import org.jsapar.parse.CellErrorEvent;
import org.jsapar.parse.ErrorEventListener;
import org.jsapar.parse.LineErrorEvent;
import org.jsapar.parse.ParseException;

/**
 * Created by stejon0 on 2016-10-02.
 */
public class ExceptionErrorEventListener implements ErrorEventListener {

    @Override
    public void cellErrorEvent(CellErrorEvent event) {
        throw new ParseException(event.getParseError());
    }

    @Override
    public void lineErrorEvent(LineErrorEvent event) {
        throw new ParseException(event.getParseError());
    }
}

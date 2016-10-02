package org.jsapar.error;

import org.jsapar.parse.CellErrorEvent;
import org.jsapar.parse.ErrorEventListener;
import org.jsapar.parse.LineErrorEvent;
import org.jsapar.parse.ParseError;

import java.util.List;

/**
 * Created by stejon0 on 2016-10-02.
 */
public class RecordingErrorEventListener implements ErrorEventListener{
    private List<ParseError> errors;

    public RecordingErrorEventListener(List<ParseError> errors) {
        this.errors = errors;
    }

    @Override
    public void cellErrorEvent(CellErrorEvent event) {
        errors.add(event.getParseError());
    }

    @Override
    public void lineErrorEvent(LineErrorEvent event) {
        errors.add(event.getParseError());
    }

    public List<ParseError> getErrors() {
        return errors;
    }
}

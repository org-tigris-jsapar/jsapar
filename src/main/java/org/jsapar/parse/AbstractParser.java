package org.jsapar.parse;

import org.jsapar.Parser;
import org.jsapar.error.ExceptionErrorEventListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by stejon0 on 2016-08-14.
 */
public abstract class AbstractParser implements Parser, LineEventListener, ErrorEventListener{
    private List<LineEventListener> parsingEventListeners = new LinkedList<>();
    private List<ErrorEventListener> errorEventListeners = new LinkedList<>();
    private ErrorEventListener defaultErrorEventListener = new ExceptionErrorEventListener();


    @Override
    public void addLineEventListener(LineEventListener eventListener) {
        if (eventListener == null)
            return;
        this.parsingEventListeners.add(eventListener);
    }

    @Override
    public void addErrorEventListener(ErrorEventListener errorEventListener) {
        if (errorEventListener == null)
            return;
        this.errorEventListeners.add(errorEventListener);

    }

    @Override
    public void lineParsedEvent(LineParsedEvent event) {
        for (LineEventListener l : this.parsingEventListeners) {
            l.lineParsedEvent(event);
        }

    }

    @Override
    public void lineErrorEvent(LineErrorEvent event) {
        if(errorEventListeners.isEmpty()){
            defaultErrorEventListener.lineErrorEvent(event);
            return;
        }
        for (ErrorEventListener l : this.errorEventListeners) {
            l.lineErrorEvent(event);
        }
    }

    @Override
    public void cellErrorEvent(CellErrorEvent event) {
        if(errorEventListeners.isEmpty()){
            defaultErrorEventListener.cellErrorEvent(event);
            return;
        }
        for (ErrorEventListener l : this.errorEventListeners) {
            l.cellErrorEvent(event);
        }
    }
}

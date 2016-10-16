package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ErrorEventSource;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by stejon0 on 2016-08-14.
 */
public abstract class AbstractParser implements Parser, LineEventListener, ErrorEventListener {
    private List<LineEventListener> parsingEventListeners     = new LinkedList<>();
    private ErrorEventSource        errorEventSource          = new ErrorEventSource();


    @Override
    public void addLineEventListener(LineEventListener eventListener) {
        if (eventListener == null)
            return;
        this.parsingEventListeners.add(eventListener);
    }

    @Override
    public void addErrorEventListener(ErrorEventListener errorEventListener) {
        errorEventSource.addEventListener(errorEventListener);
    }

    @Override
    public void lineParsedEvent(LineParsedEvent event) {
        for (LineEventListener l : this.parsingEventListeners) {
            l.lineParsedEvent(event);
        }

    }

    @Override
    public void errorEvent(ErrorEvent event) {
        errorEventSource.errorEvent(event);
    }

}

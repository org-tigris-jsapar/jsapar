package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ErrorEventSource;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract implementation of {@link Parser} interface. Provides possibility to have multiple line event listeners and
 * error event listeners. Override this class to implement a specific parser.
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

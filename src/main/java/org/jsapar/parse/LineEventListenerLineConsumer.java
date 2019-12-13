package org.jsapar.parse;

import org.jsapar.model.Line;

import java.util.function.Consumer;

/**
 * Acts as an adapter between the legacy {@link LineEventListener} and the new approach using {@link Consumer}.
 */
@Deprecated
public class LineEventListenerLineConsumer implements Consumer<Line> {
    private final  LineEventListener eventListener;

    public LineEventListenerLineConsumer(LineEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void accept(Line l) {
        eventListener.lineParsedEvent(new LineParsedEvent(this, l));
    }
}

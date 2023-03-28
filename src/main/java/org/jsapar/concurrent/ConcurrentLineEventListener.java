package org.jsapar.concurrent;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseTask;

/**
 * Makes it possible to handle line events in a different thread than the {@link ParseTask}. Please note
 * that neither the {@link LineParsedEvent} or the {@link org.jsapar.model.Line} classes
 * are internally thread safe so if you have more than one event listeners registered in a chain after this listener
 * (by using {@link org.jsapar.parse.MulticastLineEventListener}) , all accesses to the events within these event
 * listeners needs to be synchronized on
 * the event object. As long as you have only one event listener registered, no external synchronization is needed.
 * <p>
 * @implNote This implementation  acts as a decorator which means that you initialize it with an actual line event listener that
 * gets called from the consumer thread each time there is a line parse event in the producer thread.
 * <p>
 * If a worker thread event listener should throw an exception, the worker thread is immediately terminated and the
 * exception is encapsulated in a {@link JSaParException} and forwarded to the calling thread upon first available occasion.
 * <p>
 * When the internal queue is full, the producing thread starts blocking. This means that it waits for an available slot
 * in the queue before it continues parsing.
 * <p>
 * Deprecated since 2.2. Use {@link ConcurrentConsumer instead.}
 */
@Deprecated
public class ConcurrentLineEventListener extends ConcurrentConsumer<LineParsedEvent> implements LineEventListener {

    /**
     * Creates a concurrent line event listener that have a queue size of 1000 events.
     * @param lineEventListener The line event listener that will be called by consumer thread.
     */
    public ConcurrentLineEventListener(LineEventListener lineEventListener) {
        this(lineEventListener, 1000);
    }

    /**
     * Creates a concurrent line event listener with specified queue size.
     * @param lineEventListener The line event listener that will be called by consumer thread.
     * @param queueSize   Maximum size of the queue before the producing thread starts blocking.
     */
    public ConcurrentLineEventListener(LineEventListener lineEventListener, int queueSize) {
        super(lineEventListener::lineParsedEvent, queueSize);
    }

    @Override
    public void lineParsedEvent(LineParsedEvent event)  {
        this.accept(event);
    }
}

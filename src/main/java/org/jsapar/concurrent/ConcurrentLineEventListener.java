package org.jsapar.concurrent;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Makes it possible to handle line events in a different thread than the {@link org.jsapar.parse.Parser}. Please note
 * that neither the {@link LineParsedEvent} or the {@link org.jsapar.model.Line} classes
 * are internally thread safe so if you have more than one event listeners registered to the same
 * {@link org.jsapar.parse.Parser}, all accesses to the events within these event listeners needs to be synchronized on
 * the event object. As long as you have only one event listener registered, no external synchronization is needed.
 * <p>
 * This implementation acts as a mediator which means that you may register multiple line event listeners to an instance
 * of this class and they will all be called in the same order as they were registered and all within the same worker
 * thread. If any of the registered event listeners is an instance of this class, external synchronization of the events
 * will be needed as stated above.
 *
 * If a worker thread event listener should throw an exception, the worker thread is immediately terminated and the
 * excption is encapsulated in a {@link JSaParException} and forwarded to the calling thread upon first available occation.
 */
@SuppressWarnings("ALL")
public class ConcurrentLineEventListener implements LineEventListener, AutoCloseable, Stoppable {

    private BlockingQueue<LineParsedEvent> events;
    private volatile boolean                 shouldStop = false;
    private volatile boolean                 running    = false;
    private          List<LineEventListener> listeners  = new ArrayList<>();
    private          Throwable               exception  = null;
    private Thread thread;
    private ShutdownHook shutdownHook;

    /**
     * Creates a concurrent line event listener that have a queue size of 10000 events.
     */
    public ConcurrentLineEventListener() {
        this(10000);
    }

    /**
     * Creates a concurrent line event listener with specified queue size.
     * @param queueSize   Maximum size of the queue before the producing thread starts blocking.
     */
    public ConcurrentLineEventListener(int queueSize) {
        events = new LinkedBlockingQueue<>(queueSize);
        this.shutdownHook = new ShutdownHook(this);
    }

    @Override
    public void lineParsedEvent(LineParsedEvent event) throws IOException {
        checkException();
        try {
            events.put(event);
        } catch (InterruptedException e) {
        }
    }

    private void checkException() {
        synchronized (this) {
            if (exception != null) {
                try {
                    throw new JSaParException("Exception in concurrent event listening thread", exception);
                } finally {
                    exception = null;
                }
            }
        }
    }

    /**
     * Adds a line event listener to the working thread. If multiple event listeners are added to the same instance of
     * this class, they will all receive the event in the order they were added and all of them will be called by the
     * same thread, the working thread of this instance.
     *
     * @param eventListener The event listener to add.
     */
    public void addLineEventListener(LineEventListener eventListener) {
        listeners.add(eventListener);
    }

    private class WorkingThread implements Runnable {
        @Override
        public void run() {
            try {
                running = true;
                while (!shouldStop) {
                    LineParsedEvent event = events.take();
                    if (shouldStop) {
                        return;
                    }
                    if (event != null && event.getLine() != null) {
                        for (LineEventListener listener : listeners) {
                            listener.lineParsedEvent(event);
                        }
                    }
                }
            } catch (InterruptedException e) {
                // Gracefully and silently terminate.
            } catch (Throwable e) {
                synchronized (ConcurrentLineEventListener.this) {
                    exception = e;
                    shouldStop = true;
                }
            } finally {
                running = false;
            }
        }
    }

    /**
     * Starts worker thread. If worker thread is already running, calls to this method have no effect. This method does
     * not return until the worker thread is actually running.
     */
    public void start() {
        if(isRunning())
            return;
        thread = new Thread(new WorkingThread());
        thread.start();
        while (!isRunning() && !shouldStop)
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
            }

    }

    /**
     * Terminate the working thread as soon as possible but without causing any error. The event currently being
     * processed by the working thread will be completed but then the thread will terminate.
     * @throws JSaParException if the working thread has terminated due to an exception.
     */
    public void stop() throws JSaParException {
        this.shouldStop = true;
        try {
            if(isRunning())
                events.put(new LineParsedEvent(this, null)); // Make sure the blocking is released immediately
        } catch (InterruptedException e) {
        }
        if(Thread.currentThread() != thread)
            checkException();
    }

    /**
     * Waits for the working thread to handle all pending events, then gracefully terminates the working thread.
     *
     * @throws JSaParException if the working thread has terminated due to an exception.
     */
    @Override
    public void close() throws IllegalMonitorStateException {
        try {
            if(Thread.currentThread() != this.thread) {
                while (running && !events.isEmpty()) {
                    Thread.sleep(100L);
                }
            }
            stop();
            if(Thread.currentThread() != this.thread) {
                while (running) {
                    Thread.sleep(100L);
                }
            }
        } catch (InterruptedException e) {
        }
        events.clear();
    }

    /**
     * @return Number of events in queue
     */
    public int size() {
        return events.size();
    }

    /**
     * @return True if there are no events waiting in queue.
     */
    public boolean isEmpty() {
        return events.isEmpty();
    }

    /**
     * @return True if the worker thread is running, false if it was never started or has terminated.
     */
    public boolean isRunning() {
        return running;
    }
}

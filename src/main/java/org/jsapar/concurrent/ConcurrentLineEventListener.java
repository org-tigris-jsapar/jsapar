package org.jsapar.concurrent;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseTask;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Makes it possible to handle line events in a different thread than the {@link ParseTask}. Please note
 * that neither the {@link LineParsedEvent} or the {@link org.jsapar.model.Line} classes
 * are internally thread safe so if you have more than one event listeners registered in a chain after this listener
 * (by using {@link org.jsapar.parse.MulticastLineEventListener}) , all accesses to the events within these event
 * listeners needs to be synchronized on
 * the event object. As long as you have only one event listener registered, no external synchronization is needed.
 * <p>
 * This implementation acts as a decorator which means that you initialize it with an actual line event listener that
 * gets called from the consumer thread each time there is a line parse event in the producer thread.
 * <p>
 * If a worker thread event listener should throw an exception, the worker thread is immediately terminated and the
 * excption is encapsulated in a {@link JSaParException} and forwarded to the calling thread upon first available occation.
 * <p>
 * When the internal queue is full, the producing thread starts blocking. This means that it waits for an available slot
 * in the queue before it continues parsing.
 */
@SuppressWarnings("ALL")
public class ConcurrentLineEventListener implements LineEventListener, AutoCloseable, Stoppable {

    private BlockingQueue<LineParsedEvent> events;
    private volatile boolean shouldStop = false;
    private volatile boolean running = false;
    private LineEventListener listener;
    private Throwable exception = null;
    private Thread thread;
    private ShutdownHook shutdownHook;

    /**
     * Creates a concurrent line event listener that have a queue size of 1000 events.
     */
    public ConcurrentLineEventListener(LineEventListener lineEventListener) {
        this(lineEventListener, 1000);
    }

    /**
     * Creates a concurrent line event listener with specified queue size.
     * @param lineEventListener
     * @param queueSize   Maximum size of the queue before the producing thread starts blocking.
     */
    public ConcurrentLineEventListener(LineEventListener lineEventListener, int queueSize) {
        events = new LinkedBlockingQueue<>(queueSize);
        this.shutdownHook = new ShutdownHook(this);
        this.listener = lineEventListener;
    }

    @Override
    public void lineParsedEvent(LineParsedEvent event)  {
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

    private class WorkingThread implements Runnable {
        @Override
        public void run() {
            try {
                running = true;
                onStart();
                while (!shouldStop) {
                    LineParsedEvent event = events.take();
                    if (shouldStop) {
                        return;
                    }
                    // Check if it is just an event to release wait block.
                    if (event != null && event.getLine() != null) {
                        listener.lineParsedEvent(event);
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
                onStop();
            }
        }
    }

    /**
     * Called by consumer thread when it starts up but before it starts handling any event. Override this in order to
     * implement initialization needed for the new
     * thread.
     */
    protected void onStart(){
    }

    /**
     * Called by consumer thread just before it dies. Override this in order to
     * implement resource dealoccation etc. This method called also when the thread is terminated with an exception so
     * be aware that you may end up here also when a serious error has occurred.
     */
    protected void onStop(){
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
        // Wait for the consumer thread to start before returning.
        while (!isRunning() && !shouldStop)
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
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
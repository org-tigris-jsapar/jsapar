/*
 * ShutdownHook.java
 * 
 * Created on 12 December 2007 14:16
 */

/* Revision History:
 *
 * $Log: ShutdownHook.java,v $
 * Revision 1.1  2007/12/18 09:12:08  koikaj0
 * a shutdown hook to be used by thread classes using interface shutdownable
 *
 */
package org.jsapar.concurrent;

/**
 * Registers a shutdown hook on classes that implements interface Stoppable. It shuts down in
 * response to two kinds of events:
 * <ul>
 * <li>The program exits normally</li>
 * <li>In response to a user interrupt, such as typing ^C, a system-wide event such as user logoff,
 * or system shutdown.</li>
 * </ul>
 * <br/>
 * Build your runnable classes as:
 * 
 * <pre>
 * {@code
 * public class MyClass implements Runnable, Stoppable
 * {
 *     ...
 *     private ShutdownHook myShutdownHook = null;
 *     ...
 *     public MyClass()
 *     {
 *        ...
 *        // Add shutdown hook
 *        myShutdownHook = new ShutdownHook(this);
 *        ...
 *     }
 * }
 * }
 * </pre>
 * 
 */
public class ShutdownHook extends Thread {

    private Stoppable stoppable;

    /**
     * Use this method to instanciate a ShutDownHook.
     * @param stoppable The runnable thread to be able to shut down.
     */
    public ShutdownHook(Stoppable stoppable) {
        this.stoppable = stoppable;
        Runtime.getRuntime().addShutdownHook(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        stoppable.stop();
    }
}

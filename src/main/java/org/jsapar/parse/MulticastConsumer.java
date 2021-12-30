package org.jsapar.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Distributes all events to multiple {@link Consumer<T>} instances. All registered listeners
 * are called one by one from the same thread.
 */
public class MulticastConsumer<T> implements Consumer<T> {

    private final List<Consumer<T> > lineEventListeners = new LinkedList<>();


    public void addConsumer(Consumer<T> eventListener) {
        if (eventListener == null)
            return;
        this.lineEventListeners.add(eventListener);
    }

    public void removeLineEventListener(Consumer<T> lineEventListener){
        this.lineEventListeners.remove(lineEventListener);
    }

    /**
     * Will call each registered line event listener one by one in order of registration.
     * @param event The event to distribute.
     */
    @Override
    public void accept(T event)  {
        this.lineEventListeners.forEach(l->l.accept(event));
    }

    /**
     * @return Number of registered listeners
     */
    public int size(){
        return lineEventListeners.size();
    }

    /**
     * @return true if no event listeners are registered, false if there is at least one listener registered.
     */
    public boolean isEmpty(){
        return lineEventListeners.isEmpty();
    }

}

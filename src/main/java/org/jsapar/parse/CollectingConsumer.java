package org.jsapar.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Records all calls to this consumer into a list so that they can be retrieved with the method {@link #getCollected()} later.
 */
public class CollectingConsumer<T> implements Consumer<T> {
    private final List<T> collected;

    /**
     * Creates an error event listener that adds collected to the supplied list
     * @param collected The list that collected will be added to.
     */
    public CollectingConsumer(List<T> collected) {
        this.collected = collected;
    }

    /**
     * Creates an error event listener where the error list needs to be fetched with {@link #getCollected()} afterwards.
     */
    public CollectingConsumer() {
        this.collected = new ArrayList<>();
    }

    /**
     * Called when there is an error while parsing input or composing output. This implementation saves the collected in
     * a member list. The list is protected by a semaphore/synchronized block in case more than one thread is writing
     * to the same list. The list itself is used as the semaphore.
     *
     * @param event The event that contains the error information.
     */
    @Override
    public void accept(T event) {
        synchronized(this) {
            collected.add(event);
        }
    }

    /**
     * @return A list of collected recorded collected that has occurred.
     */
    public List<T> getCollected() {
        return collected;
    }

    /**
     * Clears collected recorded collected from this instance. It is usually better to create a new instance of this class than
     * to call this method.
     */
    public void clear(){
        this.collected.clear();
    }

    /**
     * @return True if there were no collected recorded.
     */
    public boolean isEmpty(){
        return collected.isEmpty();
    }

    /**
     * @return Number of collected recorded.
     */
    public int size() {
        return collected.size();
    }

}

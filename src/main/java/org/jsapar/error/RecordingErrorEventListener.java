package org.jsapar.error;

import org.jsapar.parse.CollectingConsumer;

import java.util.List;

/**
 * Records all errors into a list of errors that can be retrieved with the method {@link #getErrors()} later.
 * <p>
 * Deprecated since 2.2. Use {@link CollectingConsumer} instead.
 */
@Deprecated
public class RecordingErrorEventListener extends CollectingConsumer<JSaParException> implements ErrorEventListener {

    /**
     * Creates an error event listener that adds errors to the supplied list
     *
     * @param errors The list that errors will be added to.
     */
    public RecordingErrorEventListener(List<JSaParException> errors) {
        super(errors);
    }

    /**
     * Creates an error event listener where the error list needs to be fetched with {@link #getErrors()} afterwards.
     */
    public RecordingErrorEventListener() {
    }

    /**
     * Called when there is an error while parsing input or composing output. This implementation saves the errors in
     * a member list. The list is protected by a semaphore/synchronized block in case more than one thread is writing
     * to the same list. The list itself is used as the semaphore.
     *
     * @param event The event that contains the error information.
     */
    @Override
    public void errorEvent(ErrorEvent event) {
        accept(event.getError());
    }

    /**
     * @return A list of all recorded errors that has occurred.
     */
    public List<JSaParException> getErrors() {
        return this.getCollected();
    }

}

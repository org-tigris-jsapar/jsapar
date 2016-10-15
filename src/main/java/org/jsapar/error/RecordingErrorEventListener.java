package org.jsapar.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Records all errors into a list of errors that can be retrieved with the method {@link #getErrors()} later.
 * Created by stejon0 on 2016-10-02.
 */
public class RecordingErrorEventListener implements ErrorEventListener{
    private List<JSaParError> errors;

    /**
     * Creates an error event listener that adds errors to the supplied list
     * @param errors The list that errors will be added to.
     */
    public RecordingErrorEventListener(List<JSaParError> errors) {
        this.errors = errors;
    }

    /**
     * Creates an error event listener where the error list needs to be fetched with {@link #getErrors()} afterwards.
     */
    public RecordingErrorEventListener() {
        this.errors = new ArrayList<>();
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
        synchronized(errors) {
            errors.add(event.getError());
        }
    }

    /**
     * @return A list of all recorded errors that has occurred.
     */
    public List<JSaParError> getErrors() {
        return errors;
    }

    /**
     * Clears all recorded errors from this instance. It is usually better to create a new instance of this class than
     * to call this method.
     */
    public void clear(){
        this.errors.clear();
    }
}

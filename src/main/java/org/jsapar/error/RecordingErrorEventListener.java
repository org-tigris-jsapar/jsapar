package org.jsapar.error;

import java.util.List;

/**
 * Records all errors into a list of errors that can be retrieved with the method {@link #getErrors()} later.
 * Created by stejon0 on 2016-10-02.
 */
public class RecordingErrorEventListener implements ErrorEventListener{
    private List<Error> errors;

    public RecordingErrorEventListener(List<Error> errors) {
        this.errors = errors;
    }

    @Override
    public void errorEvent(ErrorEvent event) {
        errors.add(event.getError());
    }

    /**
     * @return A list of all recorded errors that has occurred.
     */
    public List<Error> getErrors() {
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

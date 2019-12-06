package org.jsapar.error;

import org.jsapar.parse.CollectingConsumer;

import java.util.List;

/**
 * This error event listener records errors until it reaches a maximum number. Any errors that occurs after maximum
 * number has been reached will cause a {@link MaxErrorsExceededException} instead.
 */
public class ThresholdCollectingErrorConsumer extends CollectingConsumer<JSaParException> {
    private int maxNumberOfErrors;

    /**
     * Creates an error consumer where the error list needs to be fetched with
     * {@link CollectingConsumer#getCollected()} afterwards.
     *
     * @param maxNumberOfErrors The maximum number of errors allowed to be recorded
     */
    public ThresholdCollectingErrorConsumer(int maxNumberOfErrors) {
        this.maxNumberOfErrors = maxNumberOfErrors;
    }

    /**
     * Creates an error event listener that adds errors to the supplied list
     *
     * @param maxNumberOfErrors The maximum number of errors allowed to be recorded
     * @param errors            The list that errors will be added to.
     */
    public ThresholdCollectingErrorConsumer(int maxNumberOfErrors, List<JSaParException> errors) {
        super(errors);
        this.maxNumberOfErrors = maxNumberOfErrors;
    }

    /**
     * Called when there is an error while parsing input or composing output. This implementation saves the errors in
     * a member list until maximum number has been reached. The list is protected by a semaphore/synchronized block in
     * case more than one thread is writing
     * to the same list. The list itself is used as the semaphore.
     * Any errors that occurs after maximum
     * number has been reached will cause a {@link MaxErrorsExceededException} to be thrown.
     *
     * @param error The the error information.
     */
    @Override
    public void accept(JSaParException error) {
        synchronized(this) {
            super.accept(error);
            if (this.size() > maxNumberOfErrors)
                throw new MaxErrorsExceededException(error, getCollected());
        }
    }
}

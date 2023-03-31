package org.jsapar.error;

import java.util.function.Consumer;

/**
 * This error event listener throws an unchecked exception upon the first error that occurs. This is usually the default
 * behavior unless you register any other error event listener.
 */
public class ExceptionErrorConsumer implements Consumer<JSaParException> {

    /**
     * This implementation  throws a {@link JSaParException} or any of its subclasses for every call. This means that parsing/composing will be
     * aborted upon the first error if this error event listener is registered.
     * @param error The error information.
     */
    @Override
    public void accept(JSaParException error) {
        throw error;
    }
}

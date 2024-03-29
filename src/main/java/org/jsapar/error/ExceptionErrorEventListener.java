package org.jsapar.error;

/**
 * This error event listener throws an unchecked exception upon the first error that occurs. This is usually the default
 * behavior unless you register any other error event listener.
 *
 * Deprecated since 2.2. Use {@link ExceptionErrorConsumer} instead
 */
@Deprecated
public class ExceptionErrorEventListener implements ErrorEventListener{

    /**
     * This implementation  throws a {@link JSaParException} or any of its subclasses for every call. This means that parsing/composing will be
     * aborted upon the first error if this error event listener is registered.
     * @param event The event that contains the error information.
     */
    @Override
    public void errorEvent(ErrorEvent event) {
        throw event.getError();
    }
}

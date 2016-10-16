/**
 * 
 */
package org.jsapar.error;

import org.jsapar.parse.ParseException;

import java.util.EventListener;

/**
 * Interface for receiving event call-backs while parsing.
 * 
 * @author stejon0
 * 
 */
public interface ErrorEventListener extends EventListener {

    /**
     * Called when there is an error while parsing input or composing output. If an implementation of this method throws
     * an unchecked exception, the parsing/composing will be aborted and the exception will be passed through to the
     * original calling method.
     * 
     * @param event The event that contains the error information.
     * @throws ParseException
     * @throws MaxErrorsExceededException 
     */
    void errorEvent(ErrorEvent event);

}

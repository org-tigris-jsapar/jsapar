/**
 * 
 */
package org.jsapar.parse;

import java.util.EventListener;

import org.jsapar.JSaParException;
import org.jsapar.convert.MaxErrorsExceededException;

/**
 * Interface for receiving event call-backs while parsing.
 * 
 * @author stejon0
 * 
 */
public interface LineEventListener extends EventListener {

    /**
     * Called every time that a complete line was found in the input.
     * 
     * @param event
     *            The event that contains the parsed line.
     * @throws JSaParException
     */
    void lineParsedEvent(LineParsedEvent event) throws JSaParException;

    /**
     * Called when there is an error while parsing input. If an implementation of this method throws
     * a ParseException, the parsing will be aborted.
     * 
     * @param event The event that contains the error information.
     * @throws ParseException
     * @throws MaxErrorsExceededException 
     */
    void lineErrorEvent(LineErrorEvent event) throws ParseException, MaxErrorsExceededException;
}

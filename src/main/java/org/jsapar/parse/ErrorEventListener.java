/**
 * 
 */
package org.jsapar.parse;

import org.jsapar.JSaParException;
import org.jsapar.convert.MaxErrorsExceededException;

import java.util.EventListener;

/**
 * Interface for receiving event call-backs while parsing.
 * 
 * @author stejon0
 * 
 */
public interface ErrorEventListener extends EventListener {

    /**
     * Called when there is an error while parsing input to build a cell. If an implementation of this method throws
     * an unchecked exception, the parsing will be aborted.
     * 
     * @param event The event that contains the error information.
     * @throws ParseException
     * @throws MaxErrorsExceededException 
     */
    void cellErrorEvent(CellErrorEvent event);

    /**
     * Called when there is an error while parsing input to build a line. If an implementation of this method throws
     * an unchecked exception, the parsing will be aborted.
     *
     * @param event The event that contains the error information.
     * @throws ParseException
     * @throws MaxErrorsExceededException
     */
    void lineErrorEvent(LineErrorEvent event);
}

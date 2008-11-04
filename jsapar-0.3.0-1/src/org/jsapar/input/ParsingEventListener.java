/**
 * 
 */
package org.jsapar.input;

import java.util.EventListener;

import org.jsapar.JSaParException;

/**
 * @author stejon0
 * 
 */
public interface ParsingEventListener extends EventListener {

    /**
     * Called every time that a complete line was found in the input.
     * @param event
     * @throws JSaParException 
     */
    void lineParsedEvent(LineParsedEvent event) throws JSaParException;

    /**
     * Called when there is an error while parsing input. If an implementation
     * of this method throws a ParseException, the parsing will be aborted.
     * 
     * @param event
     * @throws ParseException
     */
    void lineErrorErrorEvent(LineErrorEvent event) throws ParseException;
}

/**
 * 
 */
package org.jsapar.parse;

import org.jsapar.JSaParException;

import java.util.EventListener;

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
    void lineParsedEvent(LineParsedEvent event) ;


}

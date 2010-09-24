/**
 * 
 */
package org.jsapar.io;

import org.jsapar.JSaParException;
import org.jsapar.Line;

/**
 * Interface used by filter converter. Can be used to insert logic to which lines should be written
 * or and which should not.
 * 
 * @author stejon0
 * 
 */
public interface LineFilter {

    /**
     * If this method returns true, the line is written to the output. If it returns false, the line
     * will be discarded.
     * 
     * @param line
     * @return true=write the line, false=discrard the line
     * @throws JSaParException
     */
    public boolean shouldWrite(Line line) throws JSaParException;

}

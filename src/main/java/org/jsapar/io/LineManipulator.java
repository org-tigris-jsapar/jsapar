/**
 * 
 */
package org.jsapar.io;

import org.jsapar.JSaParException;
import org.jsapar.Line;

/**
 * @author stejon0
 *
 */
public interface LineManipulator {
    
    public void manipulate(Line line) throws JSaParException;

}

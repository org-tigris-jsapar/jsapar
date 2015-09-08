package org.jsapar.input.parse;

import java.io.IOException;
import java.util.regex.Pattern;

import org.jsapar.JSaParException;

public interface CellSplitter {
    
    /**
     * @param sLine The line to split
     * @return An array of all cells found on the line.
     * @throws JSaParException 
     * @throws IOException 
     */
    String[] split(String sLine) throws IOException, JSaParException;
    
    

}

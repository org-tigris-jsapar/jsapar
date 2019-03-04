package org.jsapar.parse.csv;

import java.io.IOException;
import java.util.List;

interface CellSplitter {
    
    /**
     * @param sLine The line to split
     * @param toAddTo The list of strings to add found cells to
     * @return The same instance as provided in toAddTo.
     * @throws IOException In case of error in underlying io operation
     */
    List<String> split(String sLine, List<String> toAddTo) throws IOException;
    
    

}

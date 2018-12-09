package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;

import java.util.List;

/**
 * Fast cell splitter that can be used when we don't have to consider quotes.
 */
class FastCellSplitter implements CellSplitter {

    private final String cellSeparator;

    FastCellSplitter(String cellSeparator) {
        this.cellSeparator = cellSeparator;
    }

    @Override
    public List<String> split(String sLine, List<String> toAddTo) throws JSaParException {
        int nIndex = 0;
        int nFound;
        while( (nFound = sLine.indexOf(cellSeparator, nIndex))>=0){
            toAddTo.add(sLine.substring(nIndex, nFound));
            nIndex = nFound + cellSeparator.length();
        }
        toAddTo.add(sLine.substring(nIndex));
        return toAddTo;
    }

}

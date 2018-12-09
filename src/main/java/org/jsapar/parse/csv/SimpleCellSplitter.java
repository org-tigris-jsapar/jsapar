package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple cell splitter that can be used when we don't have to consider quotes.
 */
class SimpleCellSplitter implements CellSplitter {

    private final String splitPattern;

    SimpleCellSplitter(String cellSeparator) {
        this.splitPattern = Pattern.quote(cellSeparator);
    }

    @Override
    public List<String> split(String sLine, List<String> toAddTo) throws JSaParException {
        if(sLine.isEmpty())
            return toAddTo;
        String[] aLine = sLine.split(splitPattern, -1);
        if(aLine.length == 1 && aLine[0].trim().isEmpty())
            return toAddTo;

        toAddTo.addAll(Arrays.asList(aLine));
        return toAddTo;
    }

}

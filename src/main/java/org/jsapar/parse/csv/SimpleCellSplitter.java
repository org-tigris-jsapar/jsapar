package org.jsapar.parse.csv;

import java.io.IOException;
import java.util.regex.Pattern;

import org.jsapar.error.JSaParException;

public class SimpleCellSplitter implements CellSplitter {

    private static final String[] EMPTY_LINE = new String[0];
    private String splitPattern;

    public SimpleCellSplitter(String cellSeparator) {
        this.splitPattern = Pattern.quote(cellSeparator);
    }

    @Override
    public String[] split(String sLine) throws IOException, JSaParException {
        if(sLine.isEmpty())
            return EMPTY_LINE;
        String[] aLine = sLine.split(splitPattern, -1);
        if(aLine.length == 1 && aLine[0].trim().isEmpty())
            return EMPTY_LINE;
        return aLine;
    }

}

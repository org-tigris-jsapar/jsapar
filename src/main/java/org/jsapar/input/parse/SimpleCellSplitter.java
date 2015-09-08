package org.jsapar.input.parse;

import java.io.IOException;
import java.util.regex.Pattern;

import org.jsapar.JSaParException;

public class SimpleCellSplitter implements CellSplitter {

    private String splitPattern;

    public SimpleCellSplitter(String cellSeparator) {
        this.splitPattern = Pattern.quote(cellSeparator);
    }

    @Override
    public String[] split(String sLine) throws IOException, JSaParException {
        return sLine.split(splitPattern, -1);
    }

}

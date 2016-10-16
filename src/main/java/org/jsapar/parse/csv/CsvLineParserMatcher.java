package org.jsapar.parse.csv;

import org.jsapar.JSaParException;
import org.jsapar.parse.fixed.FixedWidthCellParser;
import org.jsapar.parse.fixed.FixedWidthLineParser;
import org.jsapar.schema.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stejon0 on 2016-03-12.
 */
public class CsvLineParserMatcher {
    private final CsvSchemaLine schemaLine;
    private List<CsvControlCell> controlCells = new ArrayList<>();
    private CsvLineParser lineParser;
    private int occursLeft;
    private int maxControlPos;

    public CsvLineParserMatcher(CsvSchemaLine schemaLine) {
        this.schemaLine = schemaLine;
        occursLeft = schemaLine.getOccurs();
        lineParser = new CsvLineParser(schemaLine);
        int pos = 0;
        for (CsvSchemaCell schemaCell : schemaLine.getSchemaCells()) {
            CellValueCondition lineCondition = schemaCell.getLineCondition();
            if (lineCondition != null) {
                controlCells.add(new CsvControlCell(pos, schemaCell));
            }
            maxControlPos = pos;
            pos++;
        }
    }

    public CsvLineParser makeLineParserIfMatching(CsvLineReader lineReader) throws IOException{
        if (occursLeft <= 0)
            return null;

        if (!controlCells.isEmpty()) {
            String[] cells = lineReader.readLine(schemaLine.getCellSeparator(), schemaLine.getQuoteChar());
            if(null == cells || cells.length == 0)
                return null; // Empty line
            // We only peek into the line to follow.
            try {
                if (cells.length <= maxControlPos)
                    return null;

                int read = 0;
                for (CsvControlCell controlCell : controlCells) {

                    String value = cells[controlCell.pos];
                    if (value == null)
                        return null;
                    if (!controlCell.schemaCell.getLineCondition().satisfies(value))
                        return null;
                }
            }
            finally {
                lineReader.reset();
            }
        }
        if (!schemaLine.isOccursInfinitely())
            occursLeft--;
        return lineParser;
    }

    private class CsvControlCell {
        final int           pos;
        final CsvSchemaCell schemaCell;

        public CsvControlCell(int pos, CsvSchemaCell schemaCell) {
            this.pos = pos;
            this.schemaCell = schemaCell;
        }
    }

    public boolean isOccursLeft() {
        return schemaLine.isOccursInfinitely() ? true : occursLeft > 0;
    }
}
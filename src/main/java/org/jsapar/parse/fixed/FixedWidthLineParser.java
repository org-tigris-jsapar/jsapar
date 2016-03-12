package org.jsapar.parse.fixed;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.model.Cell;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.CellParseError;
import org.jsapar.parse.LineErrorEvent;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParser;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;

public class FixedWidthLineParser  {

    private static final String  EMPTY_STRING = "";
    private FixedWidthSchemaLine lineSchema;
//    private FixedWidthCellParser cellParser   = new FixedWidthCellParser();

    public FixedWidthLineParser(FixedWidthSchemaLine lineSchema) {
        this.lineSchema = lineSchema;
    }

    public boolean parse(Reader reader, long nLineNumber, LineEventListener listener) throws JSaParException,
            IOException {
        Line line = new Line(lineSchema.getLineType(), lineSchema.getSchemaCells().size());
        boolean setDefaultsOnly = false;
        boolean oneRead = false;
        boolean oneIgnored = false;

        for (FixedWidthSchemaCell schemaCell : lineSchema.getSchemaCells()) {
            if (setDefaultsOnly) {
                if (schemaCell.isDefaultValue())
                    line.addCell(schemaCell.getDefaultCell());
                continue;
            } else if (schemaCell.isIgnoreRead()) {
                if (schemaCell.isDefaultValue())
                    line.addCell(schemaCell.getDefaultCell());

                long nSkipped = reader.skip(schemaCell.getLength());
                if (nSkipped > 0 || schemaCell.getLength() == 0)
                    oneIgnored = true;

                if (nSkipped != schemaCell.getLength()) {
                    if (oneRead)
                        setDefaultsOnly = true;
                    continue;
                }
            } else {
                try {
                    Cell cell = schemaCell.makeCell(reader, lineSchema.isTrimFillCharacters(),
                            lineSchema.getFillCharacter(), listener, nLineNumber);
                    if (cell == null) {
                        if (oneRead) {
                            setDefaultsOnly = true;
                            if (schemaCell.getDefaultCell() != null)
                                line.addCell(schemaCell.makeCell(EMPTY_STRING));
                        }
                        continue;
                    }

                    oneRead = true;
                    line.addCell(cell);
                } catch (ParseException e) {
                    CellParseError cellParseError = e.getCellParseError();
                    cellParseError = new CellParseError(nLineNumber, cellParseError);
                    listener.lineErrorEvent(new LineErrorEvent(this, cellParseError));
                }
            }
        }
        if (line.size() <= 0 && !oneIgnored)
            return false;

        listener.lineParsedEvent(new LineParsedEvent(this, line, nLineNumber));

        return true;
    }

}

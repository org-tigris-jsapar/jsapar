package org.jsapar.input.parse.fixed;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.model.Cell;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.LineEventListener;
import org.jsapar.input.parse.SchemaLineParser;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;

public class FixedWidthLineParser extends SchemaLineParser {

    private static final String  EMPTY_STRING = "";
    private Reader               reader;
    private FixedWidthSchemaLine lineSchema;
//    private FixedWidthCellParser cellParser   = new FixedWidthCellParser();

    public FixedWidthLineParser(Reader reader, FixedWidthSchemaLine lineSchema) {
        this.reader = reader;
        this.lineSchema = lineSchema;
    }

    @Override
    public boolean parse(long nLineNumber, LineEventListener listener) throws JSaParException,
            IOException {
        Line line = new Line(lineSchema.getLineType(), lineSchema.getSchemaCells().size());
        boolean setDefaultsOnly = false;
        boolean oneRead = false;
        boolean oneIgnored = false;

        for (FixedWidthSchemaCell schemaCell : lineSchema.getSchemaCells()) {
            if (setDefaultsOnly) {
                if (schemaCell.isDefaultValue())
                    line.addCell(schemaCell.makeCell(EMPTY_STRING, listener, nLineNumber));
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
        if (line.getNumberOfCells() <= 0 && !oneIgnored)
            return false;

        listener.lineParsedEvent(new LineParsedEvent(this, line, nLineNumber));

        return true;
    }

}

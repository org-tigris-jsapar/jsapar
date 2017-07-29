package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.LineDecoratorErrorEventListener;
import org.jsapar.parse.ValidationHandler;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

/**
 * Parses fixed width text source on line level.
 */
class FixedWidthLineParser {

    private static final String EMPTY_STRING = "";
    private FixedWidthSchemaLine lineSchema;
    private FixedWidthCellParser cellParser        = new FixedWidthCellParser();
    private ValidationHandler    validationHandler = new ValidationHandler();
    private TextParseConfig config;

    FixedWidthLineParser(FixedWidthSchemaLine lineSchema, TextParseConfig config) {
        this.lineSchema = lineSchema;
        this.config = config;
    }

    boolean isIgnoreRead(){
        return lineSchema.isIgnoreRead();
    }

    @SuppressWarnings("UnnecessaryContinue")
    public Line parse(Reader reader, long lineNumber, ErrorEventListener errorListener) throws IOException {
        Line line = new Line(lineSchema.getLineType(), lineSchema.getSchemaCells().size());
        line.setLineNumber(lineNumber);
        boolean setDefaultsOnly = false;
        boolean oneRead = false;
        boolean oneIgnored = false;
        boolean handleInsufficient = true;

        for (FixedWidthSchemaCell schemaCell : lineSchema.getSchemaCells()) {
            if (setDefaultsOnly) {
                if (schemaCell.isDefaultValue())
                    line.addCell(schemaCell.makeDefaultCell());
                continue;
            } else if (schemaCell.isIgnoreRead()) {
                if (schemaCell.isDefaultValue())
                    line.addCell(schemaCell.makeDefaultCell());

                long nSkipped = reader.skip(schemaCell.getLength());
                if (nSkipped > 0 || schemaCell.getLength() == 0)
                    oneIgnored = true;

                if (nSkipped != schemaCell.getLength()) {
                    if (oneRead)
                        setDefaultsOnly = true;
                    continue;
                }
            } else {
                LineDecoratorErrorEventListener lineErrorEventListener = new LineDecoratorErrorEventListener(
                        errorListener, line);
                Optional<Cell> cell = cellParser
                        .parse(schemaCell, reader, lineErrorEventListener);
                if (!cell.isPresent()) {
                    if (oneRead) {
                        setDefaultsOnly = true;
                        if (schemaCell.isDefaultValue())
                            cellParser.parse(schemaCell, EMPTY_STRING, lineErrorEventListener)
                                    .ifPresent(line::addCell) ;
                        //noinspection ConstantConditions
                        if (handleInsufficient) {
                            if (!validationHandler
                                    .lineValidation(this, lineNumber, "Insufficient number of characters for line",
                                            config.getOnLineInsufficient(), errorListener)) {
                                return null;
                            }
                            handleInsufficient = false;
                        }
                    }
                    continue;
                }

                oneRead = true;
                line.addCell(cell.get());
            }
        }
        if (line.size() <= 0 && !oneIgnored)
            return null;

        return line;
    }

}

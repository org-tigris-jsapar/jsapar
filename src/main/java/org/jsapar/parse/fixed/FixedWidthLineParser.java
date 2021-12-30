package org.jsapar.parse.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.line.LineDecoratorErrorConsumer;
import org.jsapar.parse.line.ValidationHandler;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Parses fixed width text source on line level.
 */
final class FixedWidthLineParser {

    private static final String EMPTY_STRING = "";
    private final FixedWidthSchemaLine lineSchema;
    private final List<FixedWidthCellParser> cellParsers;
    private final ValidationHandler    validationHandler = new ValidationHandler();
    private final TextParseConfig            config;
    private final LineDecoratorErrorConsumer lineDecoratorErrorConsumer = new LineDecoratorErrorConsumer();

    FixedWidthLineParser(FixedWidthSchemaLine lineSchema, TextParseConfig config) {
        this.lineSchema = lineSchema;
        this.config = config;
        this.cellParsers = makeCellParsers(lineSchema);
    }

    private List<FixedWidthCellParser> makeCellParsers(FixedWidthSchemaLine lineSchema) {
        return lineSchema.stream().map(this::makeCellParser).collect(Collectors.toList());
    }

    private FixedWidthCellParser makeCellParser(FixedWidthSchemaCell fixedWidthSchemaCell) {
        return FixedWidthCellParser.ofSchemaCell(fixedWidthSchemaCell, Math.min(config.getMaxCellCacheSize(), lineSchema.getOccurs() - 1));
    }

    boolean isIgnoreRead(){
        return lineSchema.isIgnoreRead();
    }

    @SuppressWarnings("UnnecessaryContinue")
    public Line parse(ReadBuffer lineReader, Consumer<JSaParException> errorListener) throws IOException {
        Line line = new Line(lineSchema.getLineType(), lineSchema.size());
        line.setLineNumber(lineReader.getLineNumber());
        boolean setDefaultsOnly = false;
        boolean oneRead = false;
        boolean oneIgnored = false;

        lineDecoratorErrorConsumer.initialize(errorListener, line);
        for (FixedWidthCellParser cellParser : cellParsers) {
            FixedWidthSchemaCell schemaCell = cellParser.getSchemaCell();
            if (setDefaultsOnly) {
                cellParser.checkIfMandatory(errorListener);
                if (cellParser.isDefaultValue())
                    line.addCell(cellParser.makeDefaultCell());
                continue;
            } else if (schemaCell.isIgnoreRead()) {
                if (cellParser.isDefaultValue())
                    line.addCell(cellParser.makeDefaultCell());

                int nSkipped = lineReader.skipWithinLine(schemaCell.getLength());
                if (nSkipped > 0 || schemaCell.getLength() == 0)
                    oneIgnored = true;

                if (nSkipped != schemaCell.getLength()) {
                    if (oneRead) {
                        setDefaultsOnly = true;
                        if(!lineValidationInsufficient(lineReader, errorListener))
                            return null;
                    }
                    continue;
                }
            } else {
                Cell cell = cellParser.parse(lineReader, lineDecoratorErrorConsumer);
                if (cell == null) {
                    if (oneRead) {
                        setDefaultsOnly = true;
                        if (cellParser.isDefaultValue()) {
                            cell = cellParser.parse(EMPTY_STRING, lineDecoratorErrorConsumer);
                            if(cell != null)
                                line.addCell(cell);
                        }
                        if (!lineValidationInsufficient(lineReader, errorListener)) {
                            return null;
                        }
                    }
                    continue;
                }

                oneRead = true;
                line.addCell(cell);
            }
        }
        if (line.size() <= 0 && !oneIgnored)
            return null;

        int remaining = lineReader.remainsForLine();
        if(remaining > 0) {
            if(!validationHandler.lineValidation(lineReader.getLineNumber(), config.getOnLineOverflow(), errorListener,
                    ()-> remaining + " trailing characters found on line"))
                return null; // Ignore the line.
        }

        return line;
    }

    private boolean lineValidationInsufficient(ReadBuffer lineReader, Consumer<JSaParException> errorListener) {
        return validationHandler.lineValidation(lineReader.getLineNumber(), config.getOnLineInsufficient(),
                errorListener, () -> "Insufficient number of characters for line of type " + lineSchema.getLineType()
                        + ". Expected at least " + lineSchema.getTotalCellLength());
    }

}

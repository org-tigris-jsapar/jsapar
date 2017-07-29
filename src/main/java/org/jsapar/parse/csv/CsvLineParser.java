package org.jsapar.parse.csv;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.parse.*;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;

import java.io.IOException;

/**
 * Responsible for parsing csv lines
 */
class CsvLineParser {

    private static final String EMPTY_STRING = "";
    private CsvSchemaLine   lineSchema;
    private TextParseConfig config;
    private long              usedCount         = 0L;
    private CellParser        cellParser        = new CellParser();
    private ValidationHandler validationHandler = new ValidationHandler();

    /**
     * Creates a csv line parser with the given line schema.
     *
     * @param lineSchema The line schema to use.
     */
    CsvLineParser(CsvSchemaLine lineSchema) {
        this(lineSchema, new TextParseConfig());
    }

    /**
     * Creates a csv line parser with the given line schema.
     *
     * @param lineSchema The line schema to use.
     * @param config     Configuration for parsing.
     */
    CsvLineParser(CsvSchemaLine lineSchema, TextParseConfig config) {
        this.lineSchema = lineSchema;
        this.config = config;
    }

    /**
     * Parses one line from the given lineReader and sends a {@link LineParsedEvent} to the provided listener.
     *
     * @param lineReader    The line reader to read one line from.
     * @param listener      The event listener to receive events when parsing is complete.
     * @param errorListener The error event listener to which this method will send events for each error that occurs.
     * @return True if a line was parsed, false if no line could be parsed.
     * @throws IOException if an io-error occur
     */
    public boolean parse(CsvLineReader lineReader, LineEventListener listener, ErrorEventListener errorListener)
            throws IOException {

        String[] asCells = lineReader.readLine(lineSchema.getCellSeparator(), lineSchema.getQuoteChar());
        if (null == asCells)
            return false; // eof

        if (lineReader.lastLineWasEmpty())
            return handleEmptyLine(lineReader.currentLineNumber(), errorListener);

        if (usedCount == 0 && lineSchema.isFirstLineAsSchema()) {
            lineSchema = buildSchemaFromHeader(lineSchema, asCells);
            usedCount++;
            return true;
        }

        usedCount++;
        if(lineSchema.isIgnoreRead())
            return true;

        Line line = new Line(lineSchema.getLineType(),
                (lineSchema.getSchemaCells().size() > 0) ? lineSchema.getSchemaCells().size() : 10);
        line.setLineNumber(lineReader.currentLineNumber());
        LineDecoratorErrorEventListener lineErrorEventListener = new LineDecoratorErrorEventListener(errorListener, line);

        java.util.Iterator<CsvSchemaCell> itSchemaCell = lineSchema.getSchemaCells().iterator();
        for (String sCell : asCells) {
            if (itSchemaCell.hasNext()) {
                CsvSchemaCell schemaCell = itSchemaCell.next();
                addCellToLineBySchema(line, schemaCell, sCell, lineErrorEventListener);
            } else {
                if(!addCellToLineWithoutSchema(line, sCell, errorListener))
                    return true;
            }
        }
        if (line.size() <= 0)
            return false;

        // We have to fill all the default values and mandatory items for remaining cells within the schema.
        while (itSchemaCell.hasNext()) {
            if (!validationHandler.lineValidation(this, line.getLineNumber(),
                    "Insufficient number of cells could be read from the line", config.getOnLineInsufficient(),
                    errorListener)) {
                return true;
            }
            CsvSchemaCell schemaCell = itSchemaCell.next();
            addCellToLineBySchema(line, schemaCell, EMPTY_STRING, lineErrorEventListener);
        }

        listener.lineParsedEvent(new LineParsedEvent(this, line));
        return true;
    }

    /**
     * Builds a CsvSchemaLine from a header line.
     *
     * @param masterLineSchema The base to use while creating csv schema. May add formatting, defaults etc.
     * @param asCells          An array of cells in the header line to use for building the schema.
     * @return A CsvSchemaLine created from the header line.
     *
     * @throws IOException if an io-error occur
     */
    private CsvSchemaLine buildSchemaFromHeader(CsvSchemaLine masterLineSchema, String[] asCells) throws IOException {

        CsvSchemaLine schemaLine = masterLineSchema.clone();
        schemaLine.getSchemaCells().clear();

        for (String sCell : asCells) {
            CsvSchemaCell masterCell = masterLineSchema.getCsvSchemaCell(sCell);
            if (masterCell != null)
                schemaLine.addSchemaCell(masterCell);
            else
                schemaLine.addSchemaCell(new CsvSchemaCell(sCell));
        }
        addMissingDefaultValuesFromMaster(schemaLine, masterLineSchema);
        return schemaLine;
    }

    /**
     * Add all missing schema cells that has a default value in the master schema last on the line with
     * ignoreRead=true so that the default values are always set.
     *
     * @param schemaLine       The schema line to add missing default values to.
     * @param masterLineSchema The master schema line to get default values from.
     */
    private void addMissingDefaultValuesFromMaster(CsvSchemaLine schemaLine, CsvSchemaLine masterLineSchema) {
        masterLineSchema.getSchemaCells().stream().filter(schemaCell -> schemaCell.isDefaultValue()
                && schemaLine.getCsvSchemaCell(schemaCell.getName()) == null).forEach(schemaCell -> {
            CsvSchemaCell defaultCell = schemaCell.clone();
            defaultCell.setIgnoreRead(true);
            schemaLine.addSchemaCell(defaultCell);
        });
    }

    /**
     * Handles behavior of empty lines
     *
     * @param lineNumber The current line number
     * @param listener   The error event listener
     * @return Returns true (always).
     *
     */
    @SuppressWarnings("UnusedParameters")
    protected boolean handleEmptyLine(long lineNumber, ErrorEventListener listener) {
        return true;
    }

    /**
     * Adds a cell to the line according to the schema.
     *
     * @param line               The line to add a cell to
     * @param schemaCell         The cell schema
     * @param sCell              The string value of the cell
     * @param errorEventListener The error event listener to report errors to.
     *
     */
    private void addCellToLineBySchema(Line line,
                                       CsvSchemaCell schemaCell,
                                       String sCell,
                                       ErrorEventListener errorEventListener) {

        if (schemaCell.isIgnoreRead()) {
            if (schemaCell.isDefaultValue())
                line.addCell(schemaCell.makeDefaultCell());
            return;
        }
        if (schemaCell.isMaxLength() && sCell.length() > schemaCell.getMaxLength())
            sCell = sCell.substring(0, schemaCell.getMaxLength());
        cellParser.parse(schemaCell, sCell, errorEventListener).ifPresent(line::addCell);
    }

    /**
     * Adds overflowing cell to the line if there is no schema.
     *
     * @param line          The line to add cell to
     * @param sCell         The string value of the cell.
     * @param errorListener Error listener to send error event to if so is configured.
     *
     */
    private boolean addCellToLineWithoutSchema(Line line, String sCell, ErrorEventListener errorListener)
            {

        if (!validationHandler.lineValidation(this, line.getLineNumber(),
                "Found additional cell on the line that is not described in the line schema.",
                config.getOnLineOverflow(), errorListener)) {
            return false;
        }
        Cell cell;
        cell = new StringCell("@@cell-" + (1 + line.size()), sCell);
        line.addCell(cell);
        return true;
    }

}

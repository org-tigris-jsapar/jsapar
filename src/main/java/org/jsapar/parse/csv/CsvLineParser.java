package org.jsapar.parse.csv;

import java.io.IOException;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.*;
import org.jsapar.model.Cell;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineReader;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;

public class CsvLineParser  {

    private static final String EMPTY_STRING = "";
    private CsvSchemaLine lineSchema;
    private long usedCount = 0L;
    private CellParser cellParser = new CellParser();

    public CsvLineParser(CsvSchemaLine lineSchema2) {
        this.lineSchema = lineSchema2;
    }
    
    public CsvSchemaLine getLineSchema() {
        return lineSchema;
    }

    public boolean parse(CsvLineReader lineReader, LineEventListener listener, ErrorEventListener errorListener)
            throws JSaParException, IOException {

        String[] asCells = lineReader.readLine(lineSchema.getCellSeparator(), lineSchema.getQuoteChar());
        if(null == asCells)
            return false; // eof

        if (lineReader.lastLineWasEmpty())
            return handleEmptyLine(lineReader.currentLineNumber(), errorListener);

        if(usedCount == 0 && lineSchema.isFirstLineAsSchema()) {
            lineSchema = buildSchemaFromHeader(lineSchema, asCells);
            usedCount ++;
            return true;
        }

        usedCount ++;
        Line line = new Line(lineSchema.getLineType(), (lineSchema.getSchemaCells().size() > 0) ? lineSchema.getSchemaCells().size() : 10);
        line.setLineNumber(lineReader.currentLineNumber());
        LineDecoratorErrorEventListener lineErrorEventListener = new LineDecoratorErrorEventListener(
                errorListener, lineReader.currentLineNumber());

        java.util.Iterator<CsvSchemaCell> itSchemaCell = lineSchema.getSchemaCells().iterator();
        for (String sCell : asCells) {
            if (itSchemaCell.hasNext()) {
                CsvSchemaCell schemaCell = itSchemaCell.next();
                addCellToLineBySchema(line, schemaCell, sCell, lineErrorEventListener);
            } else {
                addCellToLineWithoutSchema(line, sCell);
            }
        }
        if (line.size() <= 0)
            return false;

        // We have to fill all the default values and mandatory items for remaining cells within the schema.
        while (itSchemaCell.hasNext()) {
            CsvSchemaCell schemaCell = itSchemaCell.next();
            addCellToLineBySchema(line, schemaCell, EMPTY_STRING, lineErrorEventListener);
        }

        listener.lineParsedEvent(new LineParsedEvent(this, line, lineReader.currentLineNumber()));
        return true;
    }

    /**
     * Builds a CsvSchemaLine from a header line.
     *
     * @param masterLineSchema The base to use while creating csv schema. May add formatting, defaults etc.
     * @param asCells An array of cells in the header line to use for building the schema.
     * @return A CsvSchemaLine created from the header line.
     * @throws CloneNotSupportedException
     * @throws JSaParException
     * @throws IOException
     */
    private CsvSchemaLine buildSchemaFromHeader(CsvSchemaLine masterLineSchema, String[] asCells)
            throws IOException, JSaParException {

        CsvSchemaLine schemaLine = masterLineSchema.clone();
        schemaLine.getSchemaCells().clear();

        for (String sCell : asCells) {
            CsvSchemaCell masterCell = masterLineSchema.getCsvSchemaCell(sCell);
            if(masterCell != null)
                schemaLine.addSchemaCell(masterCell);
            else
                schemaLine.addSchemaCell(new CsvSchemaCell(sCell));
        }
        addDefaultValuesFromMaster(schemaLine, masterLineSchema);
        return schemaLine;
    }

    /**
     * Add all cells that has a default value in the master schema last on the line with
     * ignoreRead=true so that the default values are always set.
     *
     * @param schemaLine
     * @param masterLineSchema
     */
    private void addDefaultValuesFromMaster(CsvSchemaLine schemaLine, CsvSchemaLine masterLineSchema) {
        for(CsvSchemaCell cell : masterLineSchema.getSchemaCells()){
            if(cell.getDefaultCell() != null){
                if(schemaLine.getCsvSchemaCell(cell.getName())==null){
                    CsvSchemaCell defaultCell = cell.clone();
                    defaultCell.setIgnoreRead(true);
                    schemaLine.addSchemaCell(defaultCell);
                }
            }
        }
    }

    /**
     * Handles behavior of empty lines
     *
     * @param lineNumber
     * @param listener
     * @return Returns true (always).
     * @throws JSaParException
     */
    protected boolean handleEmptyLine(long lineNumber, ErrorEventListener listener) throws JSaParException {
        return true;
    }

    /**
     * @param cellSeparator
     * @param quoteChar
     * @param lineReader
     * @return An array of all cells found on the line.
     */
    public static CellSplitter makeCellSplitter(String cellSeparator, char quoteChar, LineReader lineReader) {
        if (quoteChar == 0)
            return new SimpleCellSplitter(cellSeparator);
//        return new QuotedCellSplitter(cellSeparator, quoteChar, lineReader);
        // TODO here
        return null;
    }


    /**
     * Adds a cell to the line according to the schema.
     * 
     * @param line
     * @param schemaCell
     * @param sCell
     * @param listener
     * @throws JSaParException
     */
    private void addCellToLineBySchema(Line line,
                                       CsvSchemaCell schemaCell,
                                       String sCell,
                                       ErrorEventListener listener) throws JSaParException {

            if (schemaCell.isIgnoreRead()) {
                if (schemaCell.isDefaultValue())
                    line.addCell(schemaCell.getDefaultCell());
                return;
            }
            if (schemaCell.isMaxLength() && sCell.length() > schemaCell.getMaxLength())
                sCell = sCell.substring(0, schemaCell.getMaxLength());
            Cell cell = cellParser.parse(schemaCell, sCell, listener);
            if (cell != null)
                line.addCell(cell);
    }

    
    /**
     * Adds a cell to the line if there is no schema.
     * 
     * @param line
     * @param sCell
     * @throws JSaParException
     */
    private void addCellToLineWithoutSchema(Line line, String sCell) throws JSaParException {
        Cell cell;
        cell = new StringCell("@@cell-" + (1+line.size()), sCell);
        line.addCell(cell);
    }

    public long getUsedCount() {
        return usedCount;
    }
}

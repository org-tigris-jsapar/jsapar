package org.jsapar.input.parse.csv;

import java.io.IOException;

import org.jsapar.input.*;
import org.jsapar.model.Cell;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.input.LineEventListener;
import org.jsapar.input.parse.LineReader;
import org.jsapar.input.parse.SchemaLineParser;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;

public class CsvLineParser extends SchemaLineParser {

    private static final String EMPTY_STRING = "";
    private LineReader lineReader;
    private CsvSchemaLine lineSchema;
    private CellSplitter cellSplitter;
    
    public CsvLineParser(LineReader lineReader, CsvSchemaLine lineSchema2) {
        this.lineReader = lineReader;
        this.lineSchema = lineSchema2;
        cellSplitter = makeCellSplitter(lineSchema2.getCellSeparator(), lineSchema2.getQuoteChar(), lineReader);
    }
    
    public CsvSchemaLine getLineSchema() {
        return lineSchema;
    }

    /* (non-Javadoc)
     * @see org.jsapar.input.parse.SchemaLineParser#parse(long, java.lang.String, org.jsapar.input.LineEventListener)
     */
    @Override
    public boolean parse(long nLineNumber, LineEventListener listener)
            throws JSaParException, IOException {
        String sLine = lineReader.readLine();
        if(sLine == null)
            return false;

        Line line = new Line(lineSchema.getLineType(), (lineSchema.getSchemaCells().size() > 0) ? lineSchema.getSchemaCells().size() : 10);

        String[] asCells = cellSplitter.split(sLine);

        // Empty lines are not common. Only test for empty line if there are no more than one cell
        // after a split.
        if (asCells.length <= 1 && sLine.trim().isEmpty())
            return handleEmptyLine(lineSchema, nLineNumber, listener);

        java.util.Iterator<CsvSchemaCell> itSchemaCell = lineSchema.getSchemaCells().iterator();
        for (String sCell : asCells) {
            if (itSchemaCell.hasNext()) {
                CsvSchemaCell schemaCell = itSchemaCell.next();
                addCellToLineBySchema(line, schemaCell, sCell, listener, nLineNumber);
            } else {
                addCellToLineWithoutSchema(line, sCell);
            }
        }
        if (line.size() <= 0)
            return false;

        // We have to fill all the default values and mandatory items for remaining cells within the schema.
        while (itSchemaCell.hasNext()) {
            CsvSchemaCell schemaCell = itSchemaCell.next();
            addCellToLineBySchema(line, schemaCell, EMPTY_STRING, listener, nLineNumber);
        }

        listener.lineParsedEvent(new LineParsedEvent(this, line, nLineNumber));
        return true;
    }
    
    /**
     * @param sLine
     * @return An array of all cells found on the line.
     * @throws JSaParException 
     * @throws IOException 
     */
    public static CellSplitter makeCellSplitter(String cellSeparator, char quoteChar, LineReader lineReader) {
        if (quoteChar == 0)
            return new SimpleCellSplitter(cellSeparator);
        return new QuotedCellSplitter(cellSeparator, quoteChar, lineReader);
    }


    /**
     * Adds a cell to the line according to the schema.
     * 
     * @param line
     * @param schemaCell
     * @param sCell
     * @param listener
     * @param nLineNumber
     * @throws JSaParException
     */
    private void addCellToLineBySchema(Line line,
                                       CsvSchemaCell schemaCell,
                                       String sCell,
                                       LineEventListener listener,
                                       long nLineNumber) throws JSaParException {

        try {
            if (schemaCell.isIgnoreRead()) {
                if (schemaCell.isDefaultValue())
                    line.addCell(schemaCell.getDefaultCell());
                return;
            }
            if (schemaCell.isMaxLength() && sCell.length() > schemaCell.getMaxLength())
                sCell = sCell.substring(0, schemaCell.getMaxLength());
            Cell cell = schemaCell.makeCell(sCell, listener, nLineNumber);
            if (cell != null)
                line.addCell(cell);
        } catch (ParseException e) {
            CellParseError cellParseError = e.getCellParseError();
            cellParseError = new CellParseError(nLineNumber, cellParseError);
            listener.lineErrorEvent(new LineErrorEvent(this, cellParseError));
        }
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
        cell = new StringCell(sCell);
        line.addCell(cell);
    }
    
}

package org.jsapar.parse.csv;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.JSaParException;
import org.jsapar.parse.ParseException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineReader;
import org.jsapar.parse.ReaderLineReader;
import org.jsapar.parse.SchemaParser;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;

public class CsvParser implements SchemaParser{
    
    private static final String UTF8_BOM_STR = "\ufeff";
    private LineReader lineReader;
    private CsvSchema schema;

    public CsvParser(Reader reader, CsvSchema schema) {
        this.lineReader = new ReaderLineReader(schema.getLineSeparator(), reader);
        this.schema = schema;
    }
    

    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {
        long nLineNumber = 0; // First line is 1
        for (CsvSchemaLine lineSchema : schema.getCsvSchemaLines()) {

            if (lineSchema.isFirstLineAsSchema()) {
                try {
                    String sHeaderLine = lineReader.readLine();
                    if (sHeaderLine == null)
                        return;
                    lineSchema = buildSchemaFromHeader(lineSchema, sHeaderLine);
                } catch (CloneNotSupportedException e) {
                    throw new ParseException("Failed to create header schema.", e);
                }
            }
            CsvLineParser lineParser = new CsvLineParser(lineReader, lineSchema); 
            nLineNumber += parseLinesByOccurs(lineParser, nLineNumber, listener);
        }
    }
    
    
    /**
     * Builds a CsvSchemaLine from a header line.
     * 
     * @param masterLineSchema The base to use while creating csv schema. May add formatting, defaults etc.
     * @param sHeaderLine The header line to use while building the schema.
     * @return A CsvSchemaLine created from the header line.
     * @throws CloneNotSupportedException
     * @throws JSaParException 
     * @throws IOException 
     */
    private CsvSchemaLine buildSchemaFromHeader(CsvSchemaLine masterLineSchema, String sHeaderLine)
            throws CloneNotSupportedException, IOException, JSaParException {

        CsvSchemaLine schemaLine = masterLineSchema.clone();
        schemaLine.getSchemaCells().clear();

        schemaLine.setOccursInfinitely();

        sHeaderLine = removeLeadingByteOrderMark(sHeaderLine);
        
        String[] asCells = CsvLineParser.makeCellSplitter(schemaLine.getCellSeparator(), schemaLine.getQuoteChar(), null).split(sHeaderLine);
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
     * Normally the byte order mark should be removed from the input buffer before parsing but in case this is forgotten
     * we do it again because it is so hard to trace the error if it is not done at all when using the header line as a
     * schema.
     * 
     * @param sHeaderLine
     * @return
     */
    private String removeLeadingByteOrderMark(String sHeaderLine) {
        if (sHeaderLine.startsWith(UTF8_BOM_STR))
            return sHeaderLine.substring(UTF8_BOM_STR.length());
        else
            return sHeaderLine;
    }
    
    /**
     * @param lineParser
     * @param nLineNumber
     * @param reader
     * @param listener
     *            The event listener to report paring events back to.
     * @return Number of lines that were parsed (including failed ones).
     * @throws IOException
     * @throws JSaParException
     */
    private long parseLinesByOccurs(CsvLineParser lineParser,
                                    long nLineNumber,
                                    LineEventListener listener) throws IOException, JSaParException {
        long nStartLine = nLineNumber;
        for (int i = 0; i < lineParser.getLineSchema().getOccurs(); i++) {
            nLineNumber++;
            boolean isLineParsed = lineParser.parse(nLineNumber, listener);
            if (!isLineParsed)
                break;
        }

        return nLineNumber - nStartLine;
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
    
}

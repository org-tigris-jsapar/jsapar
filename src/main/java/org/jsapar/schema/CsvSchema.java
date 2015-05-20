package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed number of
 * characters. Each line is separated by the line separator defined in the base class {@link Schema}
 * 
 * @author Jonas
 * 
 */
public class CsvSchema extends Schema {

    /**
     * Byte order marker. Some editors (usually in Windows) adds a byte order marker to xml files. 
     */
    private static final String UTF8_BOM_STR = "\ufeff";
    private java.util.ArrayList<CsvSchemaLine> schemaLines = new java.util.ArrayList<CsvSchemaLine>(4);

    /**
     * @return the schemaLines
     */
    public java.util.List<CsvSchemaLine> getCsvSchemaLines() {
        return schemaLines;
    }

    /**
     * @param schemaLine
     *            the schemaLines to set
     */
    public void addSchemaLine(CsvSchemaLine schemaLine) {
        this.schemaLines.add(schemaLine);
    }

    /**
     * Builds a CsvSchemaLine from a header line.
     * 
     * @param masterLineSchema The base to use while creating csv schema. May add formatting, defaults etc.
     * @param sHeaderLine The header line to use while building the schema.
     * @return A CsvSchemaLine created from the header line.
     * @throws CloneNotSupportedException
     * @throws ParseException
     */
    private CsvSchemaLine buildSchemaFromHeader(CsvSchemaLine masterLineSchema, String sHeaderLine)
            throws CloneNotSupportedException, ParseException {

        CsvSchemaLine schemaLine = masterLineSchema.clone();
        schemaLine.getSchemaCells().clear();

        schemaLine.setOccursInfinitely();

        sHeaderLine = removeLeadingByteOrderMark(sHeaderLine);
        
        String[] asCells = schemaLine.split(sHeaderLine);
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

    @Override
    public void output(Iterator<Line> itLines, Writer writer) throws IOException, JSaParException {
        for (CsvSchemaLine lineSchema : getCsvSchemaLines()) {
            if (lineSchema.isFirstLineAsSchema()) {
                lineSchema.outputHeaderLine(writer);
                if (itLines.hasNext())
                    writer.write(getLineSeparator());
            }
            for (int i = 0; i < lineSchema.getOccurs(); i++) {
                if (!itLines.hasNext())
                    return;

                Line line = itLines.next();
                ((CsvSchemaLine) lineSchema).output(line, writer);

                if (itLines.hasNext())
                    writer.write(getLineSeparator());
                else
                    return;
            }
        }
    }

    @Override
    public void parse(java.io.Reader reader, ParsingEventListener listener) throws IOException, JSaParException {

        long nLineNumber = 0; // First line is 1
        for (CsvSchemaLine lineSchema : getCsvSchemaLines()) {

            if (lineSchema.isFirstLineAsSchema()) {
                try {
                    String sHeaderLine = parseLine(reader);
                    if (sHeaderLine == null)
                        return;
                    lineSchema = buildSchemaFromHeader(lineSchema, sHeaderLine);
                } catch (CloneNotSupportedException e) {
                    throw new ParseException("Failed to create header schema.", e);
                }
            }
            nLineNumber += parseLinesByOccurs(lineSchema, nLineNumber, reader, listener);
        }
    }

    /**
     * @param lineSchema
     * @param nLineNumber
     * @param reader
     * @param listener
     *            The event listener to report paring events back to.
     * @return Number of lines that were parsed (including failed ones).
     * @throws IOException
     * @throws JSaParException
     */
    private long parseLinesByOccurs(CsvSchemaLine lineSchema,
                                    long nLineNumber,
                                    Reader reader,
                                    ParsingEventListener listener) throws IOException, JSaParException {
        long nStartLine = nLineNumber;
        for (int i = 0; i < lineSchema.getOccurs(); i++) {
            nLineNumber++;
            String sLine = parseLine(reader);
            if (sLine == null)
                break;

            boolean isLineParsed = lineSchema.parse(nLineNumber, sLine, listener);
            if (!isLineParsed)
                break;
        }

        return nLineNumber - nStartLine;
    }

    @Override
    public CsvSchema clone() {
        CsvSchema schema;
        schema = (CsvSchema) super.clone();

        schema.schemaLines = new java.util.ArrayList<CsvSchemaLine>();
        for (CsvSchemaLine line : this.schemaLines) {
            schema.addSchemaLine(line.clone());
        }
        return schema;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());

        sb.append(" schemaLines=");
        sb.append(this.schemaLines);
        return sb.toString();
    }

    @Override
    public List<? extends SchemaLine> getSchemaLines() {
        return this.schemaLines;
    }

    @Override
    public void outputAfter(Writer writer) throws IOException, JSaParException {
    }

    @Override
    public void outputBefore(Writer writer) throws IOException, JSaParException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#outputLine(org.jsapar.Line, long, java.io.Writer)
     */
    @Override
    public boolean outputLine(Line line, long lineNumber, Writer writer) throws IOException, JSaParException {
        CsvSchemaLine schemaLine = null;

        long nLineMax = 0; // The line number for the last line of this schema line.
        long nLineWithinSchema = 1; // The line number within this schema.
        for (CsvSchemaLine currentSchemaLine : this.getCsvSchemaLines()) {
            nLineWithinSchema = lineNumber - nLineMax;
            if (currentSchemaLine.isOccursInfinitely()) {
                schemaLine = currentSchemaLine;
                break;
            }
            nLineMax += (long) currentSchemaLine.getOccurs();
            if (lineNumber <= nLineMax) {
                schemaLine = currentSchemaLine;
                break;
            }
        }

        if (schemaLine == null)
            return false;

        if (lineNumber > 1)
            writer.append(getLineSeparator());
        if (nLineWithinSchema == 1 && schemaLine.isFirstLineAsSchema()) {
            schemaLine.outputHeaderLine(writer);
            writer.append(getLineSeparator());
        }
        schemaLine.output(line, writer);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#getSchemaLine(java.lang.String)
     */
    @Override
    public SchemaLine getSchemaLine(String lineType) {
        for (CsvSchemaLine lineSchema : this.getCsvSchemaLines()) {
            if (lineSchema.getLineType().equals(lineType))
                return lineSchema;
        }
        return null;
    }

    @Override
    public int getSchemaLinesCount() {
        return this.schemaLines.size();
    }

    @Override
    public SchemaLine getSchemaLineAt(int index) {
        return this.schemaLines.get(index);
    }

}

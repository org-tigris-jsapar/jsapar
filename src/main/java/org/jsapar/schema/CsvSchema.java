package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;

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

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#outputLine(org.jsapar.model.Line, long, java.io.Writer)
     */
    @Override
    public boolean writeLine(Line line, long lineNumber, Writer writer) throws IOException, JSaParException {
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
            // TODO handle this while refactoring
//            schemaLine.outputHeaderLine(writer);
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

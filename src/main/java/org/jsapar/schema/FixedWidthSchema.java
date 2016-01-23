package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.input.ParseSchema;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed number of
 * characters. Each line is separated by the line separator defined in the base class {@link Schema}
 * .
 * 
 * If the end of line is reached before all cells are parsed the remaining cells will not be set.
 * 
 * If there are remaining characters when the end of line is reached, those characters will be
 * omitted.
 * 
 * If the line separator is an empty string, the lines will be separated by the sum of the length of
 * the cells within the schema.
 * 
 * @author Jonas Stenberg
 * 
 */
public class FixedWidthSchema extends Schema implements ParseSchema {

    /**
     * A list of fixed with schema lines which builds up this schema.
     */
    private java.util.List<FixedWidthSchemaLine> schemaLines = new java.util.LinkedList<FixedWidthSchemaLine>();

    /**
     * @return the schemaLines
     */
    public java.util.List<FixedWidthSchemaLine> getFixedWidthSchemaLines() {
        return schemaLines;
    }

    /**
     * @param schemaLines
     *            the schemaLines to set
     */
    public void setSchemaLines(java.util.List<FixedWidthSchemaLine> schemaLines) {
        this.schemaLines = schemaLines;
    }

    /**
     * @param schemaLine
     *            the schemaLines to set
     */
    public void addSchemaLine(FixedWidthSchemaLine schemaLine) {
        this.schemaLines.add(schemaLine);
        if(schemaLine.getLineTypeControlValue() != null && !schemaLine.getLineTypeControlValue().isEmpty()) {
            String[] controlValues = schemaLine.getLineTypeControlValue().split("\\|");
            if(controlValues.length > 1){
                schemaLine.setLineTypeControlValue(controlValues[0]);
                for(int i=1;i<controlValues.length; i++){
                    FixedWidthSchemaLine clone = schemaLine.clone();
                    clone.setLineTypeControlValue(controlValues[i]);
                    this.schemaLines.add(clone);
                }
            }
        }
    }



    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#output(org.jsapar.model.Document, java.io.Writer)
     */
    @Override
    public void output(Iterator<Line> itLines, Writer writer) throws IOException, JSaParException {

        for (SchemaLine lineSchema : getFixedWidthSchemaLines()) {
            for (int i = 0; i < lineSchema.getOccurs(); i++) {
                if (!itLines.hasNext()) {
                    return;
                }
                Line line = itLines.next();
                outputLine(lineSchema, line, writer);

                if (itLines.hasNext()) {
                    if (getLineSeparator().length() > 0) {
                        writer.write(getLineSeparator());
                    }
                } else {
                    return;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#clone()
     */
    @Override
    public FixedWidthSchema clone(){
        FixedWidthSchema schema = (FixedWidthSchema) super.clone();

        schema.schemaLines = new java.util.LinkedList<FixedWidthSchemaLine>();
        for (FixedWidthSchemaLine line : this.schemaLines) {
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

    @Override
    public SchemaLine getSchemaLine(String lineType) {
        for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
            if (lineSchema.getLineType().equals(lineType)) {
                return lineSchema;
            }
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

    /**
     * Adds a schema cell at the end of each line to make sure that the total length generated/parsed will always be at
     * least the minLength of the line. The name of the added cell will be _fillToMinLength_. The length of the added
     * cell will be the difference between all minLength of the line and the cells added so far. Adding more schema
     * cells after calling this method will add those cells after the filler cell which will probably lead to unexpected
     * behavior.
     */
    public void addFillerCellsToReachLineMinLength(){
        for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
            lineSchema.addFillerCellToReachMinLength(0);
        }
    }
}

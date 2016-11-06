package org.jsapar.schema;

import java.util.List;

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
public class FixedWidthSchema extends Schema {

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
    }

    @Override
    public boolean isEmpty() {
        return this.schemaLines.isEmpty();
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
    public SchemaLine getSchemaLine(String lineType) {
        for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
            if (lineSchema.getLineType().equals(lineType)) {
                return lineSchema;
            }
        }
        return null;
    }

    @Override
    public int size() {
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

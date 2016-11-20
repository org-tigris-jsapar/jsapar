package org.jsapar.schema;

import java.util.List;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed number of
 * characters. Each line is separated by the line separator defined in the base class {@link Schema}
 * .
 * <p>
 * If the end of line is reached before all cells are parsed the remaining cells will not be set.
 * <p>
 * If there are remaining characters when the end of line is reached, those characters will be
 * omitted.
 * <p>
 * If the line separator is an empty string, the lines will be separated by the sum of the length of
 * the cells within the schema.
 */
public class FixedWidthSchema extends Schema {

    /**
     * A list of fixed with schema lines which builds up this schema.
     */
    private java.util.List<FixedWidthSchemaLine> schemaLines = new java.util.LinkedList<>();

    /**
     * @return the list of line schemas.
     */
    public java.util.List<FixedWidthSchemaLine> getFixedWidthSchemaLines() {
        return schemaLines;
    }


    /**
     * @param schemaLine the schemaLines to set
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
    public FixedWidthSchema clone() {
        FixedWidthSchema schema = (FixedWidthSchema) super.clone();

        schema.schemaLines = new java.util.LinkedList<>();
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
        return super.toString() + " schemaLines=" + this.schemaLines;
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

    /**
     * Adds a schema cell at the end of each line to make sure that the total length generated/parsed will always be at
     * least the minLength of the line. The name of the added cell will be _fillToMinLength_. The length of the added
     * cell will be the difference between all minLength of the line and the cells added so far. Adding more schema
     * cells after calling this method will add those cells after the filler cell which will probably lead to unexpected
     * behavior.
     */
    public void addFillerCellsToReachLineMinLength() {
        getFixedWidthSchemaLines().forEach(FixedWidthSchemaLine::addFillerCellToReachMinLength);
    }

}

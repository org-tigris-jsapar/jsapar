package org.jsapar.schema;

import java.util.List;

/**
 * Defines a schema for a delimited input text. Each cell is delimited by a delimiter character sequence.
 * Lines are separated by the line separator defined by {@link #lineSeparator}.
 *
 */
public class CsvSchema extends Schema implements Cloneable{

    /**
     * The schema lines
     */
    private java.util.ArrayList<CsvSchemaLine> schemaLines = new java.util.ArrayList<>(4);

    /**
     * @return the schemaLines
     */
    public java.util.List<CsvSchemaLine> getCsvSchemaLines() {
        return schemaLines;
    }

    /**
     * @param schemaLine the schemaLines to set
     */
    public void addSchemaLine(CsvSchemaLine schemaLine) {
        this.schemaLines.add(schemaLine);
    }

    @Override
    public boolean isEmpty() {
        return this.schemaLines.isEmpty();
    }

    @Override
    public CsvSchema clone() {
        CsvSchema schema;
        schema = (CsvSchema) super.clone();

        schema.schemaLines = new java.util.ArrayList<>();
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
        return super.toString() +
                " schemaLines=" +
                this.schemaLines;
    }

    @Override
    public List<? extends SchemaLine> getSchemaLines() {
        return this.schemaLines;
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
    public int size() {
        return this.schemaLines.size();
    }


}

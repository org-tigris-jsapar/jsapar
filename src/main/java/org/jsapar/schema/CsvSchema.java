package org.jsapar.schema;

import org.jsapar.parse.csv.CsvParser;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextSchemaParser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Defines a schema for a delimited input text. Each cell is delimited by a delimiter character sequence.
 * Lines are separated by the line separator defined by {@link #lineSeparator}.
 *
 */
public class CsvSchema extends Schema implements Cloneable{

    /**
     * The schema lines
     */
    private List<CsvSchemaLine> schemaLines = new ArrayList<>(4);

    /**
     * @return the schemaLines
     */
    public List<CsvSchemaLine> getCsvSchemaLines() {
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

    @Override
    public Stream<CsvSchemaLine> stream() {
        return this.schemaLines.stream();
    }

    @Override
    public TextSchemaParser makeSchemaParser(Reader reader, TextParseConfig parseConfig) {
        return new CsvParser(reader, this, parseConfig);
    }


}

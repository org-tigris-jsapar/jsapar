package org.jsapar.parse;

import org.jsapar.parse.csv.CsvParser;
import org.jsapar.parse.fixed.FixedWidthParserFlat;
import org.jsapar.parse.fixed.FixedWidthParserLinesSeparated;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.Schema;

import java.io.Reader;

/**
 * Internal class for creating a {@link SchemaParser} based on a {@link Schema}.
 */
public class SchemaParserFactory {

    public SchemaParserFactory() {
    }

    /**
     * Creates a {@link SchemaParser} based on a {@link Schema}
     * @param schema The schema to create a parser for.
     * @param reader The reader that the parser should read from.
     * @param parseConfig Configuration that the parser should use.
     * @return A {@link SchemaParser} based on a {@link Schema}
     */
    public SchemaParser makeSchemaParser(Schema schema, Reader reader, ParseConfig parseConfig)  {
        if (schema instanceof CsvSchema) {
            return new CsvParser(reader, (CsvSchema) schema, parseConfig);
        }
        if (schema instanceof FixedWidthSchema) {
            FixedWidthSchema fixedWidthSchema = (FixedWidthSchema) schema;
            if (fixedWidthSchema.getLineSeparator().isEmpty())
                return new FixedWidthParserFlat(reader, fixedWidthSchema, parseConfig);
            else
                return new FixedWidthParserLinesSeparated(reader, fixedWidthSchema, parseConfig);
        }

        throw new IllegalArgumentException("Unknown schema type. Unable to create parser class for it.");
    }

}

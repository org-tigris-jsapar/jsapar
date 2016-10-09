package org.jsapar.parse;

import org.jsapar.JSaParException;
import org.jsapar.parse.csv.CsvParser;
import org.jsapar.parse.fixed.FixedWidthParserFlat;
import org.jsapar.parse.fixed.FixedWidthParserLinesSeparated;
import org.jsapar.parse.xml.XmlParser;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.XmlSchema;

import java.io.Reader;

public class SchemaParserFactory {

    public SchemaParserFactory() {
    }

    public SchemaParser makeSchemaParser(ParseSchema schema, Reader reader, ParseConfig parseConfig)  {
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

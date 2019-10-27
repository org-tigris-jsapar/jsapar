package org.jsapar.compose.csv;

import org.jsapar.compose.internal.AbstractSchemaComposer;
import org.jsapar.compose.internal.SchemaComposer;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;

import java.io.Writer;

/**
 * Composes csv output based on schema.
 */
public class CsvComposer extends AbstractSchemaComposer implements SchemaComposer {

    public CsvComposer(Writer writer, CsvSchema schema) {
        super(writer, schema, schemaLine -> new CsvLineComposer(writer, (CsvSchemaLine) schemaLine, schema.getLineSeparator(), schema.getQuoteSyntax()));
    }

}

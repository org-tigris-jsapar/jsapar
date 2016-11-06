package org.jsapar.compose.csv;

import org.jsapar.compose.LineComposer;
import org.jsapar.compose.SchemaComposer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Composes csv output based on schema.
 * Created by stejon0 on 2016-01-24.
 */
public class CsvComposer implements SchemaComposer {

    private       Writer writer;
    private final CsvSchema schema;
    private Map<SchemaLine, CsvLineComposer> lineComposerCache = new HashMap<>();

    public CsvComposer(Writer writer, CsvSchema schema) {
        this.writer = writer;
        this.schema = schema;
    }

    /**
     * This implementation composes CSV output based on schema and supplied lines.
     * @param itLines The lines to compose output for.
     * @throws IOException
     *
     */
    @Override
    public void compose(Iterator<Line> itLines) throws IOException, JSaParException {
        for (CsvSchemaLine lineSchema : schema.getCsvSchemaLines()) {
            CsvLineComposer lineComposer = (CsvLineComposer) makeLineComposer(lineSchema);
            for (int i = 0; i < lineSchema.getOccurs(); i++) {
                if (!itLines.hasNext())
                    return;

                Line line = itLines.next();
                lineComposer.compose(line);

                if (itLines.hasNext())
                    writer.write(schema.getLineSeparator());
                else
                    return;
            }
        }
    }

    /**
     * This implementation does nothing
     * @throws IOException
     *
     */
    @Override
    public void beforeCompose() throws IOException, JSaParException {
    }

    /**
     * This implementation does nothing
     * @throws IOException
     *
     */
    @Override
    public void afterCompose() throws IOException, JSaParException {

    }

    @Override
    public boolean composeLine(Line line) throws IOException {
        SchemaLine schemaLine = schema.getSchemaLine(line.getLineType());
        if (schemaLine == null)
            return false;
        makeLineComposer(schemaLine).compose(line);
        return true;
    }


    private LineComposer makeLineComposer(SchemaLine schemaLine) {
        assert schemaLine instanceof CsvSchemaLine;
        CsvLineComposer lineComposer = lineComposerCache.get(schemaLine);
        if(lineComposer == null) {
            lineComposer = new CsvLineComposer(writer, (CsvSchemaLine) schemaLine, schema.getLineSeparator());
            lineComposerCache.put(schemaLine, lineComposer);
        }
        return lineComposer;
    }



}

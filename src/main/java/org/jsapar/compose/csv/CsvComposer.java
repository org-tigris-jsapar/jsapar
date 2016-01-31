package org.jsapar.compose.csv;

import org.jsapar.JSaParException;
import org.jsapar.compose.SchemaComposer;
import org.jsapar.model.Line;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * Created by stejon0 on 2016-01-24.
 */
public class CsvComposer implements SchemaComposer {

    private       Writer writer;
    private final CsvSchema schema;

    public CsvComposer(Writer writer, CsvSchema schema) {
        this.writer = writer;
        this.schema = schema;
    }

    @Override
    public void compose(Iterator<Line> itLines) throws IOException, JSaParException {
        for (CsvSchemaLine lineSchema : schema.getCsvSchemaLines()) {
            //TODO: Cache line composer based on line schema.
            CsvLineComposer lineComposer = new CsvLineComposer(writer, lineSchema);
            if (lineSchema.isFirstLineAsSchema()) {
                lineComposer.composeHeaderLine();
                if (itLines.hasNext())
                    writer.write(schema.getLineSeparator());
            }
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
    @Override
    public void beforeCompose() throws IOException, JSaParException {

    }

    @Override
    public void afterCompose() throws IOException, JSaParException {

    }

}

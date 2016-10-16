package org.jsapar.compose.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.compose.SchemaComposer;
import org.jsapar.compose.LineComposer;
import org.jsapar.model.Line;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Composes fixed width output based on schema.
 * Created by stejon0 on 2016-01-31.
 */
public class FixedWidthComposer implements SchemaComposer {
    private final Writer writer;
    private final FixedWidthSchema schema;

    public FixedWidthComposer(Writer writer, FixedWidthSchema schema) {
        this.writer = writer;
        this.schema = schema;
    }

    /**
     * This implementation composes fixed width output based on schema and supplied lines.
     * @param iterator The lines to compose output for.
     * @throws IOException
     * @throws JSaParException
     */
    @Override
    public void compose(Iterator<Line> iterator) throws IOException, JSaParException {
        for (FixedWidthSchemaLine lineSchema : schema.getFixedWidthSchemaLines()) {
            for (int i = 0; i < lineSchema.getOccurs(); i++) {
                if (!iterator.hasNext()) {
                    return;
                }
                Line line = iterator.next();
                FixedWidthLineComposer lineComposer = new FixedWidthLineComposer(writer, lineSchema);
                lineComposer.compose(line);

                if (iterator.hasNext()) {
                    if (schema.getLineSeparator().length() > 0) {
                        writer.write(schema.getLineSeparator());
                    }
                } else {
                    return;
                }
            }
        }
    }

    /**
     * This implementation does nothing
     * @throws IOException
     * @throws JSaParException
     */
    @Override
    public void beforeCompose() throws IOException, JSaParException {

    }

    /**
     * This implementation does nothing
     * @throws IOException
     * @throws JSaParException
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

    public LineComposer makeLineComposer(SchemaLine schemaLine) {
        assert schemaLine instanceof FixedWidthSchemaLine;
        return new FixedWidthLineComposer(writer, (FixedWidthSchemaLine) schemaLine);
    }
}

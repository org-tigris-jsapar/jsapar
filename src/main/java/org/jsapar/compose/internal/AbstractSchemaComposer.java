package org.jsapar.compose.internal;

import org.jsapar.compose.line.LineComposer;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Abstract base class for schema composers with standard behavior. This implementation will only consider the line
 * type when choosing which schema line type to use.
 */
public abstract class AbstractSchemaComposer implements SchemaComposer{
    private final Writer writer;
    private final Schema<? extends SchemaLine<? extends SchemaCell>> schema;
    private final Map<String, LineComposer> lineComposers;
    private final LineBuffer lineBuffer = new LineBuffer();

    /**
     * @param writer The writer to write output to
     * @param schema The schema to use
     * @param lineComposerCreator A {@link Function} that takes a {@link SchemaLine} as argument and returns a newly created
     *                            {@link LineComposer} that uses that schema line.
     */
    /**
     * @param writer              The writer to write composed lines to.
     * @param schema              The schema.
     * @param lineComposerCreator Creates a line composer for a schema line. The line composer must write to the writer
     *                            supplied to this function, which buffers each line before it is written to the
     *                            actual writer.
     */
    public AbstractSchemaComposer(Writer writer, Schema<? extends SchemaLine<? extends SchemaCell>> schema, BiFunction<Writer, SchemaLine<? extends SchemaCell>, LineComposer> lineComposerCreator) {
        this.writer = writer;
        this.schema = schema;
        lineComposers = schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, schemaLine -> lineComposerCreator.apply(lineBuffer, schemaLine)));
    }

    /**
     * This implementation composes line separator accoring to schema.
     * @throws UncheckedIOException when an IO error occurs
     */
    @Override
    public void composeLineSeparator() {
        try {
            writer.write(schema.getLineSeparator());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Composes output based on schema and supplied lines. Mainly for test purpose.
     * @param iterator The lines to compose output for.
     * @throws UncheckedIOException when an IO error occurs
     *
     */
    public void compose(Iterator<Line> iterator) {
        while (iterator.hasNext()) {
            if (composeLine(iterator.next()) && iterator.hasNext()) {
                composeLineSeparator();
            }
        }
    }

    @Override
    public boolean composeLine(Line line)  {
        LineComposer lineComposer = lineComposers.get(line.getLineType());
        if (lineComposer == null || lineComposer.ignoreWrite())
            return false;
        try {
            lineComposer.compose(line);
            lineBuffer.writeTo(writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            lineBuffer.clear(); // Discard any partial line if composing failed.
        }
        return true;
    }

}

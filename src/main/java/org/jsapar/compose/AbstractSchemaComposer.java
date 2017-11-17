package org.jsapar.compose;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract base class for schema composers with standard behavior. This implementation will only consider the line
 * type when choosing which schema line type to use.
 */
public abstract class AbstractSchemaComposer implements SchemaComposer{
    private final Writer writer;
    private final Schema schema;
    private Map<String, LineComposer> lineComposers;

    /**
     * @param writer The writer to write output to
     * @param schema The schema to use
     * @param lineComposerCreator A {@link Function} that takes a {@link SchemaLine} as argument and returns a newly created
     *                            {@link LineComposer} that uses that schema line.
     */
    public AbstractSchemaComposer(Writer writer, Schema schema, Function<SchemaLine, LineComposer> lineComposerCreator) {
        this.writer = writer;
        this.schema = schema;
        lineComposers = schema.stream().collect(Collectors.toMap(SchemaLine::getLineType, lineComposerCreator));
    }

    /**
     * This implementation composes output based on schema and supplied lines.
     * @param iterator The lines to compose output for.
     * @throws UncheckedIOException when an IO error occurs
     *
     */
    @Override
    public void compose(Iterator<Line> iterator) throws JSaParException {
        try {
            while (iterator.hasNext()) {
                if (composeLine(iterator.next()) && iterator.hasNext()) {
                    writer.write(schema.getLineSeparator());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * This implementation does nothing
     * @throws UncheckedIOException when an IO error occurs
     *
     */
    @Override
    public void beforeCompose() throws JSaParException {

    }

    /**
     * This implementation does nothing
     * @throws UncheckedIOException when an IO error occurs
     *
     */
    @Override
    public void afterCompose() throws JSaParException {

    }

    @Override
    public boolean composeLine(Line line)  {
        LineComposer lineComposer = lineComposers.get(line.getLineType());
        if (lineComposer == null || lineComposer.ignoreWrite())
            return false;
        lineComposer.compose(line);
        return true;
    }

}

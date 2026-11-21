package org.jsapar;

import org.jsapar.compose.Composer;
import org.jsapar.compose.internal.ComposerFactory;
import org.jsapar.compose.internal.SchemaComposer;
import org.jsapar.compose.internal.TextComposerFactory;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.function.Consumer;

/**
 * This class contains methods for transforming a Document or Line into a text output. E.g. if you want to write
 * the Document to a file you should first set the schema,  use a {@link java.io.FileWriter} and
 * call the {@link #compose(Document)} method.
 * 
 */
public final class TextComposer implements Composer, AutoCloseable {
    private final Writer         writer;
    private final Schema<?>      schema;
    private final SchemaComposer schemaComposer;
    private       boolean        breakBefore = false;

    /**
     * Creates an TextComposer with a schema.
     *
     * @param schema The schema to use.
     * @param writer The writer to write text output to. Caller is responsible for either closing the writer or call the close method of the created instance.
     */
    public TextComposer(Schema<?> schema, Writer writer) {
        this(schema, writer, new TextComposerFactory());
    }

    /**
     * Creates an TextComposer with a schema allowing to add custom {@link SchemaComposer}.
     *
     * @param schema          The schema to use.
     * @param writer          The writer to write text output to. Caller is responsible for either closing the writer or call the close method of the created instance.
     * @param composerFactory A factory interface for creating {@link SchemaComposer} based on schema.
     */
    TextComposer(Schema<?> schema, Writer writer, ComposerFactory composerFactory) {
        if(writer == null)
            throw new IllegalArgumentException("Writer of text composer cannot be null");
        if(schema == null)
            throw new IllegalArgumentException("Schema of text composer cannot be null");
        this.schema = schema;
        this.writer = writer;
        this.schemaComposer = composerFactory.makeComposer(schema, writer);
    }

    /**
     * Writes the single line to a {@link java.io.Writer} according to the line type of the line.
     * The line is prefixed with the line separator unless it is the first line.
     * <p>
     * Since 2.5, the detail lines of the line, if any, are written after the line, recursively and in the order they
     * were added. If the line itself is not written, neither are its detail lines.
     * 
     * @param line The line to write.
     *
     * @throws UncheckedIOException if io-error occurs.
     * @return True if the line was written, false if there was no matching line type in the schema.
     * @throws UncheckedIOException In case it was not possible to write to the attached writer.
     */
    @Override
    public boolean composeLine(Line line) throws UncheckedIOException{
        try {
            if (breakBefore) {
                // Write the separator only for a line that will be written, so that there is no trailing separator
                // when the last line, for instance a detail line, is not written.
                if (!isWritable(line))
                    return false;
                writer.write(schema.getLineSeparator());
            }
            boolean written = writeLine(line);
            breakBefore = written;
            if (written && line.hasDetails()) {
                for (Line detail : line.getDetails()) {
                    composeLine(detail);
                }
            }
            return written;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes an additional line separator to the attached writer if the line separator is not an empty string.
     * @throws UncheckedIOException In case it was not possible to write to the attached writer.
     */
    @Override
    public boolean composeEmptyLine() throws UncheckedIOException{
        try {
            this.writer.write(schema.getLineSeparator());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }

    @Override
    public void setErrorConsumer(Consumer<JSaParException> errorConsumer) {
        // TODO: Add error handling when composing.
    }

    /**
     * Writes the single line to a {@link java.io.Writer} according to the line type of the supplied
     * line. Note that the line separator is not written by this method.
     * 
     * @param line The line to write
     *
     * @throws UncheckedIOException if an io-error occurs.
     * @return True if line was composed, false otherwise.
     */
    boolean writeLine(Line line) {
        return schemaComposer.composeLine(line);
    }

    /**
     * @param line The line to check.
     * @return True if the schema has a line type for the line that is not ignored when writing.
     */
    private boolean isWritable(Line line) {
        return schema.getSchemaLine(line.getLineType()).filter(schemaLine -> !schemaLine.isIgnoreWrite()).isPresent();
    }


    /**
     * Closes the attached writer.
     * @throws IOException In case of failing to close
     */
    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}

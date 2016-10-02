/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.compose.ComposeException;
import org.jsapar.compose.SchemaComposer;
import org.jsapar.compose.TextComposerFactory;
import org.jsapar.compose.ComposerFactory;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;

/**
 * This class contains methods for transforming a Document or Line into a text output. E.g. if you want to write
 * the Document to a file you should first set the schema,  use a {@link java.io.FileWriter} and
 * call the {@link #compose(Document)} method.
 * 
 * @author Jonas Stenberg
 * 
 */
public class TextComposer implements Composer{
    private final Writer          writer;
    private final Schema          schema;
    private final ComposerFactory composerFactory;
    private       SchemaComposer  currentSchemaComposer;

    /**
     * Creates an TextComposer with a schema.
     * 
     * @param schema
     */
    public TextComposer(Schema schema, Writer writer) {
        this(schema,  writer, new TextComposerFactory());
    }

    /**
     * Creates an TextComposer with a schema.
     *
     * @param schema
     * @param writer
     */
    public TextComposer(Schema schema, Writer writer, ComposerFactory composerFactory) {
        this.schema = schema;
        this.writer = writer;
        this.composerFactory = composerFactory;
    }

    /**
     * Writes the document to a {@link java.io.Writer} according to the schemas of this composer.
     * Note that you have to add at least one schema to the instance of TextComposer before calling
     * this method.
     * 
     * @param document
     * @throws JSaParException
     */
    @Override
    public void compose(Document document)  {
        compose(document.getLineIterator());
    }

    /**
     * Writes the document to a {@link java.io.Writer} according to the schemas of this composer. Note that you have to
     * add at least one schema to the instance of TextComposer before calling this method.
     * 
     * @param lineIterator
     *            An iterator that iterates over a collection of lines. Can be used to build lines on-the-fly if you
     *            don't want to store them all in memory.
     * @throws JSaParException
     */
    public void compose(Iterator<Line> lineIterator) throws JSaParException {
        try {
            SchemaComposer schemaComposer = makeSchemaComposer();
            schemaComposer.beforeCompose();
            schemaComposer.compose(lineIterator);
            schemaComposer.afterCompose();
        } catch (IOException e) {
            throw new ComposeException("Failed to write to buffert.", e);
        }
    }

    private SchemaComposer makeSchemaComposer() throws JSaParException {
        if (currentSchemaComposer == null)
            currentSchemaComposer = composerFactory.makeComposer(schema, writer);
        return currentSchemaComposer;
    }

    /**
     * Writes the single line to a {@link java.io.Writer} according to the line type of the line.
     * The line is terminated by the line separator.
     * 
     * @param line
     * @throws JSaParException
     * @throws IOException
     * @return True if the line was written, false if there was no matching line type in the schema.
     */
    public boolean writeLineLn(Line line) throws JSaParException, IOException {
        if(!writeLine(line))
            return false;
        writer.write(schema.getLineSeparator());
        return true;
    }

    @Override
    public boolean composeLine(Line line) throws IOException {
        return writeLineLn(line);
    }

    /**
     * Writes the single line to a {@link java.io.Writer} according to the line type of the supplied
     * line. Note that the line separator is not written by this method.
     * 
     * @param line
     * @throws JSaParException
     * @throws IOException
     */
    public boolean writeLine(Line line) throws JSaParException, IOException {
        SchemaComposer schemaComposer = makeSchemaComposer();
        return schemaComposer.composeLine(line);
    }


    /**
     * @return the schema
     */
    public Schema getSchema() {
        return schema;
    }

}

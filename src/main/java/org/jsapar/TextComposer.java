/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.compose.ComposeException;
import org.jsapar.compose.Composer;
import org.jsapar.compose.TextComposerFactory;
import org.jsapar.compose.ComposerFactory;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

/**
 * This class contains methods for transforming a Document or Line into a text output. E.g. if you want to write
 * the Document to a file you should first set the schema,  use a {@link java.io.FileWriter} and
 * call the {@link #write(Document)} method.
 * 
 * @author Jonas Stenberg
 * 
 */
public class TextComposer {
    private final Writer          writer;
    private final Schema          schema;
    private final ComposerFactory composerFactory;
    private Composer        currentComposer;

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
    public void write(Document document) throws JSaParException {
        write(document.getLineIterator());
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
    public void write(Iterator<Line> lineIterator) throws JSaParException {
        try {
            Composer composer = makeComposer();
            composer.beforeCompose();
            composer.compose(lineIterator);
            composer.afterCompose();
        } catch (IOException e) {
            throw new ComposeException("Failed to write to buffert.", e);
        }
    }

    private Composer makeComposer() throws JSaParException {
        if (currentComposer == null)
            currentComposer = composerFactory.makeComposer(schema, writer);
        return currentComposer;
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

    /**
     * Writes the single line to a {@link java.io.Writer} according to the line type of the supplied
     * line. Note that the line separator is not written by this method.
     * 
     * @param line
     * @throws JSaParException
     * @throws IOException
     */
    public boolean writeLine(Line line) throws JSaParException, IOException {
        Composer composer = makeComposer();
        SchemaLine schemaLine = schema.getSchemaLine(line.getLineType());
        if (schemaLine == null)
            return false;
        composer.makeLineComposer(schemaLine).compose(line);
        return true;
    }


    /**
     * Writes the single line to a {@link java.io.Writer} according to the member schema of this
     * instance.
     * 
     * @param line
     * @param lineNumber
     *            The line number for this schema. 
     * @throws JSaParException
     */
    public boolean writeLine(Line line, long lineNumber) throws JSaParException, IOException {
        Composer composer = makeComposer();
        SchemaLine schemaLine = schema.getSchemaLine(lineNumber);
        if(schemaLine == null)
            return false;
        composer.makeLineComposer(schemaLine).compose(line);
        return true;
    }

    /**
     * Writes a single line
     * @param line
     * @param lineNumber
     * @return
     * @throws IOException
     * @throws JSaParException
     */
    public boolean writeLineLn(Line line, long lineNumber) throws IOException, JSaParException {
        if(!writeLine(line,lineNumber))
            return false;
        writer.write(schema.getLineSeparator());
        return true;
    }


    /**
     * @return the schema
     */
    public Schema getSchema() {
        return schema;
    }

}

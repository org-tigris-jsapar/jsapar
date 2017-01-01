package org.jsapar.concurrent;

import org.jsapar.TextComposer;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.Schema;

import java.io.Reader;
import java.io.Writer;

/**
 * A multi threaded version of {@link org.jsapar.Text2TextConverter} where the composer is started in a separate worker
 * thread.
 *
 * @see ConcurrentConvertTask
 * @see org.jsapar.Text2TextConverter
 *
 */
public class ConcurrentText2TextConverter extends ConcurrentConvertTask {

    /** Creates a converter
     * @param parser The parser to use while parsing
     * @param composer The composer to use while composing.
     */
    public ConcurrentText2TextConverter(TextParseTask parser, TextComposer composer) {
        super(parser, composer);
    }

    /** Creates a converter
     * @param inputSchema The schema to use while parsing
     * @param reader The reader to parse text from
     * @param outputSchema The schema to use while composing
     * @param writer The writer to compose output to.
     */
    public ConcurrentText2TextConverter(Schema inputSchema, Reader reader, Schema outputSchema, Writer writer) {
        super(new TextParseTask(inputSchema, reader), new TextComposer(outputSchema, writer));
    }

}

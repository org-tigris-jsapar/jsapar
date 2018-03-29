package org.jsapar.concurrent;

import org.jsapar.TextComposer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A multi threaded version of {@link org.jsapar.Text2TextConverter} where the composer is started in a separate worker
 * thread.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 *
 * @see ConcurrentConvertTask
 * @see org.jsapar.Text2TextConverter
 *
 */
public class ConcurrentText2TextConverter extends AbstractConcurrentConverter{
    private TextParseConfig parseConfig = new TextParseConfig();

    public ConcurrentText2TextConverter(Schema parseSchema, Schema composeSchema) {
        super(parseSchema, composeSchema);
    }

    public ConcurrentText2TextConverter(Schema parseSchema, Schema composeSchema, TextParseConfig parseConfig) {
        super(parseSchema, composeSchema);
        this.parseConfig = parseConfig;
    }

    /**
     * @param reader The reader to read input from
     * @param writer The writer to write converted result to.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     */
    public long convert(Reader reader, Writer writer) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.getParseSchema(), reader, parseConfig);
        TextComposer composer = new TextComposer(getComposeSchema(), writer);
        return super.convert(parseTask, composer);
    }

    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}

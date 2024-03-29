package org.jsapar.parse.text;

import org.jsapar.parse.AbstractParseTask;
import org.jsapar.parse.ParseTask;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * This class is used for a one-off parsing of a text source. You create an instance of this class, calls execute, then dispose it.. <br>
 * The instance of this class will produce events for each line that has been successfully parsed.
 * <p>
 * If you want to get the result back as a complete list of lines, you should use the {@link org.jsapar.parse.CollectingConsumer}.
 * <ol>
 * <li>First, create an instance of TextParseTask.</li>
 * <li>Set event listeners for parse events and error events</li>
 * <li>Call the {@link #execute()} method. You will receive a callback event for each line that is parsed.</li>
 * </ol>
 *
 * @see org.jsapar.TextParser
 * @see ParseTask
 */
public class TextParseTask extends AbstractParseTask implements ParseTask, AutoCloseable {
    private final Reader          reader;
    private final TextSchemaParser parser;

    public TextParseTask(Schema<? extends SchemaLine<? extends SchemaCell>> schema, Reader reader) {
        this(reader, TextSchemaParser.ofSchema(schema, reader, new TextParseConfig()));
    }

    public TextParseTask(Schema<? extends SchemaLine<? extends SchemaCell>> schema, Reader reader, TextParseConfig parseConfig) {
        this(reader, TextSchemaParser.ofSchema(schema, reader, parseConfig));
    }

    public TextParseTask(Reader reader, TextSchemaParser parser) {
        this.reader = reader;
        this.parser = parser;
    }

    /**
     * Reads characters from the input and parses them into a Line. Once a Line is completed, a
     * LineParsedEvent is generated to all registered event listeners. If there is an error while
     * parsing a line, a CellErrorEvent or ErrorEvent is generated to all registered error event listeners <br>
     * Before calling this method you have to call {@link #setLineConsumer(Consumer)} to be able to handle the
     * result
     *
     * @throws IOException If there is an error reading the input
     */
    @Override
    public long execute() throws IOException {
        return parser.parse(getLineConsumer(), getErrorConsumer());
    }

    /**
     * Closes the attached reader
     * @throws IOException In case of error while closing.
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }
}

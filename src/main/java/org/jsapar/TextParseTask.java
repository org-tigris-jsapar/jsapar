package org.jsapar;

import org.jsapar.parse.*;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Reader;

/**
 * This class is the starting point for parsing a text (like a text file). <br>
 * The instance of this class will produce events for each line that has been successfully parsed. <br/>
 * If you want to get the result back as a complete Document object, you should use the {@link DocumentBuilder} instead.
 * <br/>
 * <ol>
 * <li>First, create an instance of TextParseTask.</li>
 * <li>Add event listeners for parse events and error events</li>
 * <li>Call the {@link #execute()} method. You will receive a callback event for each line that is parsed.</li>
 * </ol>
 * <br/>
 *
 * @see DocumentBuilder
 * @see TextComposer
 * @see ConvertTask
*
 */
public class TextParseTask extends AbstractParseTask implements ParseTask {

    private final Schema schema;
    private final Reader reader;
    private final SchemaParserFactory parserFactory = new SchemaParserFactory();
    private final TextParseConfig     parseConfig;

    public TextParseTask(Schema schema, Reader reader) {
        this(schema, reader, new TextParseConfig());
    }

    public TextParseTask(Schema schema, Reader reader, TextParseConfig parseConfig) {
        this.schema = schema;
        this.reader = reader;
        this.parseConfig = parseConfig;
    }

    /**
     * Reads characters from the input and parses them into a Line. Once a Line is completed, a
     * LineParsedEvent is generated to all registered event listeners. If there is an error while
     * parsing a line, a CellErrorEvent or ErrorEvent is generated to all registered error event listeners <br>
     * Before calling this method you have to call {@link #setLineEventListener(LineEventListener)} to be able to handle the
     * result
     * @throws IOException If there is an error reading the input
     */
    @Override
    public void execute() throws IOException {
        parserFactory.makeSchemaParser(this.schema, reader, parseConfig).parse(this, this);
    }

}

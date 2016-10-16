package org.jsapar;

import org.jsapar.parse.*;

import java.io.IOException;
import java.io.Reader;

/**
 * This class is the starting point for parsing a text (like a text file). <br>
 * The instance of this class will produce events for each line that has been successfully parsed. <br/>
 * If you want to get the result back as a complete Document object, you should use the {@link DocumentBuilder} instead.
 * <br/>
 * <ol>
 * <li>First, create an instance of TextParser.</li>
 * <li>Add event listeners for parse events and error events</li>
 * <li>Call the parse() method. You will receive a callback event for each line that is parsed.</li>
 * </ol>
 * <br/>
 *
 * @see DocumentBuilder
 * @see TextComposer
 * @see Converter
*
 * Created by stejon0 on 2016-08-14.
 */
public class TextParser extends AbstractParser implements Parser {

    private final ParseSchema schema;
    private Reader reader;
    private SchemaParserFactory parserFactory = new SchemaParserFactory();
    private ParseConfig parseConfig = new ParseConfig();

    public TextParser(ParseSchema schema, Reader reader) {
        this.schema = schema;
        this.reader = reader;
    }

    /**
     * Reads characters from the input and parses them into a Line. Once a Line is completed, a
     * LineParsedEvent is generated to all registered event listeners. If there is an error while
     * parsing a line, a CellErrorEvent or ErrorEvent is generated to all registered error event listeners <br>
     * Before calling this method you have to call {@link #addLineEventListener(LineEventListener)} to be able to handle the
     * result<br>
     * If there is an error reading the input or an error at the structure of the input, a
     * JSaParException will be thrown.
     * @throws JSaParException
     * @throws IOException
     */
    @Override
    public void parse() throws JSaParException, IOException {
        parserFactory.makeSchemaParser(this.schema, reader, parseConfig).parse(this, this);
    }

    public ParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(ParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
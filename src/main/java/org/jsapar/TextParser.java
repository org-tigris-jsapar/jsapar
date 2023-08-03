package org.jsapar;

import org.jsapar.model.Line;
import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineEventListenerLineConsumer;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.schema.Schema;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This class is the starting point for parsing a text (like a text file). <br>
 * The instance of this class will produce events for each line that has been successfully parsed.
 * <p>
 * If you want to get the result back as a complete Document object, you should use
 * the {@link org.jsapar.parse.DocumentBuilderLineConsumer} as line consumer.
 * <ol>
 * <li>First, create an instance of TextParser with the {@link Schema} that you want to use while parsing.</li>
 * <li>Call the {@link #parseForEach(Reader, Consumer)} method. </li>
 * <li>The supplied {@link Consumer} will be called each line that is parsed.</li>
 * </ol>
 * <p>
 * The default error handling is to throw an exception upon the first error that occurs. You can however change that
 * behavior by adding a error {@link Consumer}. There are several implementations to choose from such as
 * {@link org.jsapar.parse.CollectingConsumer} or
 * {@link org.jsapar.error.ThresholdCollectingErrorConsumer}, or you may implement your own.
 *
 * @see TextComposer
 * @see Text2TextConverter
 * @see TextParseTask
 */
public class TextParser extends AbstractParser {

    private final Schema<?>          parseSchema;
    private       TextParseConfig parseConfig;

    public TextParser(Schema<?> parseSchema) {
        this(parseSchema, new TextParseConfig());
    }

    public TextParser(Schema<?> parseSchema, TextParseConfig parseConfig) {
        this.parseSchema = parseSchema;
        this.parseConfig = parseConfig;
    }

    /**
     * Reads text from supplied reader and parses each line. Each parsed line generates a call-back to the lineEventListener.
     * <p>
     * Deprecated since 2.2. Use {@link #parseForEach(Reader, Consumer)} instead.
     *
     * @param reader            The reader to read text from.
     * @param lineEventListener The call-back interface.
     * @return Number of parsed lines.
     * @throws IOException In case of IO error
     */
    @Deprecated(since = "2.2")
    public long parse(Reader reader, LineEventListener lineEventListener) throws IOException {
        return parseForEach(reader, new LineEventListenerLineConsumer(lineEventListener));
    }

    /**
     * Reads text from supplied reader and parses each line. Each parsed line generates a call-back to the lineConsumer.
     *
     * @param reader       The reader to read text from.
     * @param lineConsumer The line consumer that will be called for each line.
     * @return Number of parsed lines.
     * @since 2.2
     * @throws IOException In case of IO error
     */
    public long parseForEach(Reader reader, Consumer<Line> lineConsumer) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);
        return execute(parseTask, lineConsumer);
    }

    /**
     * Returns a stream of lines that are lazily populated by lines when pulled from the stream. The reader is consumed
     * on the fly upon pulling items from the stream.
     * <br/>
     * This method is particularly efficient if you don't want to scan through the whole source since it will abort
     * parsing as soon as you stop pulling items from the stream.
     * @param reader The reader to parse from.
     * @return a stream of lines that are lazily populated by lines when pulled from the stream. The order of the stream is according to the order lines were parsed from the reader.
     * @since 2.3
     * @throws IOException If there is an error reading from the input reader.
     */
    public Stream<Line> stream(Reader reader) throws IOException {
        TextSchemaParser parser = TextSchemaParser.ofSchema(parseSchema, reader, getParseConfig());
        return parser.stream(getErrorConsumer());
    }


    /**
     * Reads text from supplied reader and parses each line. Each parsed line generates a call-back to the lineConsumer.
     * <br/>
     * Convenience method that both create a parser instance and performs the parsing in one call. This method can only be used
     * if there are no requirements to configure anything apart from the default behavior.
     *
     * @param schema       The schema to use for parsing.
     * @param reader       The reader to read text from.
     * @param lineConsumer The line consumer that will be called for each line.
     * @return The total number of parsed lines, including header lines.
     * @see #parseForEach(Reader, Consumer)
     * @since 2.2
     * @throws IOException In case of IO error
     */
    public static long parseForEach(Schema<?> schema, Reader reader, Consumer<Line> lineConsumer) throws IOException {
        TextParser parser = new TextParser(schema);
        return parser.parseForEach(reader, lineConsumer);
    }

    /**
     * Gets the current {@link TextParseConfig} for this parser. Makes it possible to change each specific configuration
     * value.
     *
     * @return The current parse configuration for this parser.
     */
    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    /**
     * Replaces the current parse configuration
     *
     * @param parseConfig The new parse config
     */
    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
